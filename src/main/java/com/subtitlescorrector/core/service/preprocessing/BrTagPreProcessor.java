package com.subtitlescorrector.core.service.preprocessing;

import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SubtitleFileData;
import com.subtitlescorrector.core.domain.SubtitleUnitData;

@Service
public class BrTagPreProcessor implements PreProcessor{

	@Override
	public SubtitleFileData process(SubtitleFileData data, AdditionalData params) {
		
		for(SubtitleUnitData subUnitData : data.getLines()) {
			
			String text = subUnitData.getText().replace("\n", "<br>");
			subUnitData.setText(text);
			
		}
		
		return data;
	}

}
