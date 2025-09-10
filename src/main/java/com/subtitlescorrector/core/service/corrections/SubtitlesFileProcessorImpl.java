package com.subtitlescorrector.core.service.corrections;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.EditOperation;
import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.SubtitleFileData;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.SubtitleUnitData;
import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.port.SubtitlesCloudStoragePort;
import com.subtitlescorrector.core.service.EditDistanceService;
import com.subtitlescorrector.core.service.converters.SubtitlesConverterFactory;
import com.subtitlescorrector.core.service.preprocessing.PreProcessor;
import com.subtitlescorrector.core.service.preprocessing.PreProcessorsManager;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.Util;


@Component
public class SubtitlesFileProcessorImpl implements SubtitlesFileProcessor {

	@Autowired
	ApplicationProperties properties;

	@Autowired
	CorrectorsManager correctorsManager;
	
	@Autowired
	PreProcessorsManager preProcessorsManager;

	SubtitlesCloudStoragePort s3Service;

	@Autowired
	Util util;
	
	@Autowired
	EditDistanceService levenshteinDistance;
	
	@Autowired
	WebSocketMessageSender webSocketMessageSender;
	
	@Autowired
	AiCustomCorrector aiCorrector;

	@Autowired
	private SubtitlesConverterFactory converterFactory;
	
	@Autowired
	UserData user;
	
	@Autowired
	public void setS3Service(SubtitlesCloudStoragePort s3Service) {
		this.s3Service = s3Service;
	}

	private static final Logger log = LoggerFactory.getLogger(SubtitlesFileProcessorImpl.class);
	
	@Override
	public SubtitleFileData process(File storedFile, List<String> lines, AdditionalData params, BomData bomData) {

		SubtitleFileData data = new SubtitleFileData();
		File correctedFile = new File(storedFile.getName());

		try {
			
			String s3Key = user.getWebSocketSessionId() + "_" + storedFile.getName();
			s3Service.storeIfProd("v1_" + s3Key, storedFile);
			
			Charset detectedEncoding = FileUtil.detectEncodingOfFile(storedFile);
			
			populateSubtitleFileData(params, data, correctedFile, detectedEncoding, lines, bomData);
			
			//preprocessors are not considered as subtitle corrections. Corrections will be reported in <AppliedChanges> section, but not in the editor's content
			//this are preparations of the content of the uploaded file. For example we don't want to keep any non-srt-relevant HTML
			//because of security and show it in the editor (either as text before correction or after correction) where html is not escaped
			for (PreProcessor preProcessor : preProcessorsManager.getPreProcessors()) {
				data = preProcessor.process(data);
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
				sendUpdatedEncodingLogMessage(storedFile, detectedEncoding);
			}
			
			if (params.getAiEnabled()) {
				aiCorrector.correct(data, params);
			}
			
			calculateEditOperationsAfterCorrections(data);

			sendProcessingFinishedMessage();
			
		}catch (Exception e) {
			log.error("Error processing file!", e);
		} finally {
			deleteFiles(storedFile, correctedFile);
		}

		return data;
	}

	private void populateSubtitleFileData(AdditionalData params, SubtitleFileData data, File subtitleFile,
			Charset detectedEncoding, List<String> lines, BomData bomData) {
		SubtitleFormat format = Util.detectSubtitleFormat(lines);
		data.setFormat(format);
		data.setDetectedCharset(detectedEncoding);
		data.setFilename(subtitleFile.getName());
		data.setLines(converterFactory.getConverter(format).convertToSubtitleUnits(lines));
		data.setBomData(bomData);
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

	private void sendUpdatedEncodingLogMessage(File storedFile, Charset detectedEncoding) {
		log.info("Updated encoding of: {} to UTF-8!", storedFile.getName());

		SubtitleCorrectionEvent encodingUpdate = new SubtitleCorrectionEvent();
		encodingUpdate.setCorrection("Encoding updated: " + detectedEncoding + " -> UTF-8");
		webSocketMessageSender.sendMessage(encodingUpdate);
	}

	private void sendProcessingFinishedMessage() {
		
		//wait a bit to be sure that this is the last progress update message
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {}
		
		SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
		event.setProcessedPercentage("100");
		webSocketMessageSender.sendMessage(event);

	}
	
}
