package com.subtitlescorrector.controller.rest;

import java.io.File;

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
import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.service.EmailService;
import com.subtitlescorrector.service.S3ServiceMonitor;
import com.subtitlescorrector.service.StorageService;
import com.subtitlescorrector.service.redis.RedisService;
import com.subtitlescorrector.service.subtitles.SubtitlesFileProcessor;

import jakarta.servlet.http.HttpServletRequest;

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
	public ResponseEntity<SubtitleFileData> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		
		String clientIp = request.getRemoteAddr();
		
		if(properties.isProdEnvironment() && !monitor.subtitleCorrectionAllowedForUser(clientIp)) {
			SubtitleFileData data = new SubtitleFileData();
			data.setHttpResponseMessage("The limit for processed subtitle files has been reached. Please try again later");
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(data);
		}else {
			redisService.updateLastS3UploadTimestamp(clientIp);
		}
				
		File storedFile = fileSystemStorageService.store(file);
		
		String webSocketSessionId = redisService.getWebSocketSessionIdForUser(request.getParameter("webSocketUserId"));
		AdditionalData clientParameters = extractOptions(request);
		clientParameters.setWebSocketSessionId(webSocketSessionId);
		
		SubtitleFileData data = processor.process(storedFile, clientParameters);

		//save uploaded and server-corrected version as the first version
		redisService.addUserSubtitleCurrentVersion(data, request.getParameter("webSocketUserId"));
		
		emailService.sendEmailOnlyIfProduction("Ip: " + clientIp + "\nFilename: " + file.getOriginalFilename(), properties.getAdminEmailAddress(), "Somebody is uploading a subtitle!");

		return ResponseEntity.ok(data);
		
	}

	private AdditionalData extractOptions(HttpServletRequest request) {
		
		AdditionalData params = new AdditionalData();
		
		params.setStripBTags(Boolean.parseBoolean(request.getParameter("stripBTags")));
		params.setStripITags(Boolean.parseBoolean(request.getParameter("stripITags")));
		params.setStripFontTags(Boolean.parseBoolean(request.getParameter("stripFontTags")));
		params.setStripUTags(Boolean.parseBoolean(request.getParameter("stripUTags")));
		params.setKeepBOM(Boolean.parseBoolean(request.getParameter("keepBOM")));
		params.setConvertAeToTj(Boolean.parseBoolean(request.getParameter("aeToTj")));
		params.setConvertAEToTJ(Boolean.parseBoolean(request.getParameter("AEToTJ")));
		params.setConverteToch(Boolean.parseBoolean(request.getParameter("eToch")));
		params.setConvertEToCH(Boolean.parseBoolean(request.getParameter("EToCH")));
		params.setAiEnabled(Boolean.parseBoolean(request.getParameter("aiEnabled")));
		
		return params;
	}
}

