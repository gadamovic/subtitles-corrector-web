package com.subtitlescorrector.service;

import java.time.Duration;
import java.time.LocalDateTime;

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
	
	public boolean uploadAllowed() {
		
		LocalDateTime lastUploadTimestamp = redisService.getLastS3UploadTimestamp();
		
		LocalDateTime now = LocalDateTime.now();
		
		if(lastUploadTimestamp != null) {
			
			long minutes = Duration.between(lastUploadTimestamp, now).getSeconds() / 60L;
			
			if(Math.abs(minutes) < properties.getS3UploadCooldownMinutes()) {
				return false;
			}else {
				return true;
			}
		}else {
			return true;
		}
		
	}

	
	
}
