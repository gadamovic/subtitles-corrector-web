package com.subtitlescorrector.core.service.preprocessing;

import java.time.Instant;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SubtitleFileData;
import com.subtitlescorrector.core.domain.SubtitleUnitData;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;
import com.subtitlescorrector.core.util.Util;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

@Service
public class HtmlStripPreProcessor implements PreProcessor{

	Logger log = LoggerFactory.getLogger(HtmlStripPreProcessor.class);

	@Autowired
	Util util;
	
//	@Autowired
//	KafkaTemplate<Void, SubtitleCorrectionEvent> kafkaTemplate;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	WebSocketMessageSender webSocketMessageSender;
	
	@Override
	public SubtitleFileData process(SubtitleFileData data, AdditionalData params) {
		
		Safelist safeList = new Safelist();
		safeList.addTags("b", "br", "i", "font")
			    .addAttributes("font", "color", "face", "size");
		
		OutputSettings outputSettings = new OutputSettings();
		outputSettings.prettyPrint(false);
		
		for(SubtitleUnitData subUnitData : data.getLines()) {
			
			String line = subUnitData.getText();
			
			Document document = Jsoup.parse(line);
			for(Element element : document.body().getAllElements()) {
				
				if(element.tagName() != "#text" &&
						element.tagName() != "b" &&
						element.tagName() != "br" &&
						element.tagName() != "i" && 
						element.tagName() != "font" &&
						element.tagName() != "body") {
					
					SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
					event.setCorrection("Removed <" + element.tagName() + "> tag in: [ " + element.outerHtml() + " ] during preprocessing");
					event.setEventTimestamp(Instant.now());

					event.setProcessedPercentage("0");
					event.setWebSocketSessionId(params.getWebSocketSessionId());
					
					if(properties.getSubtitlesRealTimeUpdatesEnabled()) {
						//kafkaTemplate.send(Constants.SUBTITLES_CORRECTIONS_TOPIC_NAME, event);
						webSocketMessageSender.sendMessage(event);
					}
					
				}
				
			}
			
			line = Jsoup.clean(subUnitData.getText(), "", safeList, outputSettings);
			
			subUnitData.setText(line);
			
		}
		
		return data;
	}

}
