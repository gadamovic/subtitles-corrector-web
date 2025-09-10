package com.subtitlescorrector.core.service.corrections;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.SubtitleFileData;
import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;

/**
 * Service that encapsulates general subtitle file correction operations. It is meant to decouple multiple
 * business capabilities that are supported in the application (currently correctors and conversions)
 * @author Gavrilo Adamovic
 *
 */
@Service
public class SubtitlesCorrectionServiceImpl implements SubtitlesCorrectionService {

	@Autowired
	SubtitlesFileProcessor processor;
	
	@Autowired
	ExternalCacheServicePort redisService;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	UserData user;
	
	public SubtitleFileData applyCorrectionOperations(AdditionalData clientParameters, File uploadedFile, List<String> lines, BomData bomData) {

		SubtitleFileData data = processor.process(uploadedFile, lines, clientParameters, bomData);
		//save uploaded and server-corrected version as the first version
		redisService.addUserSubtitleCurrentVersion(data, user.getUserId());
		
		return data;
		
	}
	
}
