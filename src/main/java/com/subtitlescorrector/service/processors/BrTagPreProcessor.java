package com.subtitlescorrector.service.processors;

import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleUnitData;

@Service
public class BrTagPreProcessor implements PreProcessor{

	@Override
	public SubtitleFileData process(SubtitleFileData data) {
		
		for(SubtitleUnitData subUnitData : data.getLines()) {
			
			String text = subUnitData.getText().replace("\n", "<br>");
			subUnitData.setText(text);
			
		}
		
		return data;
	}

}
