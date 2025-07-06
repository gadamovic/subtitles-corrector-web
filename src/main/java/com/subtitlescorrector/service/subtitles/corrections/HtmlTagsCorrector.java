package com.subtitlescorrector.service.subtitles.corrections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.util.Util;

@Service
public class HtmlTagsCorrector implements Corrector{

	Logger log = LoggerFactory.getLogger(HtmlTagsCorrector.class);
	
	@Autowired
	KafkaTemplate<Void, SubtitleCorrectionEvent> kafkaTemplate;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	Util util;
	
	@Override
	public SubtitleFileData correct(SubtitleFileData data, AdditionalData additionalData) {

		int numberOfLines = data.getLines().size() * additionalData.getNumberOfCorrectors();
		int currentLineNumber = data.getLines().size() * (additionalData.getCorrectorIndex() - 1) + 1;
		float processedPercentage = 0f;
		
		String webSocketSessionId = additionalData.getWebSocketSessionId();
		
		Safelist safeList = new Safelist();
		
		safeList.addTags("b", "br", "i", "u", "font")
		.addAttributes("font", "color", "face", "size");
		
		
		OutputSettings outputSettings = new OutputSettings();
		outputSettings.prettyPrint(false);
		
		for(SubtitleUnitData subUnitData : data.getLines()) {
			
			processedPercentage = ((float) currentLineNumber / (float) numberOfLines) * 100;
			
			String line = subUnitData.getText();
			String tmp = "";
			String beforeCorrection = line;
			
			safeList.addTags("b", "br", "i", "u", "font")
			.addAttributes("font", "color", "face", "size");
			
			if(additionalData.getStripBTags()) {
				safeList.removeTags("b");
				
				tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
				beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Removed <b> tag", processedPercentage, webSocketSessionId);
			}
			
			if(additionalData.getStripITags()) {
				safeList.removeTags("i");
				
				tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
				beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Removed <i> tag", processedPercentage, webSocketSessionId);
			}
			
			if(additionalData.getStripUTags()) {
				safeList.removeTags("u");
				
				tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
				beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Removed <u> tag", processedPercentage, webSocketSessionId);
			}
			
			if(additionalData.getStripFontTags()) {
				safeList.removeTags("font");
				
				tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
				beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Removed <font> tag", processedPercentage, webSocketSessionId);
			}
			
			
			subUnitData.setText(beforeCorrection);
			currentLineNumber ++;
		}

		return data;
	}

	@Override
	public void correct(SubtitleUnitData subUnitData, AdditionalData params) {
		
		String webSocketSessionId = params.getWebSocketSessionId();
		
		Safelist safeList = new Safelist();
		
		safeList.addTags("b", "br", "i", "u", "font")
		.addAttributes("font", "color", "face", "size");
		
		
		OutputSettings outputSettings = new OutputSettings();
		outputSettings.prettyPrint(false);
		
		float processedPercentage = 0f;
		
		processedPercentage = ((float) params.getProcessedLines() / (float) params.getTotalNumberOfLines()) * 100;
		
		String line = subUnitData.getText();
		String tmp = "";
		String beforeCorrection = line;
		
		safeList.addTags("b", "br", "i", "u", "font")
		.addAttributes("font", "color", "face", "size");
		
		if(params.getStripBTags()) {
			safeList.removeTags("b");
			
			tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Removed <b> tag", processedPercentage, webSocketSessionId);
		}
		
		if(params.getStripITags()) {
			safeList.removeTags("i");
			
			tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Removed <i> tag", processedPercentage, webSocketSessionId);
		}
		
		if(params.getStripUTags()) {
			safeList.removeTags("u");
			
			tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Removed <u> tag", processedPercentage, webSocketSessionId);
		}
		
		if(params.getStripFontTags()) {
			safeList.removeTags("font");
			
			tmp = Jsoup.clean(beforeCorrection, "", safeList, outputSettings);
			beforeCorrection = util.checkForChanges(tmp, beforeCorrection, "Removed <font> tag", processedPercentage, webSocketSessionId);
		}
		
		
		subUnitData.setText(beforeCorrection);
	}
		
	
	
}
