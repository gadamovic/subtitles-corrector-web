package com.subtitlescorrector.domain;

public class SubtitleConversionFileDataResponse {

	private String filename;
	private SubtitleFormat detectedSourceFormat;
	private String detectedEncoding;
	private Integer numberOfLines;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public SubtitleFormat getDetectedSourceFormat() {
		return detectedSourceFormat;
	}
	public void setDetectedSourceFormat(SubtitleFormat detectedSourceFormat) {
		this.detectedSourceFormat = detectedSourceFormat;
	}
	public String getDetectedEncoding() {
		return detectedEncoding;
	}
	public void setDetectedEncoding(String detectedEncoding) {
		this.detectedEncoding = detectedEncoding;
	}
	public Integer getNumberOfLines() {
		return numberOfLines;
	}
	public void setNumberOfLines(Integer numberOfLines) {
		this.numberOfLines = numberOfLines;
	}

}
