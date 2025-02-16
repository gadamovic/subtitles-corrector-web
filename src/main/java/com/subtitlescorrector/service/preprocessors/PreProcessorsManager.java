package com.subtitlescorrector.service.preprocessors;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
