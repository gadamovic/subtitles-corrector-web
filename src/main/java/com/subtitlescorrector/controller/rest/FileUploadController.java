package com.subtitlescorrector.controller.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
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
	S3Service s3Service;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	Util util;
	
	@Autowired
	S3ServiceMonitor monitor;
	
	@Autowired
	ApplicationProperties properties;
	
	@RequestMapping(path = "/upload", method = RequestMethod.POST)
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		
		
		String clientIp = request.getRemoteAddr();
		if(properties.isProdEnvironment() && !monitor.subtitleCorrectionAllowedForUser(clientIp)) {
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Users are allowed to process one subtitle every two minutes!");
		}else {
			redisService.updateLastS3UploadTimestamp(clientIp);
		}
		
		String s3KeyUUIDPrefix = UUID.randomUUID().toString();
		
		File storedFile = fileSystemStorageService.store(file);
		
		String webSocketSessionId = redisService.getWebSocketSessionIdForUser(request.getParameter("webSocketUserId"));
		
		File processedFile = processor.process(storedFile, s3KeyUUIDPrefix, webSocketSessionId);

		String s3Key = s3KeyUUIDPrefix + processedFile.getName();
		
		if(!properties.isProdEnvironment()) {
			return ResponseEntity.ok("http://www.dummyurl.com");
		}
		
		log.info("Attempting upload to s3...");
		s3Service.uploadFileToS3(s3Key, S3BucketNames.SUBTITLES_UPLOADED_FILES.getBucketName(), processedFile);
		
		try {
			Files.delete(storedFile.toPath());
			Files.delete(processedFile.toPath());
		} catch (IOException e) {
			log.error("Error deleting files!",e);
		}
		
		S3Presigner presigner = S3Presigner.create();
		GetObjectPresignRequest getObjectRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(r -> r.bucket(S3BucketNames.SUBTITLES_UPLOADED_FILES.getBucketName())
                					    .key(s3Key)
                					    .responseContentDisposition("attachment; filename=\"" + util.makeFilenameForDownloadFromS3Key(s3Key) +"\""))
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(getObjectRequest);
        
		return ResponseEntity.ok(presignedRequest.url().toString());
		
	}
}

