package com.subtitlescorrector.core.service.preprocessing;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;
import com.subtitlescorrector.core.util.Util;


@Service
public class HtmlStripPreProcessor implements PreProcessor{

	Logger log = LoggerFactory.getLogger(HtmlStripPreProcessor.class);

	@Autowired
	Util util;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	WebSocketMessageSender webSocketMessageSender;
	
	@Override
	public List<String> process(List<String> data) {
		
		List<String> processed = new ArrayList<>();
		
		Safelist safeList = new Safelist();
		safeList.addTags("b", "br", "i", "font")
			    .addAttributes("font", "color", "face", "size");
		
		OutputSettings outputSettings = new OutputSettings();
		outputSettings.prettyPrint(false);
		
		for(String line : data) {
			
			String text = line;
			
			Document document = Jsoup.parse(text);
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
					
					if(properties.getSubtitlesRealTimeUpdatesEnabled()) {
						webSocketMessageSender.sendMessage(event);
					}
					
				}
				
			}
			
			text = Jsoup.clean(text, "", safeList, outputSettings);
			
			processed.add(text);
			
		}
		
		return processed;
	}

}
