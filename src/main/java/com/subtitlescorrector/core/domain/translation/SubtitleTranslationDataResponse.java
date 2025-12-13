package com.subtitlescorrector.core.domain.translation;

import com.subtitlescorrector.core.domain.SubtitleFormat;

public class SubtitleTranslationDataResponse {
	
	private String filename;
	private SubtitleFormat detectedSourceFormat;
	private String detectedEncoding;
	private Integer numberOfLines;
	private String httpResponseMessage;
	
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
	public void setHttpResponseMessage(String httpResponseMessage) {
		this.httpResponseMessage = httpResponseMessage;
	}
	public String getHttpResponseMessage() {
		return this.httpResponseMessage;
	}
}
