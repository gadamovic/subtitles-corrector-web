package com.subtitlescorrector.core.service.corrections.vtt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.ai.LineForAiCorrection;
import com.subtitlescorrector.core.domain.vtt.VttSubtitleFileData;
import com.subtitlescorrector.core.domain.vtt.VttSubtitleUnitData;

@Service
public class VttFileSubtitleDataToLinesForAiCorrectionAdapter {

	public List<LineForAiCorrection> adapt(VttSubtitleFileData data){
		
		List<LineForAiCorrection> lines = new ArrayList<>();
		
		for(VttSubtitleUnitData vttSubUnitData : data.getLines()) {
			
			LineForAiCorrection line = new LineForAiCorrection();
			line.setNumber(vttSubUnitData.getNumber().toString());
			line.setText(vttSubUnitData.getText());
			lines.add(line);
			
		}
		
		return lines;
	}
	
}
