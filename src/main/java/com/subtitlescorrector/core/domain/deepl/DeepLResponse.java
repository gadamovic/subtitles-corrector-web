package com.subtitlescorrector.core.domain.deepl;

import java.util.List;

public class DeepLResponse {
	List<DeepLTranslation> translations;

	public List<DeepLTranslation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<DeepLTranslation> translations) {
		this.translations = translations;
	}
	
}
