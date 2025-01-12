package com.subtitlescorrector.service.redis;

import java.time.LocalDateTime;
import java.util.List;

import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleUnitData;

public interface RedisService {

	void updateLastS3UploadTimestamp(String ip);
	public LocalDateTime getLastS3UploadTimestamp(String ip);
	public void addWebSocketUserToCache(String userId, String webSocketSessionId);
	public String getWebSocketSessionIdForUser(String userId);
	public Integer getAndIncrementNumberOfEmailsInCurrentHour();
	public void addUserSubtitleCurrentVersion(SubtitleFileData data, String userId);
	public SubtitleFileData getUserSubtitleCurrentVersion(String userId);
	
}