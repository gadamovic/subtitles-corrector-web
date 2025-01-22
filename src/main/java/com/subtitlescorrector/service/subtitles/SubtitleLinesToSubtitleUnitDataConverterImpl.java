package com.subtitlescorrector.service.subtitles;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.SubtitleUnitData;

import io.micrometer.common.util.StringUtils;

@Service
public class SubtitleLinesToSubtitleUnitDataConverterImpl implements SubtitleLinesToSubtitleUnitDataConverter {

	Logger log = LoggerFactory.getLogger(SubtitleLinesToSubtitleUnitDataConverterImpl.class);

	@Override
	public List<SubtitleUnitData> convertToSubtitleUnits(List<String> lines){
		
		List<SubtitleUnitData> dataList = new ArrayList<>();
		SubtitleUnitData data = null;

		for(String line : lines) {
			
			
			Integer number = toInteger(line);
			if(number != null && data == null) {
				data = new SubtitleUnitData();
				data.setNumber(number);
				data.setFormat("srt");
				continue;
			}
			
			if(line.contains("-->")) {
				data.setTimestampFrom(line.substring(0, line.indexOf("-->") - 1));
				data.setTimestampTo(line.substring((line.lastIndexOf(" ") + 1), line.length()));
				continue;
			}
			
			if(StringUtils.isNotBlank(line)) {
				if(StringUtils.isNotBlank(data.getText())) {
					data.setText(data.getText() + "\n" + line);
				}else {
					data.setText(line);
				}
			}else {
				//end of subtitle
				dataList.add(data);
				data = null;
			}
			
			
		}
		
		return dataList;
	}

	public Integer toInteger(String candidate) {
		try {
			Integer i = Integer.parseInt(candidate.trim());
			return i;
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
	
	public List<String> convertToListOfStrings(List<SubtitleUnitData> lines){
		
		List<String> stringList = new ArrayList<String>(); 
		
		for(SubtitleUnitData subtitle : lines) {
			stringList.add(subtitle.getNumber().toString());
			stringList.add(getTimestampFrom(subtitle) + " --> " + getTimestampTo(subtitle));
			stringList.add(subtitle.getText());
			stringList.add("");
		}
		
		return stringList;
		
	}
	
	private String getTimestampFrom(SubtitleUnitData data) {
		if(data.getTimestampFromShifted() != null) {
			return data.getTimestampFromShifted();
		}else {
			return data.getTimestampFrom();
		}
	}
	
	private String getTimestampTo(SubtitleUnitData data) {
		if(data.getTimestampToShifted() != null) {
			return data.getTimestampToShifted();
		}else {
			return data.getTimestampTo();
		}
	}

}
