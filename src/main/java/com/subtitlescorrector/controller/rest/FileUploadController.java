package com.subtitlescorrector.controller.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.domain.S3BucketNames;
import com.subtitlescorrector.domain.SubtitlesFileProcessorResponse;
import com.subtitlescorrector.service.EmailService;
import com.subtitlescorrector.service.S3ServiceMonitor;
import com.subtitlescorrector.service.StorageService;
import com.subtitlescorrector.service.redis.RedisService;
import com.subtitlescorrector.service.s3.S3Service;
import com.subtitlescorrector.service.subtitles.SubtitlesFileProcessor;
import com.subtitlescorrector.util.Util;

import jakarta.servlet.http.HttpServletRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class FileUploadController {

	Logger log = LoggerFactory.getLogger(FileUploadController.class);

	
	@Autowired
	StorageService fileSystemStorageService;
	
	@Autowired
	SubtitlesFileProcessor processor;
		
	@Autowired
	RedisService redisService;
	
	@Autowired
	S3ServiceMonitor monitor;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	EmailService emailService;
	
	@RequestMapping(path = "/upload", method = RequestMethod.POST)
	public ResponseEntity<List<String>> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		
		
		String clientIp = request.getRemoteAddr();
		
		if(properties.isProdEnvironment() && !monitor.subtitleCorrectionAllowedForUser(clientIp)) {
			return null;//ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Users are allowed to process one subtitle every two minutes!");
		}else {
			redisService.updateLastS3UploadTimestamp(clientIp);
		}
				
		File storedFile = fileSystemStorageService.store(file);
		String webSocketSessionId = redisService.getWebSocketSessionIdForUser(request.getParameter("webSocketUserId"));
		SubtitlesFileProcessorResponse response = processor.process(storedFile, webSocketSessionId);

		emailService.sendEmailOnlyIfProduction("Ip: " + clientIp + "\nFilename: " + file.getOriginalFilename(), properties.getAdminEmailAddress(), "Somebody is uploading a subtitle!");

		return ResponseEntity.ok(response.getLines());
		
	}
}

