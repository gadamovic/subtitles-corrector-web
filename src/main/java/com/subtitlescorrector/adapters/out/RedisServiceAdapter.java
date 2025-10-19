package com.subtitlescorrector.adapters.out;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.UserSubtitleConversionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.UserSubtitleCorrectionCurrentVersionMetadata;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.service.conversion.AssSubtitleConversionFileData;
import com.subtitlescorrector.core.service.conversion.SrtSubtitleConversionFileData;
import com.subtitlescorrector.core.service.conversion.VttSubtitleConversionFileData;
import com.subtitlescorrector.core.util.JsonSerializationUtil;

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
	
	public void addUserSubtitleCurrentVersion(String jsonData, String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			jedis.setex(RedisSchema.createUserSubtitleCurrentVersionKey(userId), USER_SUBTITLE_CURRENT_VERSION_CACHE_TTL, jsonData);
			
		}
	}
	
	
	public void addUserSubtitleConversionData(String data, String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			jedis.setex(RedisSchema.createUserSubtitleConversionDataKey(userId), USER_SUBTITLE_CONVERSION_DATA_CACHE_TTL, data);
			
		}
	}
	
	public String getUserSubtitleCurrentVersionJson(String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			String jsonResult = jedis.get(RedisSchema.createUserSubtitleCurrentVersionKey(userId));
			return jsonResult;
		}
	}
	
	public VttSubtitleConversionFileData getVttUserSubtitleConversionData(String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			String jsonResult = jedis.get(RedisSchema.createUserSubtitleCurrentVersionKey(userId));
			return JsonSerializationUtil.jsonToVttSubtitleConversionFileData(jsonResult);
		}
	}
	
	public AssSubtitleConversionFileData getAssUserSubtitleConversionData(String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			String jsonResult = jedis.get(RedisSchema.createUserSubtitleCurrentVersionKey(userId));
			return JsonSerializationUtil.jsonToAssSubtitleConversionFileData(jsonResult);
		}
	}
	
	public SrtSubtitleConversionFileData getSrtUserSubtitleConversionData(String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			
			String jsonResult = jedis.get(RedisSchema.createUserSubtitleCurrentVersionKey(userId));
			return JsonSerializationUtil.jsonToSrtSubtitleConversionFileData(jsonResult);
		}
	}

	@Override
	public UserSubtitleCorrectionCurrentVersionMetadata getUsersLastUpdatedSubtitleFileMetadata(String userId) {
		
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			String jsonResult = jedis.get(RedisSchema.createUserSubtitleCurrentVersionFileMetadataKey(userId));
			return JsonSerializationUtil.jsonToSubtitleCurrentVersionMetadata(jsonResult);
		}
	}

	@Override
	public void addUsersLastUpdatedSubtitleFileMetadata(UserSubtitleCorrectionCurrentVersionMetadata metadata, String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			jedis.setex(RedisSchema.createUserSubtitleCurrentVersionFileMetadataKey(userId), USER_SUBTITLE_CURRENT_VERSION_CACHE_TTL, JsonSerializationUtil.userSubtitleCurrentVersionMetadataToJson(metadata));
		}
	}

	@Override
	public void addUsersLastUpdatedSubtitleConversionFileMetadata(UserSubtitleConversionCurrentVersionMetadata metadata,
			String userId) {
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			jedis.setex(RedisSchema.createUserSubtitleCurrentVersionFileMetadataKey(userId), USER_SUBTITLE_CURRENT_VERSION_CACHE_TTL, JsonSerializationUtil.userSubtitleConversionCurrentVersionMetadataToJson(metadata));
		}
	}
	
	@Override
	public UserSubtitleConversionCurrentVersionMetadata getUsersLastUpdatedSubtitleConversionFileMetadata(String userId) {
		
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			String jsonResult = jedis.get(RedisSchema.createUserSubtitleCurrentVersionFileMetadataKey(userId));
			return JsonSerializationUtil.jsonToUserSubtitleConversionCurrentVersionMetadata(jsonResult);
		}
	}
	
}
