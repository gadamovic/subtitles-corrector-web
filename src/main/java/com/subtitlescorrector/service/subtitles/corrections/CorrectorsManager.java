package com.subtitlescorrector.service.subtitles.corrections;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.AdditionalData;

@Service
public class CorrectorsManager {

	@Autowired
	InvalidCharactersCorrector invalidCharactersCorrector;
	
	@Autowired
	HtmlTagsCorrector htmlTagsCorrector;
	
	public List<Corrector> getCorrectors(AdditionalData clientParameters) {
		
		List<Corrector> correctors = new ArrayList<>();
		correctors.add(invalidCharactersCorrector);
		
		if(clientParameters.getStripBTags() ||
				clientParameters.getStripFontTags() ||
				clientParameters.getStripITags() || 
				clientParameters.getStripUTags()) {
			correctors.add(htmlTagsCorrector);
		}
		
		return correctors;
		
	}
	
}
