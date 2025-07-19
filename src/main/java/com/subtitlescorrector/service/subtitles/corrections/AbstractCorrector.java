package com.subtitlescorrector.service.subtitles.corrections;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.service.CustomWebSocketHandler;

public abstract class AbstractCorrector {

	@Autowired
	CustomWebSocketHandler webSocketHandler;
	
	@Autowired
	ApplicationProperties properties;
	
	private static final Logger log = LoggerFactory.getLogger(AbstractCorrector.class);
	
	public abstract void correct(SubtitleUnitData subUnit, AdditionalData params, float processedPercentage);

	public void process(SubtitleUnitData subUnit, AdditionalData params) {
		
		float processedPercentage = ((float) params.getProcessedLines() / (float) params.getTotalNumberOfLines()) * 100;
		
		correct(subUnit, params, processedPercentage);
				
	}
	
	public String checkForChanges(String afterCorrection, String beforeCorrection, String correctionDescription,
			float processedPercentage, String webSocketSessionId) {
		if(!afterCorrection.equals(beforeCorrection)) {
			
			log.info("Before correction: " + beforeCorrection);
			log.info("After correction : " + afterCorrection);
			
			SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
			event.setCorrection(correctionDescription);
			event.setEventTimestamp(Instant.now());

			event.setProcessedPercentage(String.valueOf(processedPercentage));
			event.setWebSocketSessionId(webSocketSessionId);
						
			if(properties.getSubtitlesRealTimeUpdatesEnabled()) {
				webSocketHandler.sendMessage(event);
			}
			
		}

		return afterCorrection;
	}
	
}
