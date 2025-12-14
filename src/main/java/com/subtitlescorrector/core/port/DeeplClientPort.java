package com.subtitlescorrector.core.port;

import java.util.List;

import com.subtitlescorrector.core.domain.deepl.DeepLResponse;
import com.subtitlescorrector.core.domain.deepl.DeepLUsageData;

public interface DeeplClientPort {
	public DeepLResponse translate(List<String> lines, String isoLanguage);
	public DeepLUsageData getUsageInfo();
}
