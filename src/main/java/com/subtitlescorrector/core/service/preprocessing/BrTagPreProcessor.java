package com.subtitlescorrector.core.service.preprocessing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BrTagPreProcessor implements PreProcessor{

	@Override
	public List<String> process(List<String> data) {
		
		List<String> processed = new ArrayList<>();
		
		for(String line : data) {
			
			String text = line.replace("\n", "<br>");
			processed.add(text);
			
		}
		
		return processed;
	}

}
