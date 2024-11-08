package com.subtitlescorrector.service.redis;

import java.time.LocalDateTime;

public interface RedisService {

	void updateLastS3UploadTimestamp();
	public LocalDateTime getLastS3UploadTimestamp();
}