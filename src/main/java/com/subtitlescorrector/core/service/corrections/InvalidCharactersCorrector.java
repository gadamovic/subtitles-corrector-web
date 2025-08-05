package com.subtitlescorrector.core.service.corrections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SubtitleUnitData;

@Service
public class InvalidCharactersCorrector extends AbstractCorrector{

	Logger log = LoggerFactory.getLogger(InvalidCharactersCorrector.class);

	@Override
	public void correct(SubtitleUnitData subUnit, AdditionalData params, float processedPercentage) {
		
		String webSocketSessionId = params.getWebSocketSessionId();
		
		String line = subUnit.getText();
					
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
		
		if(params.getConvertAeToTj()) {
			tmp = beforeCorrection.replace("æ", "ć");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "æ -> ć", processedPercentage, webSocketSessionId);
		}
		
		if(params.getConvertAEToTJ()) {			
			tmp = beforeCorrection.replace("Æ", "Ć");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "Æ -> Ć", processedPercentage, webSocketSessionId);
		}
		
		if(params.getConverteToch()) {
			tmp = beforeCorrection.replace("è", "č");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "è -> č", processedPercentage, webSocketSessionId);
		}
		
		if(params.getConvertEToCH()) {
			tmp = beforeCorrection.replace("È", "Č");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "È -> Č", processedPercentage, webSocketSessionId);
		}
		
		subUnit.setText(beforeCorrection);

	}
	
}
