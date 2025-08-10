package com.subtitlescorrector.core.service;

import org.springframework.web.multipart.MultipartFile;

import com.subtitlescorrector.core.domain.RequestValidatorStatus;


public interface RequestValidator {

	RequestValidatorStatus validateSubtitlesUploadRateLimit(String userIp);
	RequestValidatorStatus validateFileSize(MultipartFile file);

}