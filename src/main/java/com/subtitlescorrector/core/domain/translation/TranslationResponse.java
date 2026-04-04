package com.subtitlescorrector.core.domain.translation;

import java.util.List;

public class TranslationResponse {

	private List<TranslationLine> translations;

	public List<TranslationLine> getTranslations() {
		return translations;
	}

	public void setTranslations(List<TranslationLine> translations) {
		this.translations = translations;
	}
	
}
