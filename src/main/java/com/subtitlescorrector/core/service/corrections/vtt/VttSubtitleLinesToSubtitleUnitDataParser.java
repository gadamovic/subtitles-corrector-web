package com.subtitlescorrector.core.service.corrections.vtt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.SecondMillisecondDelimiterRegex;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.SubtitleTimestamp;
import com.subtitlescorrector.core.service.corrections.vtt.domain.VttSubtitleFileData;
import com.subtitlescorrector.core.service.corrections.vtt.domain.VttSubtitleUnitData;
import com.subtitlescorrector.core.util.Util;

import io.micrometer.common.util.StringUtils;

@Service
public class VttSubtitleLinesToSubtitleUnitDataParser {

	private static final String WEBVTT_MARK = "WEBVTT";

	Logger log = LoggerFactory.getLogger(VttSubtitleLinesToSubtitleUnitDataParser.class);

	@Autowired
	Util util;
	
	public VttSubtitleFileData convertToSubtitleUnits(List<String> lines){
		
		VttSubtitleFileData fileData = new VttSubtitleFileData();
		
		lines = trimLines(lines);
		
		Util.removeBomIfExists(lines);
		
		List<VttSubtitleUnitData> dataList = new ArrayList<>();
		VttSubtitleUnitData data = null;
		int i = -1;
		int subtitleLineNumber = 1;
		boolean foundFirstSubtitleLine = false;
		
		for(String line : lines) {
			
			i++;
			
			if(line.contains("-->")) {
				
				foundFirstSubtitleLine = true;
				data = new VttSubtitleUnitData();
				data.setNumber(subtitleLineNumber++);
				data.setFormat(SubtitleFormat.VTT);
				
				String from = (line.substring(0, line.indexOf("-->") - 1));
				String to = line.substring(line.indexOf(" --> ") + " --> ".length());
				
				if(to.contains(" ")) {
					String toSplit[] = to.split(" ");
					
					to = toSplit[0];
					String cues = "";
					
					for(int j = 1; j < toSplit.length; j++) {
						cues += (" " + toSplit[j]);
					}
					
					cues = cues.substring(1);
					data.setCues(cues);
					
				}
				
				SubtitleTimestamp tsFrom = util.parseSubtitleTimestampString(from, SecondMillisecondDelimiterRegex.DOT);
				SubtitleTimestamp tsTo = util.parseSubtitleTimestampString(to, SecondMillisecondDelimiterRegex.DOT);
				
				tsFrom.setFormattedTimestamp(Util.formatTimestamp(tsFrom, "."));
				tsTo.setFormattedTimestamp(Util.formatTimestamp(tsTo, "."));
				
				data.setTimestampFrom(tsFrom);
				data.setTimestampTo(tsTo);

				continue;
			}
			
			if(!line.equals(WEBVTT_MARK) && !foundFirstSubtitleLine) {
				fileData.addLineToHeader(line);
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
		
		fileData.setHeader(trimHeader(fileData.getHeader()));
		fileData.setLines(dataList);
		return fileData;
	}

	private List<String> trimHeader(List<String> header) {
		
		if(header == null || header.size() == 0) {
			return header;
		}
		
		int startIndex = 0;
		int endIndex = header.size() - 1;
		for (int i = 0; i < header.size(); i++) {
			if (StringUtils.isNotBlank(header.get(i))) {
				startIndex = i;
				break;
			}
		}

		for (int i = header.size() - 1; i >= 0; i--) {
			if (StringUtils.isNotBlank(header.get(i))) {
				endIndex = i;
				break;
			}			
		}
		
		if((startIndex == endIndex) && (StringUtils.isBlank(header.get(startIndex)))) {
			return Collections.emptyList();
		}
		
		return header.subList(startIndex, endIndex + 1);

	}

	private List<String> trimLines(List<String> lines) {
				
		if(lines == null || lines.size() == 0) {
			return lines;
		}
		
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
	
	public List<String> convertToListOfStrings(VttSubtitleFileData data, boolean addBom){
		
		List<VttSubtitleUnitData> lines = data.getLines();
		
		List<String> stringList = new ArrayList<String>(); 
		
		stringList.add(WEBVTT_MARK);
		stringList.add("");
		
		if(data.getHeader() != null && data.getHeader().size() > 0) {
			stringList.addAll(data.getHeader());
			stringList.add("");
		}
		
		for(VttSubtitleUnitData subtitle : lines) {
			
			StringBuilder sb = new StringBuilder(getTimestampFrom(subtitle)).append(" --> ").append(getTimestampTo(subtitle));
			if(StringUtils.isNotBlank(subtitle.getCues())) {
				sb.append(" ").append(subtitle.getCues());
			}
			
			stringList.add(sb.toString());
			
			stringList.add(subtitle.getText());
			stringList.add("");
		}
		
		if(addBom) {
			stringList = Util.addBom(stringList);
		}
		
		return stringList;
		
	}
	
	private String getTimestampFrom(VttSubtitleUnitData data) {
		
		SubtitleTimestamp timestamp = null;
		
		if(data.getTimestampFromShifted() != null) {
			timestamp = data.getTimestampFromShifted();
		}else {
			timestamp = data.getTimestampFrom();
		}
		
		String formattedTimestamp = Util.formatTimestamp(timestamp, ".");
		
		return formattedTimestamp;
		
	}

	private String getTimestampTo(VttSubtitleUnitData data) {
		
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
