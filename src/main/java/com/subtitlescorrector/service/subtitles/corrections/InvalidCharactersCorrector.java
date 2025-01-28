package com.subtitlescorrector.service.subtitles.corrections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.util.Util;

@Service
public class InvalidCharactersCorrector implements Corrector{

	Logger log = LoggerFactory.getLogger(InvalidCharactersCorrector.class);
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	Util util;
	
	@Override
	public SubtitleFileData correct(SubtitleFileData data, AdditionalData additionalData) {

		int numberOfLines = data.getLines().size() * additionalData.getNumberOfCorrectors();
		int currentLineNumber = data.getLines().size() * (additionalData.getCorrectorIndex() - 1) + 1;
		float processedPercentage = 0f;

		String webSocketSessionId = additionalData.getWebSocketSessionId();
		
		for(SubtitleUnitData subUnitData : data.getLines()) {
			
			String line = subUnitData.getText();
			//TODO: Check if this can be moved outside of correctors, or make sure it's added only on the first one
			subUnitData.setTextBeforeCorrection(line);
			
			processedPercentage = ((float) currentLineNumber / (float) numberOfLines) * 100;
			
			String tmp = "";
			String beforeCorrection = line;
			
			//NOTE: there are characters in this empty-looking quotes, but some editors doesn't print them
			tmp = line.replace("", "ž");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "\"\" -> ž", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("", "Ž");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "\"\" -> Ž", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("", "š");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "\"\" -> š", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("", "Š");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "\"\" -> Š", processedPercentage, webSocketSessionId);
	
			tmp = beforeCorrection.replace("", "");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Removed \"\"", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("", "");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Removed \"\"", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("æ", "ć");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "æ -> ć", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("Æ", "Ć");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Æ -> Ć", processedPercentage, webSocketSessionId);
	
			tmp = beforeCorrection.replace("è", "č");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "è -> č", processedPercentage, webSocketSessionId);
			
			tmp = beforeCorrection.replace("È", "Č");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "È -> Č", processedPercentage, webSocketSessionId);
			
			subUnitData.setText(beforeCorrection);
			
			currentLineNumber ++;
		}
		
		return data;
	}
	
}
