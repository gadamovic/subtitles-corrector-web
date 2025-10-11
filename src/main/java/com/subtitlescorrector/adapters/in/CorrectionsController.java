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
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.RequestValidatorStatus;
import com.subtitlescorrector.core.domain.exception.InvalidBusinessOperationException;
import com.subtitlescorrector.core.port.EmailServicePort;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.port.StorageSystemPort;
import com.subtitlescorrector.core.service.RequestValidator;
import com.subtitlescorrector.core.service.UploadFileEntryPoint;
import com.subtitlescorrector.core.service.conversion.SubtitleConversionService;
import com.subtitlescorrector.core.service.conversion.VttSubtitleConversionFileData;
import com.subtitlescorrector.core.service.corrections.SubtitleCorrectionFileDataWebDto;
import com.subtitlescorrector.core.service.corrections.SubtitlesCorrectionService;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.util.Util;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api/rest/1.0/corrections")
public class CorrectionsController {

	Logger log = LoggerFactory.getLogger(CorrectionsController.class);

	@Autowired
	StorageSystemPort fileSystemStorageService;
		
	@Autowired
	ExternalCacheServicePort redisService;
	
	@Autowired
	RequestValidator validator;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	SubtitlesCorrectionService correctionService;
	
	@Autowired
	EmailServicePort emailService;
	
	@RequestMapping(path = "/upload", method = RequestMethod.POST)
	public ResponseEntity<SubtitleCorrectionFileDataWebDto> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		
		String clientIp = request.getRemoteAddr();
		
		emailService.sendEmailOnlyIfProduction("Ip: " + clientIp + "\nFilename: " + file.getOriginalFilename(), properties.getAdminEmailAddress(),
				"Somebody is uploading a subtitle for CORRECTION");

		
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
		AdditionalData clientParameters = extractOptions(request);
		clientParameters.setOriginalFilename(file.getOriginalFilename());
		
		SubtitleCorrectionFileDataWebDto response = correctionService.applyCorrectionOperations(clientParameters, storedFile);
		return ResponseEntity.ok(response);

		
	}

	private ResponseEntity<SubtitleCorrectionFileDataWebDto> getInvalidResponseEntity(HttpStatus httpStatus, String message) {
		SubtitleCorrectionFileDataWebDto data = new SubtitleCorrectionFileDataWebDto();
		data.setHttpResponseMessage(message);
		log.error(message);
		return ResponseEntity.status(httpStatus).body(data);
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
		params.setBusinessOperation(request.getParameter("businessOperation"));
		
		return params;
	}
}

