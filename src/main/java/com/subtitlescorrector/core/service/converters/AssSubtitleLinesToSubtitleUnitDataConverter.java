package com.subtitlescorrector.core.service.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.SecondMillisecondDelimiterRegex;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.SubtitleTimestamp;
import com.subtitlescorrector.core.domain.SubtitleUnitData;
import com.subtitlescorrector.core.domain.TimeUnit;
import com.subtitlescorrector.core.util.Util;

@Service
public class AssSubtitleLinesToSubtitleUnitDataConverter implements SubtitleLinesToSubtitleUnitDataConverter{

	private static final String EVENTS_SECTION_START = "[Events]";
	private static final String FORMAT = "Format:";
	private static final String DEFAULT_DIALOGUE_FORMAT_PLACEHOLDER = "Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text";

	Logger log = LoggerFactory.getLogger(SrtSubtitleLinesToSubtitleUnitDataConverter.class);
	
	@Autowired
	Util util;
	
	//TODO: <br> tags in texts and download button label shows source format in filename
	
	@Override
	public List<SubtitleUnitData> convertToSubtitleUnits(List<String> lines) {

		boolean foundEventsSection = false;
		boolean foundFormat = false;
		List<String> format = null;
		int i = 1;
		
		Util.removeBomIfExists(lines);
		
		List<SubtitleUnitData> subsList = new ArrayList<>();
		
		for(String line : lines) {
		
			if(!foundEventsSection && !line.equals(EVENTS_SECTION_START)) {
				continue;
			}else {
				foundEventsSection = true;
			}
			
			if(!foundFormat && !line.startsWith(FORMAT)) {
				continue;
			}else if(!foundFormat){
				foundFormat = true;
				format = Arrays.asList(line.substring(FORMAT.length()).split(","));
				format.replaceAll(String::trim);
			}
		
			
			if(line.contains("Dialogue: ")) {
				String tmp = line.substring("Dialogue: ".length());
				String parts[] = tmp.split(",");
				
				String start = parts[format.indexOf("Start")];
				String end = parts[format.indexOf("End")];
				String text = parts[format.indexOf("Text")];
				
				SubtitleUnitData subUnit = new SubtitleUnitData();
				subUnit.setFormat(SubtitleFormat.ASS);
				subUnit.setNumber(i++);
				subUnit.setText(text);
				
				SubtitleTimestamp tsFrom = util.parseSubtitleTimestampString(start, SecondMillisecondDelimiterRegex.DOT, 10);
				subUnit.setTimestampFrom(tsFrom);
				
				SubtitleTimestamp tsTo = util.parseSubtitleTimestampString(end, SecondMillisecondDelimiterRegex.DOT, 10);
				subUnit.setTimestampTo(tsTo);
				
				subsList.add(subUnit);
			}
			
		}
			
		return subsList;
	}

	@Override
	public List<String> convertToListOfStrings(List<SubtitleUnitData> lines, boolean addBom) {
		
		List<String> stringList = new ArrayList<String>(); 
		
		stringList.addAll(createDefaultAssHeader());
		
		for(SubtitleUnitData subtitle : lines) {

			String line = "Dialogue: " + DEFAULT_DIALOGUE_FORMAT_PLACEHOLDER;
			String from = Util.formatTimestamp(subtitle.getTimestampFrom(), ".", TimeUnit.CENTISECOND);
			String to = Util.formatTimestamp(subtitle.getTimestampTo(), ".", TimeUnit.CENTISECOND);
			
			line = line.replace("Layer", "0");
			line = line.replace("Start", from);
			line = line.replace("End", to);
			line = line.replace("Style", "Default");
			line = line.replace("Name", "");
			line = line.replace("MarginL", "0");
			line = line.replace("MarginR", "");
			line = line.replace("MarginV", "0");
			line = line.replace("Effect", "");
			line = line.replace("Text", subtitle.getText().replace("\n", "\\n"));
			
			stringList.add(line);
			
		}
		
		if(addBom) {
			stringList = Util.addBom(stringList);
		}
		
		return stringList;
	}

	private List<String> createDefaultAssHeader() {

		List<String> header = new ArrayList<>();
		
		header.add("[Script Info]");
		header.add("ScriptType: v4.00+");
		header.add("");
		header.add("[V4+ Styles]");
		header.add("Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding");
		header.add("Style: Default,Arial,20,&H00FFFFFF,&H000000FF,&H00000000,&H80000000,-1,0,0,0,100,100,0,0,1,1,0,2,0,0,0,0");
		header.add("");
		header.add("[Events]");
		header.add(FORMAT + " " + DEFAULT_DIALOGUE_FORMAT_PLACEHOLDER);
		header.add("");
		
		return header;
	}

}
