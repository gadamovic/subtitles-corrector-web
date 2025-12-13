package com.subtitlescorrector.core.domain.deepl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeepLTranslation {
	
	String text;
	@JsonProperty("detected_source_language")
	String detectedSourceLanguage;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDetectedSourceLanguage() {
		return detectedSourceLanguage;
	}

	public void setDetectedSourceLanguage(String detectedSourceLanguage) {
		this.detectedSourceLanguage = detectedSourceLanguage;
	}
	
}
