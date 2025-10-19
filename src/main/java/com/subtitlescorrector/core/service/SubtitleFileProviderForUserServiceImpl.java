package com.subtitlescorrector.core.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.UserSubtitleConversionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.UserSubtitleConversionData;
import com.subtitlescorrector.core.domain.UserSubtitleCorrectionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.UserSubtitleData;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.service.conversion.AssSubtitleConversionFileData;
import com.subtitlescorrector.core.service.conversion.SrtSubtitleConversionFileData;
import com.subtitlescorrector.core.service.conversion.SubtitlesFileConverter;
import com.subtitlescorrector.core.service.conversion.VttSubtitleConversionFileData;
import com.subtitlescorrector.core.service.converters.SubtitlesConverterFactory;
import com.subtitlescorrector.core.service.corrections.ass.AssSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.vtt.VttSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.JsonSerializationUtil;

@Service
public class SubtitleFileProviderForUserServiceImpl implements SubtitleFileProviderForUser {

	@Autowired
	ExternalCacheServicePort redisService;
	
	@Autowired
	SubtitlesConverterFactory converterFactory;
	
	@Autowired
	SrtSubtitleLinesToSubtitleUnitDataParser srtParser;
	
	@Autowired
	VttSubtitleLinesToSubtitleUnitDataParser vttParser;
	
	@Autowired
	AssSubtitleLinesToSubtitleUnitDataParser assParser;
	
	@Autowired
	SubtitlesFileConverter converter;
	
	Logger log = LoggerFactory.getLogger(SubtitleFileProviderForUserServiceImpl.class);
	
	@Override
	public UserSubtitleData provideFileForUser(String userId) {
		
		UserSubtitleData userData = new UserSubtitleData();
		
		String subtitleFileJson = redisService.getUserSubtitleCurrentVersionJson(userId);
		UserSubtitleCorrectionCurrentVersionMetadata metadata = redisService.getUsersLastUpdatedSubtitleFileMetadata(userId);

		List<String> lines = null;
		
		switch(metadata.getFormat()) {
		case SRT:
			lines = srtParser.convertToListOfStrings(JsonSerializationUtil.jsonToSrtSubtitleFileData(subtitleFileJson), 
					metadata.getBomData().getHasBom() && metadata.getBomData().getKeepBom());
			break;
		case VTT:
			lines = vttParser.convertToListOfStrings(JsonSerializationUtil.jsonToVttSubtitleFileData(subtitleFileJson), 
					metadata.getBomData().getHasBom() && metadata.getBomData().getKeepBom());
			break;
		case ASS:
			lines = assParser.convertToListOfStrings(JsonSerializationUtil.jsonToAssSubtitleFileData(subtitleFileJson), 
					metadata.getBomData().getHasBom() && metadata.getBomData().getKeepBom());
		}
				
		File downloadableFile = new File(metadata.getFilename());		
		FileUtil.writeLinesToFile(downloadableFile, lines, StandardCharsets.UTF_8);
		userData.setFile(downloadableFile);
		userData.setFileMetadata(metadata);
		
		return userData;
	}

	@Override
	public UserSubtitleConversionData provideConversionFileForUser(String userId, String targetFormat) {
		
		UserSubtitleConversionData fileData = new UserSubtitleConversionData();

		UserSubtitleConversionCurrentVersionMetadata metadata = redisService.getUsersLastUpdatedSubtitleConversionFileMetadata(userId);
		List<String> lines = new ArrayList<>();
		
		switch(metadata.getSourceFormat()) {
		case SRT:
			SrtSubtitleConversionFileData srtData = redisService.getSrtUserSubtitleConversionData(userId);
			lines = converter.convertAndReturnTextLines(srtData, SubtitleFormat.valueOf(targetFormat));
			break;
		case VTT:
			VttSubtitleConversionFileData vttData = redisService.getVttUserSubtitleConversionData(userId);
			lines = converter.convertAndReturnTextLines(vttData, SubtitleFormat.valueOf(targetFormat));
			break;
		case ASS:
			AssSubtitleConversionFileData assData = redisService.getAssUserSubtitleConversionData(userId);
			lines = converter.convertAndReturnTextLines(assData, SubtitleFormat.valueOf(targetFormat));
			break;
		}
	
		File downloadableFile = new File(metadata.getFilename());
		FileUtil.writeLinesToFile(downloadableFile, lines, StandardCharsets.UTF_8);
		fileData.setFile(downloadableFile);
		fileData.setMetadata(metadata);
		
		return fileData;
	}

}
