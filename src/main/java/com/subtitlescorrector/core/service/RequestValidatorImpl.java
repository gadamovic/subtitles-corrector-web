package com.subtitlescorrector.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.RequestValidatorStatus;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;

@Service
public class RequestValidatorImpl implements RequestValidator {

	@Autowired
	ExternalCacheServicePort redisPort;
	
	@Autowired
	ApplicationProperties properties;
	
	@Override
	public RequestValidatorStatus validateSubtitlesUploadRateLimit(String userIp) {
		Integer numberOfSubtitles = redisPort.incrementAndGetNumberOfSubtitlesProcessedByUserInCurrentTimeInterval(userIp);
		if(numberOfSubtitles > properties.getSubtitlesPerUserPerTimeIntervalLimit()) {
			return RequestValidatorStatus.SUBTITLES_PROCESSED_LIMIT_EXCEEDED_FOR_IP;
		}else {
			return RequestValidatorStatus.OK;
		}
	}

	@Override
	public RequestValidatorStatus validateFileSize(MultipartFile file) {
		
		if((double) (file.getSize() / 1024.0) > properties.getFileSizeUploadLimitKb()) {
			return RequestValidatorStatus.FILE_TOO_BIG;
		}
		
		return RequestValidatorStatus.OK;
	}

}
