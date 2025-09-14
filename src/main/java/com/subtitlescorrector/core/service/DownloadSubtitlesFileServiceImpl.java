package com.subtitlescorrector.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.UserSubtitleConversionData;
import com.subtitlescorrector.core.domain.UserSubtitleData;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.port.HttpServletResponseFileWriterPort;
import com.subtitlescorrector.core.port.SubtitlesCloudStoragePort;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class DownloadSubtitlesFileServiceImpl implements DownloadSubtitlesFileService {

	@Autowired
	SubtitleFileProviderForUser subtitleFileProvider;
	
	@Autowired
	ExternalCacheServicePort cacheService;
	
	@Autowired
	SubtitlesCloudStoragePort cloudStorageService;
	
	@Autowired
	HttpServletResponseFileWriterPort responseWriter;
	
	Logger log = LoggerFactory.getLogger(DownloadSubtitlesFileServiceImpl.class);
	
	@Override
	public void downloadSubtitlesFileForUser(String userId, HttpServletResponse response) {

		UserSubtitleData userData = subtitleFileProvider.provideFileForUser(userId);

		MDC.put("subtitle_name", userData.getSubtitleFileData().getFilename());
		log.info("Downloading corrected file...");
		MDC.remove("subtitle_name");
		
		String webSocketSessionIdAsFilename = cacheService.getWebSocketSessionIdForUser(userId);
		cloudStorageService.storeIfProd("v3_" + webSocketSessionIdAsFilename + "_" + userData.getSubtitleFileData().getFilename(), userData.getFile());

		responseWriter.writeFileToResponse(userData, response);
		
	}
	
	@Override
	public void downloadSubtitlesConvertedFileForUser(String userId, String targetFormat, HttpServletResponse response) {

		UserSubtitleConversionData userData = subtitleFileProvider.provideConversionFileForUser(userId, targetFormat);
		
		MDC.put("subtitle_name", userData.getData().getFilename());
		MDC.put("source_format", userData.getData().getSourceFormat().toString());
		MDC.put("target_format", targetFormat);
		log.info("Downloading converted file...");
		MDC.remove("filename");
		MDC.remove("source_format");
		MDC.remove("target_format");
		
		String webSocketSessionIdAsFilename = cacheService.getWebSocketSessionIdForUser(userId);
		cloudStorageService.storeIfProd("post-conversion_" + webSocketSessionIdAsFilename + "_" + userData.getData().getFilename(), userData.getFile());
		
		responseWriter.writeFileToResponse(userData, response);
		
	}
	
}
