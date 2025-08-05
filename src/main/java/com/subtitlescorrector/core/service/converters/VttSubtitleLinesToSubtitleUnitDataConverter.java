package com.subtitlescorrector.core.service.converters;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.SubtitleTimestamp;
import com.subtitlescorrector.core.domain.SubtitleUnitData;
import com.subtitlescorrector.core.util.Util;

import io.micrometer.common.util.StringUtils;

@Service
public class VttSubtitleLinesToSubtitleUnitDataConverter implements SubtitleLinesToSubtitleUnitDataConverter {

	Logger log = LoggerFactory.getLogger(VttSubtitleLinesToSubtitleUnitDataConverter.class);

	@Autowired
	Util util;
	
	@Override
	public List<SubtitleUnitData> convertToSubtitleUnits(List<String> lines){
		
		lines = trimLines(lines);
		
		List<SubtitleUnitData> dataList = new ArrayList<>();
		SubtitleUnitData data = null;
		int i = -1;
		int subtitleLineNumber = 1;
		boolean foundFirstSubtitleLine = false;
		
		for(String line : lines) {
			
			i++;
			
			if(line.contains("-->")) {
				
				foundFirstSubtitleLine = true;
				data = new SubtitleUnitData();
				data.setNumber(subtitleLineNumber++);
				data.setFormat(SubtitleFormat.VTT);
				
				String from = (line.substring(0, line.indexOf("-->") - 1));
				String to = line.substring((line.lastIndexOf(" ") + 1), line.length());
				
				SubtitleTimestamp tsFrom = Util.parseSubtitleTimestampString(from, "\\.");
				SubtitleTimestamp tsTo = Util.parseSubtitleTimestampString(to, "\\.");
				
				tsFrom.setFormattedTimestamp(Util.formatTimestamp(tsFrom, "."));
				tsTo.setFormattedTimestamp(Util.formatTimestamp(tsTo, "."));
				
				data.setTimestampFrom(tsFrom);
				data.setTimestampTo(tsTo);

				continue;
			}
			
			//ignore multiple consecutive blank lines or any blank lines before the first subtitle line is found
			if((!foundFirstSubtitleLine) ||
					(StringUtils.isBlank(line) && (i < lines.size()) && StringUtils.isBlank(lines.get(i + 1)))) {
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
	
	public List<String> convertToListOfStrings(List<SubtitleUnitData> lines){
		
		List<String> stringList = new ArrayList<String>(); 
		
		stringList.add("WEBVTT");
		stringList.add("");
		
		for(SubtitleUnitData subtitle : lines) {
			stringList.add(getTimestampFrom(subtitle) + " --> " + getTimestampTo(subtitle));
			stringList.add(subtitle.getText());
			stringList.add("");
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
		
		String formattedTimestamp = Util.formatTimestamp(timestamp, ".");
		
		return formattedTimestamp;
		
	}

	private String getTimestampTo(SubtitleUnitData data) {
		
		SubtitleTimestamp timestamp = null;
		
		if(data.getTimestampToShifted() != null) {
			timestamp = data.getTimestampToShifted();
		}else {
			timestamp = data.getTimestampTo();
		}
		
		String formattedTimestamp = Util.formatTimestamp(timestamp, ".");
		
		return formattedTimestamp;
	}

}
