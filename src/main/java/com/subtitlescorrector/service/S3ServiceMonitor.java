package com.subtitlescorrector.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.service.redis.RedisService;

@Service
public class S3ServiceMonitor {

	@Autowired
	RedisService redisService;
	
	@Autowired
	ApplicationProperties properties;
	
	public boolean subtitleCorrectionAllowedForUser(String userIp) {
		Integer numberOfSubtitles = redisService.incrementAndGetNumberOfSubtitlesProcessedByUserInCurrentTimeInterval(userIp);
		return numberOfSubtitles <= properties.getSubtitlesPerUserPerTimeIntervalLimit();
	}

	
	
}
