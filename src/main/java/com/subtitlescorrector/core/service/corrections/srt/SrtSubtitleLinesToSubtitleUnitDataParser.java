package com.subtitlescorrector.core.service.corrections.srt;

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
import com.subtitlescorrector.core.domain.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleUnitData;
import com.subtitlescorrector.core.service.converters.SubtitleLinesToSubtitleUnitDataConverter;
import com.subtitlescorrector.core.util.SubtitleTimestampUtils;
import com.subtitlescorrector.core.util.Util;

import io.micrometer.common.util.StringUtils;

@Service
public class SrtSubtitleLinesToSubtitleUnitDataParser{

	Logger log = LoggerFactory.getLogger(SrtSubtitleLinesToSubtitleUnitDataParser.class);

	@Autowired
	SubtitleTimestampUtils util;
	
	public SrtSubtitleFileData convertToSubtitleUnits(List<String> lines){
		
		SrtSubtitleFileData fileData = new SrtSubtitleFileData();
		
		Util.removeBomIfExists(lines);
		
		lines = trimLines(lines);
		
		List<SrtSubtitleUnitData> dataList = new ArrayList<>();
		SrtSubtitleUnitData data = null;
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
				data = new SrtSubtitleUnitData();
				data.setNumber(number);
				data.setFormat(SubtitleFormat.SRT);
				continue;
			}
			
			if(line.contains("-->")) {
				String from = (line.substring(0, line.indexOf("-->") - 1));
				String to = line.substring((line.lastIndexOf(" ") + 1), line.length());
				
				SubtitleTimestamp tsFrom = util.parseSubtitleTimestampString(from, SecondMillisecondDelimiterRegex.COMMA);
				SubtitleTimestamp tsTo = util.parseSubtitleTimestampString(to, SecondMillisecondDelimiterRegex.COMMA);
				
				tsFrom.setFormattedTimestamp(SubtitleTimestampUtils.formatTimestamp(tsFrom, ","));
				tsTo.setFormattedTimestamp(SubtitleTimestampUtils.formatTimestamp(tsTo, ","));
				
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
		fileData.setLines(dataList);
		return fileData;
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
	
	public List<String> convertToListOfStrings(SrtSubtitleFileData data, boolean addBom){
		
		List<SrtSubtitleUnitData> lines = data.getLines();
		
		List<String> stringList = new ArrayList<String>(); 
		
		for(SrtSubtitleUnitData subtitle : lines) {
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
	
	private String getTimestampFrom(SrtSubtitleUnitData data) {
		
		SubtitleTimestamp timestamp = null;
		
		if(data.getTimestampFromShifted() != null) {
			timestamp = data.getTimestampFromShifted();
		}else {
			timestamp = data.getTimestampFrom();
		}
		
		String formattedTimestamp = SubtitleTimestampUtils.formatTimestamp(timestamp, ",");
		
		return formattedTimestamp;
		
	}

	private String getTimestampTo(SrtSubtitleUnitData data) {
		
		SubtitleTimestamp timestamp = null;
		
		if(data.getTimestampToShifted() != null) {
			timestamp = data.getTimestampToShifted();
		}else {
			timestamp = data.getTimestampTo();
		}
		
		String formattedTimestamp = SubtitleTimestampUtils.formatTimestamp(timestamp, ",");
		
		return formattedTimestamp;
	}

}
