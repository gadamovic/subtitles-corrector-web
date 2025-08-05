package com.subtitlescorrector.core.domain;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;

public class SubtitleConversionFileData implements Serializable{

	private static final long serialVersionUID = -2406499811554095114L;

	private String filename;
	
	private List<SubtitleUnitData> lines;
	
	private SubtitleFormat sourceFormat;
	
	private SubtitleFormat targetFormat;
	
	private String detectedEncoding;

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

	public SubtitleFormat getSourceFormat() {
		return sourceFormat;
	}

	public void setSourceFormat(SubtitleFormat sourceFormat) {
		this.sourceFormat = sourceFormat;
	}

	public SubtitleFormat getTargetFormat() {
		return targetFormat;
	}

	public void setTargetFormat(SubtitleFormat targetFormat) {
		this.targetFormat = targetFormat;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDetectedEncoding() {
		return detectedEncoding;
	}

	public void setDetectedEncoding(String detectedEncoding) {
		this.detectedEncoding = detectedEncoding;
	}
	

	
	
}