package com.subtitlescorrector.service.redis;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	RedisConnectionProvider redisConnection;
	
	@Override
	public void updateLastS3UploadTimestamp() {
	
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			jedis.set(RedisSchema.createUserLastPostTimestampKey(), LocalDateTime.now().toString());
		}
		
	}
	
	public LocalDateTime getLastS3UploadTimestamp(){
		try (Jedis jedis = redisConnection.getJedisPool().getResource()) {
			String strValue = jedis.get(RedisSchema.createUserLastPostTimestampKey());
			if(strValue != null) {
				LocalDateTime timestamp = LocalDateTime.parse(strValue);
				return timestamp;
			}else {
				return null;
			}
		}
	}
	
}
