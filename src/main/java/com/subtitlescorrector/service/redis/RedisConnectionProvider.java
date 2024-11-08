package com.subtitlescorrector.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisConnectionProvider {

	@Autowired
	ApplicationProperties properties;
	
	JedisPool jedisPool;
	
	@PostConstruct
	public void init() {
		jedisPool = new JedisPool(new JedisPoolConfig(), properties.getRedisConnectionHostAsString(),
				   properties.getRedisConnectionPortAsInt());
	}
	
	@PreDestroy
	public void preDestroy() {
		jedisPool.close();
	}
	
	public JedisPool getJedisPool() {

		return jedisPool;
	}
	
}
