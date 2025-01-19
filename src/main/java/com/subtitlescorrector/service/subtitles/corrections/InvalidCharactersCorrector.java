package com.subtitlescorrector.service.subtitles.corrections;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.service.subtitles.EditDistanceService;
import com.subtitlescorrector.util.Constants;

@Service
public class InvalidCharactersCorrector implements Corrector{

	Logger log = LoggerFactory.getLogger(InvalidCharactersCorrector.class);
	
	@Autowired
	KafkaTemplate<Void, SubtitleCorrectionEvent> kafkaTemplate;
	
	@Autowired
	ApplicationProperties properties;
	
	@Override
	public SubtitleFileData correct(SubtitleFileData data, String webSocketSessionId) {

		int numberOfLines = data.getLines().size();
		int currentLineNumber = 0;
		float processedPercentage = 0f;
		
		for(SubtitleUnitData subUnitData : data.getLines()) {
			
			String line = subUnitData.getText();
			subUnitData.setTextBeforeCorrection(line);
			
			currentLineNumber ++;
			processedPercentage = ((float) currentLineNumber / (float) numberOfLines) * 100;
			
			String tmp = "";
			String beforeCorrection = line;
			
			//NOTE: there are characters in this empty-looking quotes, but some editors doesn't print them
			tmp = line.replace("", "ž");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> ž", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("", "Ž");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> Ž", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("", "š");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> š", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("", "Š");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> Š", processedPercentage, webSocketSessionId);
	
			tmp = beforeCorrection.replace("", "");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "Removed \"\"", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("", "");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "Removed \"\"", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("æ", "ć");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "æ -> ć", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("Æ", "Ć");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "Æ -> Ć", processedPercentage, webSocketSessionId);
	
			tmp = beforeCorrection.replace("è", "č");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "è -> č", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("È", "Č");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "È -> Č", processedPercentage, webSocketSessionId);
			
			subUnitData.setText(beforeCorrection);
		}
		
		sendProcessingFinishedMessage(webSocketSessionId);

		return data;
	}

	private String checkForChanges(String afterCorrection, String beforeCorrection, String correctionDescription, float processedPercentage, String webSocketSessionId) {
		if(!afterCorrection.equals(beforeCorrection)) {
			
			SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
			event.setCorrection(correctionDescription);
			event.setEventTimestamp(Instant.now());

			event.setProcessedPercentage(String.valueOf(processedPercentage));
			event.setWebSocketSessionId(webSocketSessionId);
						
			if(properties.getSubtitlesKafakEnabled()) {
				kafkaTemplate.send(Constants.SUBTITLES_CORRECTIONS_TOPIC_NAME, event);
			}
			
		}

		return afterCorrection;
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
	
}
