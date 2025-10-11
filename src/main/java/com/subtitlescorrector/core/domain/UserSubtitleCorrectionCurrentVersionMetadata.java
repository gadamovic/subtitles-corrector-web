package com.subtitlescorrector.core.domain;

public class UserSubtitleCorrectionCurrentVersionMetadata {

	SubtitleFormat format;
	BomData bomData;
	String filename;
	
	public SubtitleFormat getFormat() {
		return format;
	}
	public void setFormat(SubtitleFormat format) {
		this.format = format;
	}
	public BomData getBomData() {
		return bomData;
	}
	public void setBomData(BomData bomData) {
		this.bomData = bomData;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
