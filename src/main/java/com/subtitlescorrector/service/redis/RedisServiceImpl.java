package com.subtitlescorrector.service.redis;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	RedisConnectionProvider redisConnection;
	
	private static final int S3_LAST_UPLOAD_CACHE_TTL = 3600;
	private static final int USER_WEBSOCKET_SESSION_CACHE_TTL = 10800;
	
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
	
}
