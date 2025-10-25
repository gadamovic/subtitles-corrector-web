package com.subtitlescorrector.core.service.corrections.ass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.SecondMillisecondDelimiterRegex;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.SubtitleTimestamp;
import com.subtitlescorrector.core.domain.TimeUnit;
import com.subtitlescorrector.core.domain.ass.AssSubtitleFileData;
import com.subtitlescorrector.core.domain.ass.AssSubtitleUnitData;
import com.subtitlescorrector.core.domain.exception.SubtitleFileParseException;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.util.SubtitleTimestampUtils;
import com.subtitlescorrector.core.util.Util;

@Service
public class AssSubtitleLinesToSubtitleUnitDataParser {

	private static final String EVENTS_SECTION_START = "[Events]";
	private static final String FORMAT = "Format:";
	private static final String DEFAULT_DIALOGUE_FORMAT_PLACEHOLDER = "Layer,Start,End,Style,Name,MarginL,MarginR,MarginV,Effect,Text";

	Logger log = LoggerFactory.getLogger(SrtSubtitleLinesToSubtitleUnitDataParser.class);
	
    private static final Pattern STYLE_PATTERN = Pattern.compile("\\{.*?\\}");
	
	@Autowired
	SubtitleTimestampUtils util;
		
	public AssSubtitleFileData convertToSubtitleUnits(List<String> lines) {

		AssSubtitleFileData fileData = new AssSubtitleFileData();
		
		boolean foundEventsSection = false;
		boolean foundFormat = false;
		List<String> format = null;
		int i = 1;
		
		Util.removeBomIfExists(lines);
		
		List<AssSubtitleUnitData> subsList = new ArrayList<>();
		
		try {
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
					String parts[] = tmp.split(",", format.size());
					
					String start = parts[format.indexOf("Start")];
					String end = parts[format.indexOf("End")];
					String text = parts[format.indexOf("Text")];
					text = STYLE_PATTERN.matcher(text).replaceAll("");
					
					AssSubtitleUnitData subUnit = new AssSubtitleUnitData();
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
		}catch(Exception e) {
			log.error("Error parsing ass file!", e);
			throw new SubtitleFileParseException("Error parsing ass file!");
		}
		fileData.setLines(subsList);
		return fileData;
	}

	public List<String> convertToListOfStrings(AssSubtitleFileData data, boolean addBom) {
		
		List<AssSubtitleUnitData> lines = data.getLines();
		
		List<String> stringList = new ArrayList<String>(); 
		
		stringList.addAll(createDefaultAssHeader());
		
		for(AssSubtitleUnitData subtitle : lines) {

			String line = "Dialogue: " + DEFAULT_DIALOGUE_FORMAT_PLACEHOLDER;
			String from = SubtitleTimestampUtils.formatTimestamp(subtitle.getTimestampFrom(), ".", TimeUnit.CENTISECOND);
			String to = SubtitleTimestampUtils.formatTimestamp(subtitle.getTimestampTo(), ".", TimeUnit.CENTISECOND);
			
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
