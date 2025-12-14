package com.subtitlescorrector.core.port;

import java.util.List;

import com.subtitlescorrector.core.domain.deepl.DeepLUsageData;

public interface DeeplClientPort {
	public List<String> translate(List<String> lines);
	public DeepLUsageData getUsageInfo(); 
}
