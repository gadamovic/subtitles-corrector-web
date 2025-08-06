package com.subtitlescorrector.core.service;

import com.subtitlescorrector.core.domain.UserSubtitleConversionData;
import com.subtitlescorrector.core.domain.UserSubtitleData;

public interface SubtitleFileProviderForUser {
	public UserSubtitleData provideFileForUser(String userId);
	public UserSubtitleConversionData provideConversionFileForUser(String userId, String targetFormat);
}
