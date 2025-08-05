package com.subtitlescorrector.core.domain;

public class ConversionParameters {

	private SubtitleFormat targetFormat;
	private String userId;

	public SubtitleFormat getTargetFormat() {
		return targetFormat;
	}

	public void setTargetFormat(SubtitleFormat targetFormat) {
		this.targetFormat = targetFormat;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}
	
	
}
