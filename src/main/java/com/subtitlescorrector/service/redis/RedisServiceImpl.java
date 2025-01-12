package com.subtitlescorrector.service.redis;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.util.Util;

import io.micrometer.common.util.StringUtils;
import redis.clients.jedis.Jedis;

@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	RedisConnectionProvider redisConnection;
	
	private static final int S3_LAST_UPLOAD_CACHE_TTL = 3600;
	private static final int USER_WEBSOCKET_SESSION_CACHE_TTL = 60 * 60 * 3;
	private static final int NUMBER_OF_EMAILS_CACHE_TTL = 60 * 60 * 3;
	private static final int USER_SUBTITLE_CURRENT_VERSION_CACHE_TTL = 60 * 60 * 3;
	
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
	public Integer getAndIncrementNumberOfEmailsInCurrentHour() {
		
		Integer numberOfEmailsInt = null;
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			String numberOfEmails = jedis.get(RedisSchema.createNumberOfEmailsSentInCurrentHour());
			
			if(StringUtils.isNotBlank(numberOfEmails)) {
				numberOfEmailsInt = Integer.parseInt(numberOfEmails);
				numberOfEmailsInt++;
				jedis.setex(RedisSchema.createNumberOfEmailsSentInCurrentHour(), NUMBER_OF_EMAILS_CACHE_TTL, numberOfEmailsInt.toString());
			}else {
				jedis.setex(RedisSchema.createNumberOfEmailsSentInCurrentHour(), NUMBER_OF_EMAILS_CACHE_TTL, "1");
				numberOfEmailsInt = 1;
			}
			
			
		}
		return numberOfEmailsInt;
	}
	
	public void addUserSubtitleCurrentVersion(SubtitleFileData data, String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			jedis.setex(RedisSchema.createUserSubtitleCurrentVersionKey(userId), USER_SUBTITLE_CURRENT_VERSION_CACHE_TTL, Util.subtitleUnitDataListToJson(data));
			
		}
	}
	
	public SubtitleFileData getUserSubtitleCurrentVersion(String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			String jsonResult = jedis.get(RedisSchema.createUserSubtitleCurrentVersionKey(userId));
			return Util.jsonToSubtitleUnitDataList(jsonResult);
		}
	}
	
}
