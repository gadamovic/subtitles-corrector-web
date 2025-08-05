package com.subtitlescorrector.core.service;

import com.subtitlescorrector.core.domain.UserSubtitleData;

public interface SubtitleFileProviderForUser {
	public UserSubtitleData provideFileForUser(String userId);
}
