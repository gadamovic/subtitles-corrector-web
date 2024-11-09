package com.subtitlescorrector.service.subtitles;

import java.io.File;
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
	public File process(File storedFile) {
		
		List<String> lines = FileUtil.loadTextFile(storedFile);
		List<String> correctedLines = new ArrayList<>();
		
		for(String line : lines) {
			
			String tmp = "";
			String beforeCorrection = line;
			
			tmp = line.replace("", "ž");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> ž");
			
			tmp = beforeCorrection.replace("", "Ž");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> Ž");
			
			tmp = beforeCorrection.replace("", "š");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> š");
			
			tmp = beforeCorrection.replace("", "Š");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> Š");
	
			tmp = beforeCorrection.replace("æ", "ć");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "æ -> Š");
			
			tmp = beforeCorrection.replace("Æ", "Ć");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "Æ -> Š");
	
			tmp = beforeCorrection.replace("è", "č");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "è -> Š");
			
			tmp = beforeCorrection.replace("È", "Č");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "È -> Č");
			
			correctedLines.add(beforeCorrection);
		}
		
		File correctedFile = new File(storedFile.getName());
		FileUtil.writeLinesToFile(correctedFile, correctedLines, StandardCharsets.UTF_8);
		return correctedFile;
	}

	private String checkForChanges(String afterCorrection, String beforeCorrection, String correctionDescription) {
		if(!afterCorrection.equals(beforeCorrection)) {
			log.info("Correction applied: " + correctionDescription);
			
			SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
			event.setCorrection(correctionDescription);
			event.setEventTimestamp(Instant.now());
			//event.setDetectedEncoding("UTF-8");

			producer.generateCorrectionEvent(event);
			
		}

		return afterCorrection;
	}

}
