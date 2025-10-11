package com.subtitlescorrector.core.domain;

import java.io.Serializable;

public class UserSubtitleConversionCurrentVersionMetadata implements Serializable{

	private static final long serialVersionUID = 1L;

	SubtitleFormat sourceFormat;
	SubtitleFormat targetFormat;
	BomData bomData;
	String filename;
	
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
