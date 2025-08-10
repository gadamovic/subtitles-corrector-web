package com.subtitlescorrector.core.domain;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;

public class SubtitleFileData implements Serializable{

	private static final long serialVersionUID = -2406499811554095114L;

	String filename;
	
	List<SubtitleUnitData> lines;
	
	String httpResponseMessage;
	
	Charset detectedCharset;
	
	SubtitleFormat format;
	
	BomData bomData;
	
	public SubtitleFormat getFormat() {
		return format;
	}

	public void setFormat(SubtitleFormat format) {
		this.format = format;
	}

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

	public BomData getBomData() {
		return bomData;
	}

	public void setBomData(BomData bomData) {
		this.bomData = bomData;
	}

}
