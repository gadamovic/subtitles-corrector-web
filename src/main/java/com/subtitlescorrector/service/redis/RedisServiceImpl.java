package com.subtitlescorrector.service.redis;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	RedisConnectionProvider redisConnection;
	
	private static final int CACHE_TTL = 3600;
	
	@Override
	public void updateLastS3UploadTimestamp(String ip) {
	
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			jedis.setex(RedisSchema.createUserLastPostTimestampKey(ip), CACHE_TTL, LocalDateTime.now().toString());
		}
		
	}
	
	public LocalDateTime getLastS3UploadTimestamp(String ip){
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			String strValue = jedis.get(RedisSchema.createUserLastPostTimestampKey(ip));
			if(strValue != null) {
				LocalDateTime timestamp = LocalDateTime.parse(strValue);
				return timestamp;
			}else {
				return null;
			}
		}
	}
	
}
