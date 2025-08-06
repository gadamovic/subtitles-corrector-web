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
import com.subtitlescorrector.core.domain.SubtitleConversionFileData;
import com.subtitlescorrector.core.domain.SubtitleFileData;
import com.subtitlescorrector.core.port.EmailServicePort;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.port.StorageSystemPort;
import com.subtitlescorrector.core.service.CloudStorageRateLimiter;
import com.subtitlescorrector.core.service.conversion.SubtitleConversionService;
import com.subtitlescorrector.core.service.corrections.SubtitlesCorrectionService;
import com.subtitlescorrector.core.util.Util;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class FileUploadController {

	Logger log = LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	StorageSystemPort fileSystemStorageService;
		
	@Autowired
	ExternalCacheServicePort redisService;
	
	@Autowired
	CloudStorageRateLimiter monitor;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	EmailServicePort emailService;
	
	@Autowired
	SubtitlesCorrectionService correctionService;
	
	@Autowired
	SubtitleConversionService conversionService;
	
	@RequestMapping(path = "/upload", method = RequestMethod.POST)
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		
		String clientIp = request.getRemoteAddr();
		
		if(properties.isProdEnvironment() && !monitor.subtitleCorrectionAllowedForUser(clientIp)) {
			SubtitleFileData data = new SubtitleFileData();
			String message = "The limit for processed subtitle files has been reached for user: " + clientIp + ". Please try again later";
			log.info(message);
			data.setHttpResponseMessage(message);
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Util.subtitleFileDataToJson(data));
		}else {
			redisService.updateLastS3UploadTimestamp(clientIp);
		}
		
		File storedFile = fileSystemStorageService.store(file);		
		AdditionalData clientParameters = extractOptions(request);
		
		emailService.sendEmailOnlyIfProduction("Ip: " + clientIp + "\nFilename: " + storedFile.getName(), properties.getAdminEmailAddress(),
				"Somebody is uploading a subtitle for: " + clientParameters.getBusinessOperation().toString());

		if (clientParameters.getBusinessOperation() != null) {
			switch (clientParameters.getBusinessOperation()) {
			case CORRECTION:
				SubtitleFileData data = correctionService.applyCorrectionOperations(clientParameters, storedFile, request,
						file.getOriginalFilename());
				return ResponseEntity.ok(Util.subtitleFileDataToJson(data));
			case CONVERSION:
				SubtitleConversionFileData conversionData = conversionService.applyConversionOperations(clientParameters.getConversionParameters(), storedFile, request,
						file.getOriginalFilename());
				return ResponseEntity.ok(Util.subtitleConversionFileDataResponseToJson(Util.subtitleConversionFileDataToResponseObject(conversionData)));
			default: 
				return getInvalidResponseEntity();
			}
		}else {
			return getInvalidResponseEntity();
		}
		
	}

	private ResponseEntity<String> getInvalidResponseEntity() {
		SubtitleFileData data;
		data = new SubtitleFileData();
		data.setHttpResponseMessage("Invalid business operation provided. Contact support for more information.");
		log.error("Invalid business operation!");
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Util.subtitleFileDataToJson(data));
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
		params.setUserId(request.getParameter("userId"));
		
		return params;
	}
}

