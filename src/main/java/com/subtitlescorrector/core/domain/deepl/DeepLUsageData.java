package com.subtitlescorrector.core.domain.deepl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeepLUsageData {
	
	@JsonProperty("character_count")
	Integer characterCount;
	@JsonProperty("character_limit")
	Integer characterLimit;
	
	public Integer getCharacterCount() {
		return characterCount;
	}
	public void setCharacterCount(Integer characterCount) {
		this.characterCount = characterCount;
	}
	public Integer getCharacterLimit() {
		return characterLimit;
	}
	public void setCharacterLimit(Integer characterLimit) {
		this.characterLimit = characterLimit;
	}
	
}
