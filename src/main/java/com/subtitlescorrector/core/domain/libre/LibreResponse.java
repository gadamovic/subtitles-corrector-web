package com.subtitlescorrector.core.domain.libre;

import java.util.ArrayList;
import java.util.List;

import com.subtitlescorrector.core.domain.translation.TranslationLine;
import com.subtitlescorrector.core.domain.translation.TranslationResponse;
import com.subtitlescorrector.core.domain.translation.TranslationResponseBase;

public class LibreResponse implements TranslationResponseBase{

	List<DetectedLanguage> detectedLanguage;
	List<String> translatedText;

	public List<DetectedLanguage> getDetectedLanguage() {
		return detectedLanguage;
	}
	public void setDetectedLanguage(List<DetectedLanguage> detectedLanguage) {
		this.detectedLanguage = detectedLanguage;
	}
	public List<String> getTranslatedText() {
		return translatedText;
	}
	public void setTranslatedText(List<String> translatedText) {
		this.translatedText = translatedText;
	}
	
	@Override
	public TranslationResponse toTranslationResponse() {
	
		TranslationResponse translationResponse = new TranslationResponse();
		List<TranslationLine> lines = new ArrayList<>();
		for(String text : translatedText) {
			TranslationLine line = new TranslationLine();
			line.setText(text);
			line.setDetectedSourceLanguage(detectedLanguage.get(0).language); //we assume all lines are in the same language
		}
		translationResponse.setTranslations(lines);
		return translationResponse;
	}

}
