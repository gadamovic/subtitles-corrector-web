package com.subtitlescorrector.core.domain;

public enum RequestValidatorStatus {

	FILE_TOO_BIG("File is too big for upload"),
	OK("File valid"),
	SUBTITLES_PROCESSED_LIMIT_EXCEEDED_FOR_IP("Subtitles processed limit has been exceeded");
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private RequestValidatorStatus(String message) {
		this.message = message;
	}
}
