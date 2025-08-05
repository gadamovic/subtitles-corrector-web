package com.subtitlescorrector.core.domain;

import com.subtitlescorrector.core.util.Constants;

public enum EmailSendStatus {

	SUCCESS("Success"),
	FAILURE_EMAIL_SEND_RATE_LIMIT("Emails for the current hour exceeded limit of " + Constants.EMAILS_SENT_PER_HOUR_LIMIT + ". Email not sent!"),
	GENERIC_ERROR("Generic error"), DEVELOPMENT_NOT_SENT("Email not sent, because in development.");
	
	String description;
	
	EmailSendStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
