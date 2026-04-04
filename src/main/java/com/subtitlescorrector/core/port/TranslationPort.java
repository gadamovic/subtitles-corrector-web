package com.subtitlescorrector.core.port;

import java.util.List;

import com.subtitlescorrector.core.domain.deepl.DeepLUsageData;
import com.subtitlescorrector.core.domain.translation.TranslationResponse;

public interface TranslationPort {
	public TranslationResponse translate(List<String> lines, String isoLanguage);
	public DeepLUsageData getUsageInfo();
}
