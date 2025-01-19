package com.subtitlescorrector.service.processors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleUnitData;

@Service
public class HtmlStripPreProcessor implements PreProcessor{

	@Override
	public SubtitleFileData process(SubtitleFileData data) {
		
		Safelist safeList = new Safelist();
		safeList.addTags("b", "br", "i", "font")
			    .addAttributes("font", "color", "face", "size");
		
		OutputSettings outputSettings = new OutputSettings();
		outputSettings.prettyPrint(false);
		
		for(SubtitleUnitData subUnitData : data.getLines()) {
			
			String text = Jsoup.clean(subUnitData.getText(), "", safeList, outputSettings);
			subUnitData.setText(text);
			
		}
		
		return data;
	}

}
