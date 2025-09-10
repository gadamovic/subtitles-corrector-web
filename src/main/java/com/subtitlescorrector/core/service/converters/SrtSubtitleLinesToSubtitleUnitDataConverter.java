package com.subtitlescorrector.core.service.converters;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SecondMillisecondDelimiterRegex;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.SubtitleTimestamp;
import com.subtitlescorrector.core.domain.SubtitleUnitData;
import com.subtitlescorrector.core.util.Util;

import io.micrometer.common.util.StringUtils;

@Service
public class SrtSubtitleLinesToSubtitleUnitDataConverter implements SubtitleLinesToSubtitleUnitDataConverter {

	Logger log = LoggerFactory.getLogger(SrtSubtitleLinesToSubtitleUnitDataConverter.class);

	@Autowired
	Util util;
	
	@Override
	public List<SubtitleUnitData> convertToSubtitleUnits(List<String> lines){
		
		lines = trimLines(lines);
		
		Util.removeBomIfExists(lines);
		
		List<SubtitleUnitData> dataList = new ArrayList<>();
		SubtitleUnitData data = null;
		int i = -1;
		
		for(String line : lines) {
			
			//ignore multiple consecutive blank lines
			//TODO: should also handle the case with random blank lines across the file
			i++;
			if(StringUtils.isBlank(line) && (i < lines.size()) && StringUtils.isBlank(lines.get(i + 1))) {
				continue;
			}
			
			Integer number = toInteger(line);
			if(number != null && data == null) {
				data = new SubtitleUnitData();
				data.setNumber(number);
				data.setFormat(SubtitleFormat.SRT);
				continue;
			}
			
			if(line.contains("-->")) {
				String from = (line.substring(0, line.indexOf("-->") - 1));
				String to = line.substring((line.lastIndexOf(" ") + 1), line.length());
				
				SubtitleTimestamp tsFrom = util.parseSubtitleTimestampString(from, SecondMillisecondDelimiterRegex.COMMA);
				SubtitleTimestamp tsTo = util.parseSubtitleTimestampString(to, SecondMillisecondDelimiterRegex.COMMA);
				
				tsFrom.setFormattedTimestamp(Util.formatTimestamp(tsFrom, ","));
				tsTo.setFormattedTimestamp(Util.formatTimestamp(tsTo, ","));
				
				data.setTimestampFrom(tsFrom);
				data.setTimestampTo(tsTo);
				
				continue;
			}
			
			if(StringUtils.isNotBlank(line)) {
				if(StringUtils.isNotBlank(data.getText())) {
					data.setText(data.getText() + "\n" + line);
				}else {
					data.setText(line);
				}
				
				if(i == lines.size()-1) {
					//last line of the file
					dataList.add(data);
					data = null;
				}
				
			}else {
				//end of subtitle
				dataList.add(data);
				data = null;
			}
			
			
		}
		
		return dataList;
	}

	private List<String> trimLines(List<String> lines) {
				
		int firstValidIndex = 0;
		
		for(String line : lines) {
			
			if(StringUtils.isBlank(line)) {
				firstValidIndex ++;
				continue;
			}else {
				break;
			}
			
		}
		
		return lines.subList(firstValidIndex, lines.size());
		
	}

	public Integer toInteger(String candidate) {
		try {
			Integer i = Integer.parseInt(candidate.trim());
			return i;
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
	
	public List<String> convertToListOfStrings(List<SubtitleUnitData> lines, boolean addBom){
		
		List<String> stringList = new ArrayList<String>(); 
		
		for(SubtitleUnitData subtitle : lines) {
			stringList.add(subtitle.getNumber().toString());
			stringList.add(getTimestampFrom(subtitle) + " --> " + getTimestampTo(subtitle));
			stringList.add(subtitle.getText());
			stringList.add("");
		}
		
		if(addBom) {
			stringList = Util.addBom(stringList);
		}
		
		return stringList;
		
	} 
	
	private String getTimestampFrom(SubtitleUnitData data) {
		
		SubtitleTimestamp timestamp = null;
		
		if(data.getTimestampFromShifted() != null) {
			timestamp = data.getTimestampFromShifted();
		}else {
			timestamp = data.getTimestampFrom();
		}
		
		String formattedTimestamp = Util.formatTimestamp(timestamp, ",");
		
		return formattedTimestamp;
		
	}

	private String getTimestampTo(SubtitleUnitData data) {
		
		SubtitleTimestamp timestamp = null;
		
		if(data.getTimestampToShifted() != null) {
			timestamp = data.getTimestampToShifted();
		}else {
			timestamp = data.getTimestampTo();
		}
		
		String formattedTimestamp = Util.formatTimestamp(timestamp, ",");
		
		return formattedTimestamp;
	}

}
