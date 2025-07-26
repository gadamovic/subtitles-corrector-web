package com.subtitlescorrector.service.subtitles;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.controller.rest.FileUploadController;
import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.EditOperation;
import com.subtitlescorrector.domain.S3BucketNames;
import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleFormat;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.service.CustomWebSocketHandler;
import com.subtitlescorrector.service.preprocessors.PreProcessor;
import com.subtitlescorrector.service.preprocessors.PreProcessorsManager;
import com.subtitlescorrector.service.s3.S3Service;
import com.subtitlescorrector.service.subtitles.corrections.AiCustomCorrector;
import com.subtitlescorrector.service.subtitles.corrections.AbstractCorrector;
import com.subtitlescorrector.service.subtitles.corrections.CorrectorsManager;
import com.subtitlescorrector.util.Constants;
import com.subtitlescorrector.util.FileUtil;
import com.subtitlescorrector.util.Util;

@Component
public class SubtitlesFileProcessorImpl implements SubtitlesFileProcessor {

	@Autowired
	ApplicationProperties properties;

	//KafkaTemplate<Void, SubtitleCorrectionEvent> kafkaTemplate;

	@Autowired
	CorrectorsManager correctorsManager;
	
	@Autowired
	PreProcessorsManager preProcessorsManager;

	S3Service s3Service;

	@Autowired
	Util util;
	
	@Autowired
	EditDistanceService levenshteinDistance;
	
	@Autowired
	CustomWebSocketHandler webSocketHandler;
	
	@Autowired
	AiCustomCorrector aiCorrector;

	@Autowired
	private SubtitlesConverterFactory converterFactory;
	
//	@Autowired
//	public void setKafkaTemplate(KafkaTemplate<Void, SubtitleCorrectionEvent> kafkaTemplate) {
//		this.kafkaTemplate = kafkaTemplate;
//	}

	@Autowired
	public void setS3Service(S3Service s3Service) {
		this.s3Service = s3Service;
	}

	private static final Logger log = LoggerFactory.getLogger(SubtitlesFileProcessorImpl.class);
	
	@Override
	public SubtitleFileData process(File storedFile, AdditionalData params) {

		SubtitleFileData data = new SubtitleFileData();
		File correctedFile = new File(storedFile.getName());

		try {
			
			String s3Key = params.getWebSocketSessionId() + "_" + storedFile.getName();
			s3Service.uploadFileToS3IfProd("v1_" + s3Key, S3BucketNames.SUBTITLES_UPLOADED_FILES.getBucketName(), storedFile, "ttl=7days");
			
			Charset detectedEncoding = FileUtil.detectEncodingOfFile(storedFile);
			
			List<String> lines = FileUtil.loadTextFile(storedFile);
			
			handleBOM(params, data, lines);
			populateSubtitleFileData(params, data, correctedFile, detectedEncoding, lines);
			
			//preprocessors are not considered as subtitle corrections. Corrections will be reported in <AppliedChanges> section, but not in the editor's content
			//this are preparations of the content of the uploaded file. For example we don't want to keep any non-srt-relevant HTML
			//because of security and show it in the editor (either as text before correction or after correction) where html is not escaped
			for (PreProcessor preProcessor : preProcessorsManager.getPreProcessors()) {
				data = preProcessor.process(data, params);
			}
			
			data.getLines().forEach(line -> line.setTextBeforeCorrection(line.getText()));
			 					         //TODO: Can correctorsManager be a factory?			
			List<AbstractCorrector> correctors = correctorsManager.getCorrectors(params);
			
			params.setTotalNumberOfLines(data.getLines().size());
						
			for(SubtitleUnitData subUnit : data.getLines()) {
				for(AbstractCorrector corrector : correctors) {
					
					corrector.process(subUnit, params);
					
				}
				params.setProcessedLines(params.getProcessedLines() + 1);
			}
			
			if (detectedEncoding != StandardCharsets.UTF_8) {
				sendUpdatedEncodingLogMessage(storedFile, params.getWebSocketSessionId(), detectedEncoding);
			}
			
			if (params.getAiEnabled()) {
				aiCorrector.correct(data, params);
			}
			
			calculateEditOperationsAfterCorrections(data);

			sendProcessingFinishedMessage(params.getWebSocketSessionId());
			
			FileUtil.writeLinesToFile(correctedFile, converterFactory.getConverter(data.getFormat()).convertToListOfStrings(data.getLines()), StandardCharsets.UTF_8);

			s3Service.uploadFileToS3IfProd("v2_" + s3Key, S3BucketNames.SUBTITLES_UPLOADED_FILES.getBucketName(), correctedFile, "ttl=7days");

		}catch (Exception e) {
			log.error("Error processing file!", e);
		} finally {
			deleteFiles(storedFile, correctedFile);
		}

		return data;
	}

	private void populateSubtitleFileData(AdditionalData params, SubtitleFileData data, File correctedFile,
			Charset detectedEncoding, List<String> lines) {
		SubtitleFormat format = Util.detectSubtitleFormat(lines);
		data.setFormat(format);
		data.setDetectedCharset(detectedEncoding);
		data.setFilename(correctedFile.getName());
		data.setLines(converterFactory.getConverter(format).convertToSubtitleUnits(lines, params));
	}

	private void handleBOM(AdditionalData params, SubtitleFileData data, List<String> lines) {

		if (lines.get(0).startsWith("\uFEFF")) {
			
			// Remove BOM
			String line = lines.get(0).substring(1);
			lines.remove(0);
			lines.add(0, line);
			
			data.setHasBom(true);
			if(params.getKeepBOM()) {
				data.setKeepBom(true);
			}else {
				data.setKeepBom(false);
			}
		} else {
			data.setHasBom(false);
			data.setKeepBom(false);
		}

	}

	private void calculateEditOperationsAfterCorrections(SubtitleFileData data) {
		for(SubtitleUnitData subData : data.getLines()) {
			
			String original = subData.getTextBeforeCorrection();
			String corrected = subData.getText();
			
			List<EditOperation> operations = levenshteinDistance.getEditOperations(original, corrected);
			
			subData.setCompEditOperations(Util.convertToCompositeEditOperations(operations));
			
		}
	}

	private void deleteFiles(File storedFile, File correctedFile) {
		try {
			
			if(storedFile.exists()) {
				Files.delete(storedFile.toPath());
			}
			
			if(correctedFile.exists()) {
				Files.delete(correctedFile.toPath());
			}
			
		} catch (IOException e) {
			log.error("Error deleting files!", e);
		}
	}

	private void sendUpdatedEncodingLogMessage(File storedFile, String webSocketSessionId, Charset detectedEncoding) {
		log.info("Updated encoding of: {} to UTF-8!", storedFile.getName());

		SubtitleCorrectionEvent encodingUpdate = new SubtitleCorrectionEvent();
		encodingUpdate.setCorrection("Encoding updated: " + detectedEncoding + " -> UTF-8");
		encodingUpdate.setWebSocketSessionId(webSocketSessionId);
		//kafkaTemplate.send(Constants.SUBTITLES_CORRECTIONS_TOPIC_NAME, encodingUpdate);
		webSocketHandler.sendMessage(encodingUpdate);
	}

	private void sendProcessingFinishedMessage(String webSocketSessionId) {
		
		//wait a bit to be sure that this is the last progress update message
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {}
		
		SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
		event.setWebSocketSessionId(webSocketSessionId);
		event.setProcessedPercentage("100");
		//kafkaTemplate.send(Constants.SUBTITLES_CORRECTIONS_TOPIC_NAME, event);
		webSocketHandler.sendMessage(event);

	}
	
}
