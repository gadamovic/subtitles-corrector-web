package com.subtitlescorrector.core.service.preprocessing;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class BrTagPreProcessor implements PreProcessor{

	@Override
	public List<String> process(List<String> data) {
		
		List<String> processed = new ArrayList<>();
		
		for(String line : data) {
			
			String text = "";
			if(StringUtils.isNotBlank(line)) {
				text = line.replace("\n", "<br>");
			}
			processed.add(text);
			
		}
		
		return processed;
	}

}
