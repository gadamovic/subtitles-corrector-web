package com.subtitlescorrector.core.domain.deepl;

import java.util.ArrayList;
import java.util.List;

import com.subtitlescorrector.core.domain.translation.TranslationLine;
import com.subtitlescorrector.core.domain.translation.TranslationResponse;
import com.subtitlescorrector.core.domain.translation.TranslationResponseBase;

public class DeepLResponse implements TranslationResponseBase{
	List<DeepLTranslation> translations;

	public List<DeepLTranslation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<DeepLTranslation> translations) {
		this.translations = translations;
	}

	@Override
	public TranslationResponse toTranslationResponse() {
		
		TranslationResponse response = new TranslationResponse();
		response.setTranslations(new ArrayList<TranslationLine>());
		
		for(DeepLTranslation translation : translations) {
			TranslationLine line = new TranslationLine();
			line.setText(translation.getText());
			line.setDetectedSourceLanguage(translation.getDetectedSourceLanguage());
			response.getTranslations().add(line);
		}
		
		return response;
	}
	
}
