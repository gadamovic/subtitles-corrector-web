package com.subtitlescorrector.service.redis;

import java.time.LocalDateTime;

public interface RedisService {

	void updateLastS3UploadTimestamp(String ip);
	public LocalDateTime getLastS3UploadTimestamp(String ip);
	public void addWebSocketUserToCache(String userId, String webSocketSessionId);
	public String getWebSocketSessionIdForUser(String userId);
}