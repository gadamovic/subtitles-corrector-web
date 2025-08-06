package com.subtitlescorrector.adapters.out;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.SubtitleConversionFileData;
import com.subtitlescorrector.core.domain.SubtitleFileData;
import com.subtitlescorrector.core.domain.SubtitleUnitData;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.util.Util;

import io.micrometer.common.util.StringUtils;
import redis.clients.jedis.Jedis;

@Service
public class RedisServiceAdapter implements ExternalCacheServicePort {

	@Autowired
	RedisConnectionProvider redisConnection;
	
	private static final int S3_LAST_UPLOAD_CACHE_TTL = 3600;
	private static final int USER_WEBSOCKET_SESSION_CACHE_TTL = 60 * 60 * 3;
	private static final int NUMBER_OF_EMAILS_CACHE_TTL = 60 * 60 * 3;
	private static final int USER_SUBTITLE_CURRENT_VERSION_CACHE_TTL = 60 * 60 * 3;
	private static final int USER_SUBTITLE_CONVERSION_DATA_CACHE_TTL = USER_SUBTITLE_CURRENT_VERSION_CACHE_TTL;
	private static final int NUMBER_OF_PROCESSED_SUBTITLES_PER_USER_TTL = 60 * 60;
	
	@Override
	public void updateLastS3UploadTimestamp(String ip) {
	
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			jedis.setex(RedisSchema.createLastS3UploadForIp(ip), S3_LAST_UPLOAD_CACHE_TTL, LocalDateTime.now().toString());
		}
		
	}
	
	public LocalDateTime getLastS3UploadTimestamp(String ip){
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			String strValue = jedis.get(RedisSchema.createLastS3UploadForIp(ip));
			if(strValue != null) {
				LocalDateTime timestamp = LocalDateTime.parse(strValue);
				return timestamp;
			}else {
				return null;
			}
		}
	}
	
	public void addWebSocketUserToCache(String userId, String webSocketSessionId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			jedis.setex(RedisSchema.createWebsocketUserIdWebSocketSessionId(userId), USER_WEBSOCKET_SESSION_CACHE_TTL, webSocketSessionId);
		}
	}
	
	public String getWebSocketSessionIdForUser(String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			return jedis.get(RedisSchema.createWebsocketUserIdWebSocketSessionId(userId));
		}
	}
	
	@Override
	public Integer incrementAndGetNumberOfEmailsInCurrentHour() {
		
		Integer numberOfEmailsInt = null;
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			String numberOfEmails = jedis.get(RedisSchema.createNumberOfEmailsSentInCurrentHour());
			
			if(StringUtils.isNotBlank(numberOfEmails)) {
				numberOfEmailsInt = Integer.parseInt(numberOfEmails) + 1;
				jedis.setex(RedisSchema.createNumberOfEmailsSentInCurrentHour(), NUMBER_OF_EMAILS_CACHE_TTL, numberOfEmailsInt.toString());
			}else {
				jedis.setex(RedisSchema.createNumberOfEmailsSentInCurrentHour(), NUMBER_OF_EMAILS_CACHE_TTL, "1");
				numberOfEmailsInt = 1;
			}
			
			
		}
		return numberOfEmailsInt;
	}
	
	public Integer incrementAndGetNumberOfSubtitlesProcessedByUserInCurrentTimeInterval(String userId) {
		
		String key = RedisSchema.createNumberOfSubtitlesPerUserPerTimeIntervalKey(userId);
		Integer numberOfSubtitlesInt = null;
		
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			String numberOfSubtitles = jedis.get(key);
			if(StringUtils.isNotBlank(numberOfSubtitles)) {
				numberOfSubtitlesInt = Integer.valueOf(numberOfSubtitles) + 1;
				jedis.setex(key, NUMBER_OF_PROCESSED_SUBTITLES_PER_USER_TTL, numberOfSubtitlesInt.toString());
			}else {
				jedis.setex(key, NUMBER_OF_PROCESSED_SUBTITLES_PER_USER_TTL, "1");
				numberOfSubtitlesInt = 1;
			}
			
		}
		
		return numberOfSubtitlesInt;
	}
	
	public void addUserSubtitleCurrentVersion(SubtitleFileData data, String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			jedis.setex(RedisSchema.createUserSubtitleCurrentVersionKey(userId), USER_SUBTITLE_CURRENT_VERSION_CACHE_TTL, Util.subtitleFileDataToJson(data));
			
		}
	}
	
	
	public void addUserSubtitleConversionData(SubtitleConversionFileData data, String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			jedis.setex(RedisSchema.createUserSubtitleConversionDataKey(userId), USER_SUBTITLE_CONVERSION_DATA_CACHE_TTL, Util.subtitleConversionFileDataToJson(data));
			
		}
	}
	
	public SubtitleFileData getUserSubtitleCurrentVersion(String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			String jsonResult = jedis.get(RedisSchema.createUserSubtitleCurrentVersionKey(userId));
			return Util.jsonToSubtitleFileData(jsonResult);
		}
	}
	
	public SubtitleConversionFileData getUserSubtitleConversionData(String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			String jsonResult = jedis.get(RedisSchema.createUserSubtitleCurrentVersionKey(userId));
			return Util.jsonToSubtitleConversionFileData(jsonResult);
		}
	}
	
}
