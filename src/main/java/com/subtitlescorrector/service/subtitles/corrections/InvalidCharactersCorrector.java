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
	Util util;
	
	@Override
	public SubtitleFileData correct(SubtitleFileData data, AdditionalData params) {

		int numberOfLines = data.getLines().size() * params.getNumberOfCorrectors();
		int currentLineNumber = data.getLines().size() * (params.getCorrectorIndex() - 1) + 1;
		float processedPercentage = 0f;

		String webSocketSessionId = params.getWebSocketSessionId();
		
		for(SubtitleUnitData subUnitData : data.getLines()) {
			
			String line = subUnitData.getText();
			
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
			
			if(params.getConvertAeToTj()) {
				tmp = beforeCorrection.replace("æ", "ć");
				beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "æ -> ć", processedPercentage, webSocketSessionId);
			}
			
			if(params.getConvertAEToTJ()) {			
				tmp = beforeCorrection.replace("Æ", "Ć");
				beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Æ -> Ć", processedPercentage, webSocketSessionId);
			}
			
			if(params.getConverteToch()) {
				tmp = beforeCorrection.replace("è", "č");
				beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "è -> č", processedPercentage, webSocketSessionId);
			}
			
			if(params.getConvertEToCH()) {
				tmp = beforeCorrection.replace("È", "Č");
				beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "È -> Č", processedPercentage, webSocketSessionId);
			}
			
			subUnitData.setText(beforeCorrection);
			
			currentLineNumber ++;
		}
		
		return data;
	}

	@Override
	public void correct(SubtitleUnitData subUnit, AdditionalData params) {
		
		String webSocketSessionId = params.getWebSocketSessionId();
		float processedPercentage = 0f;
		processedPercentage = ((float) params.getProcessedLines() / (float) params.getTotalNumberOfLines()) * 100;
		

		String line = subUnit.getText();
					
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
		
		if(params.getConvertAeToTj()) {
			tmp = beforeCorrection.replace("æ", "ć");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "æ -> ć", processedPercentage, webSocketSessionId);
		}
		
		if(params.getConvertAEToTJ()) {			
			tmp = beforeCorrection.replace("Æ", "Ć");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Æ -> Ć", processedPercentage, webSocketSessionId);
		}
		
		if(params.getConverteToch()) {
			tmp = beforeCorrection.replace("è", "č");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "è -> č", processedPercentage, webSocketSessionId);
		}
		
		if(params.getConvertEToCH()) {
			tmp = beforeCorrection.replace("È", "Č");
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "È -> Č", processedPercentage, webSocketSessionId);
		}
		
		subUnit.setText(beforeCorrection);

	}
	
}
