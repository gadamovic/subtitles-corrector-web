package com.subtitlescorrector.core.service.translation;

import java.io.File;

import com.subtitlescorrector.core.domain.translation.SubtitleTranslationDataResponse;
import com.subtitlescorrector.core.domain.translation.TranslationLanguage;

public interface TranslationService {

	public SubtitleTranslationDataResponse translate(File file, TranslationLanguage language);
	
}
