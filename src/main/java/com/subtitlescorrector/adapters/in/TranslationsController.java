package com.subtitlescorrector.adapters.in;

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

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.RequestValidatorStatus;
import com.subtitlescorrector.core.domain.translation.SubtitleTranslationDataResponse;
import com.subtitlescorrector.core.domain.translation.TranslationLanguage;
import com.subtitlescorrector.core.port.EmailServicePort;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.port.StorageSystemPort;
import com.subtitlescorrector.core.service.RequestValidator;
import com.subtitlescorrector.core.service.translation.TranslationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api/rest/1.0/translations")
public class TranslationsController {

	Logger log = LoggerFactory.getLogger(TranslationsController.class);
	
	@Autowired
	RequestValidator validator;
	
	@Autowired
	EmailServicePort emailService;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	ExternalCacheServicePort redisService;
	
	@Autowired
	StorageSystemPort fileSystemStorageService;
	
	@Autowired
	TranslationService translationService;
	
	@RequestMapping(path="/upload", method = RequestMethod.POST)
	public ResponseEntity<SubtitleTranslationDataResponse> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("language") String language, HttpServletRequest request) {
		
		String clientIp = request.getRemoteAddr();
		
		//TODO: this things are on the start of many controllers - they can be moved to aspect
		emailService.sendEmailOnlyIfProduction("Ip: " + clientIp + "\nFilename: " + file.getOriginalFilename(), properties.getAdminEmailAddress(),
				"Somebody is uploading a subtitle for TRANSLATION");
		
		RequestValidatorStatus uploadRateCheck = validator.validateSubtitlesUploadRateLimit(clientIp);
		RequestValidatorStatus fileSizeCheck = validator.validateFileSize(file);
		
		if(fileSizeCheck == RequestValidatorStatus.FILE_TOO_BIG) {
			return getInvalidResponseEntity(HttpStatus.PAYLOAD_TOO_LARGE, "Uploaded file is too big. Maximum allowed file size is: " + properties.getFileSizeUploadLimitKb() + "kb");
		}
		
		if(properties.isProdEnvironment() && uploadRateCheck == RequestValidatorStatus.SUBTITLES_PROCESSED_LIMIT_EXCEEDED_FOR_IP) {
			return getInvalidResponseEntity(HttpStatus.TOO_MANY_REQUESTS, "The limit for processed subtitle files has been reached for user: " + clientIp + ". Please try again later");
		}else {
			redisService.updateLastS3UploadTimestamp(clientIp);
		}
		
		File storedFile = fileSystemStorageService.store(file);		
		
		SubtitleTranslationDataResponse response = translationService.translate(storedFile, TranslationLanguage.findByDisplayName(language));
		
		return ResponseEntity.ok(response);
	}
	
	private ResponseEntity<SubtitleTranslationDataResponse> getInvalidResponseEntity(HttpStatus httpStatus, String message) {
		SubtitleTranslationDataResponse data = new SubtitleTranslationDataResponse();
		data.setHttpResponseMessage(message);
		log.error(message);
		return ResponseEntity.status(httpStatus).body(data);
	}
	
}
