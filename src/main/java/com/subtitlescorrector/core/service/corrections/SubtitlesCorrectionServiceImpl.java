package com.subtitlescorrector.core.service.corrections;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SubtitleFileData;
import com.subtitlescorrector.core.port.EmailServicePort;
import com.subtitlescorrector.core.port.RedisServicePort;

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
	RedisServicePort redisService;
	
	@Autowired
	ApplicationProperties properties;
	
	public SubtitleFileData applyCorrectionOperations(AdditionalData clientParameters, File uploadedFile, HttpServletRequest request, String originalFileName) {
				
		String webSocketSessionId = redisService.getWebSocketSessionIdForUser(request.getParameter("webSocketUserId"));

		clientParameters.setWebSocketSessionId(webSocketSessionId);
		
		SubtitleFileData data = processor.process(uploadedFile, clientParameters);

		//save uploaded and server-corrected version as the first version
		redisService.addUserSubtitleCurrentVersion(data, request.getParameter("webSocketUserId"));
		
		return data;
		
	}
	
}
