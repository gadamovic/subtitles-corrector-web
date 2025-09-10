package com.subtitlescorrector.core.service.corrections;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.SubtitleUnitData;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;


public abstract class AbstractCorrector {

	@Autowired
	WebSocketMessageSender webSocketMessageSender;
	
	@Autowired
	ApplicationProperties properties;
	
	private static final Logger log = LoggerFactory.getLogger(AbstractCorrector.class);
	
	public abstract void correct(SubtitleUnitData subUnit, AdditionalData params, float processedPercentage);

	public void process(SubtitleUnitData subUnit, AdditionalData params) {
		
		float processedPercentage = ((float) params.getProcessedLines() / (float) params.getTotalNumberOfLines()) * 100;
		
		correct(subUnit, params, processedPercentage);
				
	}
	
	public String checkForChanges(String afterCorrection, String beforeCorrection, String correctionDescription,
			float processedPercentage) {
		if(!afterCorrection.equals(beforeCorrection)) {
			
			log.info("Before correction: " + beforeCorrection);
			log.info("After correction : " + afterCorrection);
			
			SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
			event.setCorrection(correctionDescription);
			event.setEventTimestamp(Instant.now());

			event.setProcessedPercentage(String.valueOf(processedPercentage));
						
			if(properties.getSubtitlesRealTimeUpdatesEnabled()) {
				webSocketMessageSender.sendMessage(event);
			}
			
		}

		return afterCorrection;
	}
	
}
