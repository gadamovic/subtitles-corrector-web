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
		
		String line = subUnit.getText();
					
		String tmp = "";
		String beforeCorrection = line;
		
		//NOTE: there are characters in this empty-looking quotes, but some editors doesn't print them
		tmp = line.replace("", "ž");
		beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> ž", processedPercentage);
		
		tmp = beforeCorrection.replace("", "Ž");
		beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> Ž", processedPercentage);
		
		tmp = beforeCorrection.replace("", "š");
		beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> š", processedPercentage);
		
		tmp = beforeCorrection.replace("", "Š");
		beforeCorrection = checkForChanges(tmp, beforeCorrection, "\"\" -> Š", processedPercentage);

		tmp = beforeCorrection.replace("", "");
		beforeCorrection = checkForChanges(tmp, beforeCorrection, "Removed \"\"", processedPercentage);
		
		tmp = beforeCorrection.replace("", "");
		beforeCorrection = checkForChanges(tmp, beforeCorrection, "Removed \"\"", processedPercentage);
		
		if(params.getConvertAeToTj()) {
			tmp = beforeCorrection.replace("æ", "ć");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "æ -> ć", processedPercentage);
		}
		
		if(params.getConvertAEToTJ()) {			
			tmp = beforeCorrection.replace("Æ", "Ć");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "Æ -> Ć", processedPercentage);
		}
		
		if(params.getConverteToch()) {
			tmp = beforeCorrection.replace("è", "č");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "è -> č", processedPercentage);
		}
		
		if(params.getConvertEToCH()) {
			tmp = beforeCorrection.replace("È", "Č");
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "È -> Č", processedPercentage);
		}
		
		subUnit.setText(beforeCorrection);

	}
	
}
