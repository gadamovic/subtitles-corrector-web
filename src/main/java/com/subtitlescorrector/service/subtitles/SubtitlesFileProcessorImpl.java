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
import org.springframework.stereotype.Component;

import com.subtitlescorrector.controller.rest.FileUploadController;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.producers.SubtitleCorrectionEventProducer;
import com.subtitlescorrector.util.FileUtil;

@Component
public class SubtitlesFileProcessorImpl implements SubtitlesFileProcessor {

	Logger log = LoggerFactory.getLogger(FileUploadController.class);
	
	@Autowired
	SubtitleCorrectionEventProducer producer;
	
	@Override
	public File process(File storedFile, String s3KeyUUIDPrefix) {
		
		Charset detectedEncoding = FileUtil.detectEncodingOfFile(storedFile);
		List<String> lines = FileUtil.loadTextFile(storedFile);
		List<String> correctedLines = new ArrayList<>();
		
		for(String line : lines) {
			
			String tmp = "";
			String beforeCorrection = line;
			
			tmp = line.replace("", "ž");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "\"\" -> ž", detectedEncoding);
			
			tmp = beforeCorrection.replace("", "Ž");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "\"\" -> Ž", detectedEncoding);
			
			tmp = beforeCorrection.replace("", "š");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "\"\" -> š", detectedEncoding);
			
			tmp = beforeCorrection.replace("", "Š");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "\"\" -> Š", detectedEncoding);
	
			tmp = beforeCorrection.replace("æ", "ć");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "æ -> Š", detectedEncoding);
			
			tmp = beforeCorrection.replace("Æ", "Ć");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "Æ -> Š", detectedEncoding);
	
			tmp = beforeCorrection.replace("è", "č");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "è -> Š", detectedEncoding);
			
			tmp = beforeCorrection.replace("È", "Č");
			beforeCorrection = checkForChanges(s3KeyUUIDPrefix + storedFile.getName(), tmp, beforeCorrection, "È -> Č", detectedEncoding);
			
			correctedLines.add(beforeCorrection);
		}
		
		File correctedFile = new File(storedFile.getName());
		
		if(detectedEncoding != StandardCharsets.UTF_8) {
			log.info("Updated encoding of: {} to UTF-8!", storedFile.getName());
		}
		
		FileUtil.writeLinesToFile(correctedFile, correctedLines, StandardCharsets.UTF_8);
		return correctedFile;
	}

	private String checkForChanges(String s3Key, String afterCorrection, String beforeCorrection, String correctionDescription, Charset detectedEncoding) {
		if(!afterCorrection.equals(beforeCorrection)) {
			log.info("Correction applied: " + correctionDescription);
			
			SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
			event.setCorrection(correctionDescription);
			event.setEventTimestamp(Instant.now());
			event.setDetectedEncoding(detectedEncoding.displayName());
			event.setFileId(s3Key);

			producer.generateCorrectionEvent(event);
			
		}

		return afterCorrection;
	}

}
