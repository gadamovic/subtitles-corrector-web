package com.subtitlescorrector.core.service.corrections.vtt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.EditOperation;
import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.domain.ai.LineForAiCorrection;
import com.subtitlescorrector.core.port.SubtitlesCloudStoragePort;
import com.subtitlescorrector.core.service.EditDistanceService;
import com.subtitlescorrector.core.service.converters.SubtitlesConverterFactory;
import com.subtitlescorrector.core.service.corrections.AbstractCorrector;
import com.subtitlescorrector.core.service.corrections.AdditionalDataToCorrectorParametersAdapter;
import com.subtitlescorrector.core.service.corrections.AiCustomCorrector;
import com.subtitlescorrector.core.service.corrections.CorrectorsManager;
import com.subtitlescorrector.core.service.corrections.SubtitlesFileProcessor;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleUnitData;
import com.subtitlescorrector.core.service.corrections.vtt.domain.VttSubtitleFileData;
import com.subtitlescorrector.core.service.corrections.vtt.domain.VttSubtitleUnitData;
import com.subtitlescorrector.core.service.preprocessing.PreProcessor;
import com.subtitlescorrector.core.service.preprocessing.PreProcessorsManager;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.Util;


@Component
public class VttSubtitlesFileProcessor{

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
	VttSubtitleLinesToSubtitleUnitDataConverter converter;
	
	@Autowired
	UserData user;
	
	@Autowired
	AdditionalDataToCorrectorParametersAdapter adapter;
	
	@Autowired
	VttFileSubtitleDataToLinesForAiCorrectionAdapter aiAdapter;
	
	@Autowired
	public void setS3Service(SubtitlesCloudStoragePort s3Service) {
		this.s3Service = s3Service;
	}

	private static final Logger log = LoggerFactory.getLogger(VttSubtitlesFileProcessor.class);
	
	public VttSubtitleFileData process(File storedFile, List<String> lines, AdditionalData params, BomData bomData) {

		VttSubtitleFileData data = new VttSubtitleFileData();
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
				List<String> tmp = preProcessor.process(data.getLines().stream().map(VttSubtitleUnitData::getText).collect(Collectors.toList()));
				for(int i = 0; i < tmp.size(); i++) {
					VttSubtitleUnitData vttData = data.getLines().get(i);
					vttData.setText(tmp.get(i));
				}
			}
			
			data.getLines().forEach(line -> line.setTextBeforeCorrection(line.getText()));
			 					         //TODO: Can correctorsManager be a factory?			
			List<AbstractCorrector> correctors = correctorsManager.getCorrectors(params);
			
			params.setTotalNumberOfLines(data.getLines().size());
						
			for(VttSubtitleUnitData subUnit : data.getLines()) {
				for(AbstractCorrector corrector : correctors) {
					
					subUnit.setText(corrector.process(subUnit.getText(), adapter.adapt(params)));
					
				}
				params.setProcessedLines(params.getProcessedLines() + 1);
			}
			
			if (detectedEncoding != StandardCharsets.UTF_8) {
				sendUpdatedEncodingLogMessage(storedFile, detectedEncoding);
			}
			
			if (params.getAiEnabled()) {
				List<LineForAiCorrection> corrected = aiCorrector.correct(aiAdapter.adapt(data), adapter.adapt(params));
				
				// Put back corrected lines to VttSubtitleUnitData
				List<VttSubtitleUnitData> uncorrected = data.getLines();
				for(int i = 0; i < uncorrected.size(); i++) {
					uncorrected.get(i).setText(corrected.get(i).getText());
				}
				
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

	private void populateSubtitleFileData(AdditionalData params, VttSubtitleFileData data, File subtitleFile,
			Charset detectedEncoding, List<String> lines, BomData bomData) {
		data.setDetectedCharset(detectedEncoding);
		data.setFilename(subtitleFile.getName());
		data.setLines(converter.convertToSubtitleUnits(lines));
		data.setBomData(bomData);
	}

	private void calculateEditOperationsAfterCorrections(VttSubtitleFileData data) {
		for(VttSubtitleUnitData subData : data.getLines()) {
			
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
