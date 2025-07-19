package com.subtitlescorrector.service.subtitles.corrections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

@Service
public class HtmlTagsCorrector extends Corrector{

	Logger log = LoggerFactory.getLogger(HtmlTagsCorrector.class);
	
	@Autowired
	KafkaTemplate<Void, SubtitleCorrectionEvent> kafkaTemplate;

	@Override
	public void correct(SubtitleUnitData subUnitData, AdditionalData params, float processedPercentage) {
				
		String webSocketSessionId = params.getWebSocketSessionId();

		Safelist safeList = new Safelist();
		
		safeList.addTags("b", "br", "i", "u", "font")
		.addAttributes("font", "color", "face", "size");
		
		
		OutputSettings outputSettings = new OutputSettings();
		outputSettings.prettyPrint(false);
		
		
		String line = subUnitData.getText();
		String tmp = "";
		String beforeCorrection = line;
		
		safeList.addTags("b", "br", "i", "u", "font")
		.addAttributes("font", "color", "face", "size");
		
		if(params.getStripBTags()) {
			safeList.removeTags("b");
			
			tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "Removed <b> tag", processedPercentage, webSocketSessionId);
		}
		
		if(params.getStripITags()) {
			safeList.removeTags("i");
			
			tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "Removed <i> tag", processedPercentage, webSocketSessionId);
		}
		
		if(params.getStripUTags()) {
			safeList.removeTags("u");
			
			tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "Removed <u> tag", processedPercentage, webSocketSessionId);
		}
		
		if(params.getStripFontTags()) {
			safeList.removeTags("font");
			
			tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
			beforeCorrection = checkForChanges(tmp, beforeCorrection, "Removed <font> tag", processedPercentage, webSocketSessionId);
		}
		
		
		subUnitData.setText(beforeCorrection);
	}
		
	
	
}
