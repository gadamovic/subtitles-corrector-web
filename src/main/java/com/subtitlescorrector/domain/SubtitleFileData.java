package com.subtitlescorrector.domain;

import java.io.Serializable;
import java.util.List;

public class SubtitleFileData implements Serializable{

	private static final long serialVersionUID = -2406499811554095114L;

	String filename;
	
	List<SubtitleUnitData> lines;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<SubtitleUnitData> getLines() {
		return lines;
	}

	public void setLines(List<SubtitleUnitData> lines) {
		this.lines = lines;
	}
	
	
	
	
}
