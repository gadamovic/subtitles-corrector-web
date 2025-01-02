package com.subtitlescorrector.service.subtitles;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.controller.rest.FileUploadController;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.producers.SubtitleCorrectionEventProducer;
import com.subtitlescorrector.service.WebSocketMessageBrokerService;
import com.subtitlescorrector.util.Constants;
import com.subtitlescorrector.util.FileUtil;

@Component
public class SubtitlesFileProcessorImpl implements SubtitlesFileProcessor {

	Logger log = LoggerFactory.getLogger(FileUploadController.class);
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	WebSocketMessageBrokerService webSocketBrokerService;
	
	@Autowired
	KafkaTemplate<Void, SubtitleCorrectionEvent> kafkaTemplate;
	
	@Override
	public File process(File storedFile, String s3KeyUUIDPrefix, String webSocketSessionId) {
		
		Charset detectedEncoding = FileUtil.detectEncodingOfFile(storedFile);
		List<String> lines = FileUtil.loadTextFile(storedFile);
		List<String> correctedLines = new ArrayList<>();
		int numberOfLines = lines.size();
		int currentLineNumber = 0;
		float processedPercentage = 0f;
				
		for(String line : lines) {
			
			currentLineNumber ++;
			processedPercentage = ((float) currentLineNumber / (float) numberOfLines) * 100;
			
			String tmp = "";
			String beforeCorrection = line;
			
			tmp = line.replace("", "ž");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "\"\" -> ž", detectedEncoding, processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("", "Ž");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "\"\" -> Ž", detectedEncoding, processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("", "š");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "\"\" -> š", detectedEncoding, processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("", "Š");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "\"\" -> Š", detectedEncoding, processedPercentage, webSocketSessionId);
	
			tmp = beforeCorrection.replace("æ", "ć");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "æ -> Š", detectedEncoding, processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("Æ", "Ć");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "Æ -> Š", detectedEncoding, processedPercentage, webSocketSessionId);
	
			tmp = beforeCorrection.replace("è", "č");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "è -> Š", detectedEncoding, processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("È", "Č");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "È -> Č", detectedEncoding, processedPercentage, webSocketSessionId);
			
			correctedLines.add(beforeCorrection);
		}
		
		sendProcessingFinishedMessage(webSocketSessionId);
		
		File correctedFile = new File(storedFile.getName());
		
		if(detectedEncoding != StandardCharsets.UTF_8) {
			log.info("Updated encoding of: {} to UTF-8!", storedFile.getName());
		}
		
		FileUtil.writeLinesToFile(correctedFile, correctedLines, StandardCharsets.UTF_8);
		return correctedFile;
	}

	private void sendProcessingFinishedMessage(String webSocketSessionId) {
		
		//wait a bit so this is the last progress update message
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {}
		
		SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
		event.setWebSocketSessionId(webSocketSessionId);
		event.setProcessedPercentage("100");
		kafkaTemplate.send(Constants.SUBTITLES_CORRECTIONS_TOPIC_NAME, event);

	}

	private String checkForChanges(String s3Key, String afterCorrection, String beforeCorrection, String correctionDescription, Charset detectedEncoding, float processedPercentage, String webSocketSessionId) {
		if(!afterCorrection.equals(beforeCorrection)) {
			log.info("Correction applied: " + correctionDescription);
			
			SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
			event.setCorrection(correctionDescription);
			event.setEventTimestamp(Instant.now());
			event.setDetectedEncoding(detectedEncoding.displayName());
			event.setFileId(s3Key);
			event.setProcessedPercentage(String.valueOf(processedPercentage));
			event.setWebSocketSessionId(webSocketSessionId);
			
			if(properties.getSubtitlesKafakEnabled()) {
				kafkaTemplate.send(Constants.SUBTITLES_CORRECTIONS_TOPIC_NAME, event);
			}
			
		}

		return afterCorrection;
	}

}
