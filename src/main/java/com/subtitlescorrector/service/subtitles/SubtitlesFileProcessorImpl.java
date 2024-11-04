package com.subtitlescorrector.service.subtitles;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.subtitlescorrector.util.FileUtil;

@Component
public class SubtitlesFileProcessorImpl implements SubtitlesFileProcessor {

	@Override
	public File process(File storedFile) {
		
		List<String> lines = FileUtil.loadTextFile(storedFile);
		List<String> correctedLines = new ArrayList<>();
		
		for(String line : lines) {
			String correctedLine = line.replace("", "ž");
			correctedLine = correctedLine.replace("", "Ž");
	
			correctedLine = correctedLine.replace("", "š");
			correctedLine = correctedLine.replace("", "Š");
	
			correctedLine = correctedLine.replace("æ", "ć");
			correctedLine = correctedLine.replace("Æ", "Ć");
	
			correctedLine = correctedLine.replace("è", "č");
			correctedLine = correctedLine.replace("È", "Č");
			correctedLines.add(correctedLine);
		}
		
		File correctedFile = new File("correctedFile.srt");
		FileUtil.writeLinesToFile(correctedFile, correctedLines, StandardCharsets.UTF_8);
		return correctedFile;
	}

}
