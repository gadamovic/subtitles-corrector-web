package com.subtitlescorrector.core.service.corrections;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.AdditionalData;

@Service
public class CorrectorsManager {

	@Autowired
	InvalidCharactersCorrector invalidCharactersCorrector;
	
	@Autowired
	HtmlTagsCorrector htmlTagsCorrector;
	
	public List<AbstractCorrector> getCorrectors(AdditionalData clientParameters) {
		
		List<AbstractCorrector> correctors = new ArrayList<>();
		correctors.add(invalidCharactersCorrector);
		
		if((clientParameters.getStripBTags() != null && clientParameters.getStripBTags()) ||
				(clientParameters.getStripFontTags() != null && clientParameters.getStripFontTags()) ||
				(clientParameters.getStripITags() != null && clientParameters.getStripITags()) || 
				(clientParameters.getStripUTags() != null && clientParameters.getStripUTags())) {
			correctors.add(htmlTagsCorrector);
		}
		
		return correctors;
		
	}
	
}
