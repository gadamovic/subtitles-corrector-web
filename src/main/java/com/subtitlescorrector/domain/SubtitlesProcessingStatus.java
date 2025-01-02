package com.subtitlescorrector.domain;

public enum SubtitlesProcessingStatus {

	SUCCESS("OK"),
	FAILURE_TOO_MANY_REQUESTS("Users are allowed to process one subtitle every two minutes!"),
	SUCCESS_DEVELOPMENT("OK - development");
	
	SubtitlesProcessingStatus(String description) {
		this.description = description;
	}

	public String description;
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
