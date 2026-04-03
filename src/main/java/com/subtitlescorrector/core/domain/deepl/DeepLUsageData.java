package com.subtitlescorrector.core.domain.deepl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeepLUsageData {
	
	@JsonProperty("character_count")
	Long characterCount;
	@JsonProperty("character_limit")
	Long characterLimit;
	
	public Long getCharacterCount() {
		return characterCount;
	}
	public void setCharacterCount(Long characterCount) {
		this.characterCount = characterCount;
	}
	public Long getCharacterLimit() {
		return characterLimit;
	}
	public void setCharacterLimit(Long characterLimit) {
		this.characterLimit = characterLimit;
	}
	
}
