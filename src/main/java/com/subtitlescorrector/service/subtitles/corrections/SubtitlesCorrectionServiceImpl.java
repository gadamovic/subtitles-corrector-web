package com.subtitlescorrector.service.subtitles.corrections;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.service.EmailService;
import com.subtitlescorrector.service.redis.RedisService;
import com.subtitlescorrector.service.subtitles.SubtitlesFileProcessor;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service that encapsulates general subtitle file correction operations. It is meant to decouple multiple
 * business capabilities that are supported in the application (currently correctors and convertsions)
 * @author Gavrilo Adamovic
 *
 */
@Service
public class SubtitlesCorrectionServiceImpl implements SubtitlesCorrectionService {

	@Autowired
	SubtitlesFileProcessor processor;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	EmailService emailService;
	
	public SubtitleFileData applyCorrectionOperations(AdditionalData clientParameters, File uploadedFile, HttpServletRequest request, String originalFileName) {
		
		String clientIp = request.getRemoteAddr();
		
		String webSocketSessionId = redisService.getWebSocketSessionIdForUser(request.getParameter("webSocketUserId"));

		clientParameters.setWebSocketSessionId(webSocketSessionId);
		
		SubtitleFileData data = processor.process(uploadedFile, clientParameters);

		//save uploaded and server-corrected version as the first version
		redisService.addUserSubtitleCurrentVersion(data, request.getParameter("webSocketUserId"));
		
		emailService.sendEmailOnlyIfProduction("Ip: " + clientIp + "\nFilename: " + originalFileName, properties.getAdminEmailAddress(), "Somebody is uploading a subtitle!");

		return data;
		
	}
	
}
