package com.subtitlescorrector.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.port.RedisServicePort;

@Service
public class CloudStorageRateLimiterImpl implements CloudStorageRateLimiter {

	@Autowired
	RedisServicePort redisPort;
	
	@Autowired
	ApplicationProperties properties;
	
	@Override
	public boolean subtitleCorrectionAllowedForUser(String userIp) {
		Integer numberOfSubtitles = redisPort.incrementAndGetNumberOfSubtitlesProcessedByUserInCurrentTimeInterval(userIp);
		return numberOfSubtitles <= properties.getSubtitlesPerUserPerTimeIntervalLimit();
	}

}
