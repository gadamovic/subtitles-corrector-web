package com.subtitlescorrector.core.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.adapters.in.SaveSubtitleController;
import com.subtitlescorrector.core.domain.SubtitleConversionFileData;
import com.subtitlescorrector.core.domain.SubtitleFileData;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.UserSubtitleConversionData;
import com.subtitlescorrector.core.domain.UserSubtitleData;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.service.converters.SubtitlesConverterFactory;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.Util;

@Service
public class SubtitleFileProviderForUserServiceImpl implements SubtitleFileProviderForUser {

	@Autowired
	ExternalCacheServicePort redisService;
	
	@Autowired
	SubtitlesConverterFactory converterFactory;
	
	Logger log = LoggerFactory.getLogger(SubtitleFileProviderForUserServiceImpl.class);
	
	@Override
	public UserSubtitleData provideFileForUser(String userId) {
		
		UserSubtitleData userData = new UserSubtitleData();
		
		SubtitleFileData data = redisService.getUserSubtitleCurrentVersion(userId);
		userData.setSubtitleFileData(data);

		List<String> lines = converterFactory.getConverter(data.getFormat()).convertToListOfStrings(data.getLines());
		
		if(data.getHasBom() && data.getKeepBom()) {
			lines = Util.addBom(lines);
		}
		
		File downloadableFile = new File(data.getFilename());		
		FileUtil.writeLinesToFile(downloadableFile, lines, StandardCharsets.UTF_8);
		userData.setFile(downloadableFile);
		
		return userData;
	}

	@Override
	public UserSubtitleConversionData provideConversionFileForUser(String userId, String targetFormat) {
		
		UserSubtitleConversionData fileData = new UserSubtitleConversionData();
		
		SubtitleConversionFileData data = redisService.getUserSubtitleConversionData(userId);
		
		List<String> lines = converterFactory.getConverter(SubtitleFormat.valueOf(SubtitleFormat.class, targetFormat)).convertToListOfStrings(data.getLines());
		
		File downloadableFile = new File(data.getFilename());
		FileUtil.writeLinesToFile(downloadableFile, lines, StandardCharsets.UTF_8);
		
		fileData.setData(data);
		fileData.setFile(downloadableFile);
		
		return fileData;
	}

}
