package com.subtitlescorrector.core.service.corrections.ass;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.ai.LineForAiCorrection;
import com.subtitlescorrector.core.domain.ass.AssSubtitleFileData;
import com.subtitlescorrector.core.domain.ass.AssSubtitleUnitData;

@Service
public class AssFileSubtitleDataToLinesForAiCorrectionAdapter {

	public List<LineForAiCorrection> adapt(AssSubtitleFileData data){
		
		List<LineForAiCorrection> lines = new ArrayList<>();
		
		for(AssSubtitleUnitData srtSubUnitData : data.getLines()) {
			
			LineForAiCorrection line = new LineForAiCorrection();
			line.setNumber(srtSubUnitData.getNumber().toString());
			line.setText(srtSubUnitData.getText());
			lines.add(line);
			
		}
		
		return lines;
	}
	
}
