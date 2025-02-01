package com.subtitlescorrector.domain;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;

public class SubtitleFileData implements Serializable{

	private static final long serialVersionUID = -2406499811554095114L;

	String filename;
	
	List<SubtitleUnitData> lines;
	
	String httpResponseMessage;
	
	Charset detectedCharset;
	
	/**
	 * Byte order mark
	 */
	Boolean hasBom;

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

	public String getHttpResponseMessage() {
		return httpResponseMessage;
	}

	public void setHttpResponseMessage(String httpResponseMessage) {
		this.httpResponseMessage = httpResponseMessage;
	}

	public Charset getDetectedCharset() {
		return detectedCharset;
	}

	public void setDetectedCharset(Charset detectedCharset) {
		this.detectedCharset = detectedCharset;
	}

	public Boolean getHasBom() {
		return hasBom;
	}

	public void setHasBom(Boolean hasBom) {
		this.hasBom = hasBom;
	}

}
