package com.subtitlescorrector.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;

@Service
public class CloudStorageRateLimiterImpl implements CloudStorageRateLimiter {

	@Autowired
	ExternalCacheServicePort redisPort;
	
	@Autowired
	ApplicationProperties properties;
	
	@Override
	public boolean subtitleCorrectionAllowedForUser(String userIp) {
		Integer numberOfSubtitles = redisPort.incrementAndGetNumberOfSubtitlesProcessedByUserInCurrentTimeInterval(userIp);
		return numberOfSubtitles <= properties.getSubtitlesPerUserPerTimeIntervalLimit();
	}

}
