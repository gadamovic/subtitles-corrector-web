package com.subtitlescorrector.core.service.corrections.srt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.ai.LineForAiCorrection;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleUnitData;

@Service
public class SrtFileSubtitleDataToLinesForAiCorrectionAdapter {

	public List<LineForAiCorrection> adapt(SrtSubtitleFileData data){
		
		List<LineForAiCorrection> lines = new ArrayList<>();
		
		for(SrtSubtitleUnitData srtSubUnitData : data.getLines()) {
			
			LineForAiCorrection line = new LineForAiCorrection();
			line.setNumber(srtSubUnitData.getNumber().toString());
			line.setText(srtSubUnitData.getText());
			lines.add(line);
			
		}
		
		return lines;
	}
	
}
