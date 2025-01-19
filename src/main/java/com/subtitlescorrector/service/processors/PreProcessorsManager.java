package com.subtitlescorrector.service.processors;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.service.subtitles.corrections.Corrector;
import com.subtitlescorrector.service.subtitles.corrections.InvalidCharactersCorrector;

@Service
public class PreProcessorsManager {

	@Autowired
	BrTagPreProcessor brTagPreProcessor;
	
	@Autowired
	HtmlStripPreProcessor htmlStripPreProcessor;
	
	public List<PreProcessor> getPreProcessors() {
		
		List<PreProcessor> preProcessors = new ArrayList<>();
		preProcessors.add(brTagPreProcessor);
		preProcessors.add(htmlStripPreProcessor);
		
		return preProcessors;
		
	}
	
}
