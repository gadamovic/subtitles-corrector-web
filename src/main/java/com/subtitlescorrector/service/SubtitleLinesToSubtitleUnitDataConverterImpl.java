package com.subtitlescorrector.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.SubtitleUnitData;

import io.micrometer.common.util.StringUtils;

@Service
public class SubtitleLinesToSubtitleUnitDataConverterImpl implements SubtitleLinesToSubtitleUnitDataConverter {

	@Override
	public List<SubtitleUnitData> convert(List<String> lines){
		
		List<SubtitleUnitData> dataList = new ArrayList<>();
		for(String line : lines) {
			
			SubtitleUnitData data = new SubtitleUnitData();
			data.setFormat("srt");
			
			Integer number = toInteger(line);
			if(number != null) {
				data.setNumber(number);
				continue;
			}
			
			if(line.contains("-->")) {
				data.setTimestampFrom(line.substring(0, line.indexOf("-->")));
				data.setTimestampFrom(line.substring((line.lastIndexOf(" ") + 1), line.length() - 1));
				continue;
			}
			
			if(StringUtils.isNotBlank(line)) {
				data.setText(line);
			}else {
				continue;
			}
			
			dataList.add(data);
			
		}
		
		return dataList;
	}

	public Integer toInteger(String candidate) {
		try {
			Integer i = Integer.parseInt(candidate);
			return i;
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

}
