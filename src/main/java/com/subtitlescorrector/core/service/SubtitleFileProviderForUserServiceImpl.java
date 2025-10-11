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
import com.subtitlescorrector.core.service.conversion.VttSubtitleConversionFileData;
import com.subtitlescorrector.core.service.converters.SubtitlesConverterFactory;
import com.subtitlescorrector.core.service.corrections.ass.AssSubtitleLinesToSubtitleUnitDataConverter;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleLinesToSubtitleUnitDataConverter;
import com.subtitlescorrector.core.service.corrections.vtt.VttSubtitleLinesToSubtitleUnitDataConverter;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.Util;

@Service
public class SubtitleFileProviderForUserServiceImpl implements SubtitleFileProviderForUser {

	@Autowired
	ExternalCacheServicePort redisService;
	
	@Autowired
	SubtitlesConverterFactory converterFactory;
	
	@Autowired
	SrtSubtitleLinesToSubtitleUnitDataConverter srtConverter;
	
	@Autowired
	VttSubtitleLinesToSubtitleUnitDataConverter vttConverter;
	
	@Autowired
	AssSubtitleLinesToSubtitleUnitDataConverter assConverter;
	
	Logger log = LoggerFactory.getLogger(SubtitleFileProviderForUserServiceImpl.class);
	
	@Override
	public UserSubtitleData provideFileForUser(String userId) {
		
		UserSubtitleData userData = new UserSubtitleData();
		
		String subtitleFileJson = redisService.getUserSubtitleCurrentVersionJson(userId);
		UserSubtitleCorrectionCurrentVersionMetadata metadata = redisService.getUsersLastUpdatedSubtitleFileMetadata(userId);

		List<String> lines = null;
		
		switch(metadata.getFormat()) {
		case SRT:
			lines = srtConverter.convertToListOfStrings(Util.jsonToSrtSubtitleFileData(subtitleFileJson).getLines(), 
					metadata.getBomData().getHasBom() && metadata.getBomData().getKeepBom());
			break;
		case VTT:
			lines = vttConverter.convertToListOfStrings(Util.jsonToVttSubtitleFileData(subtitleFileJson).getLines(), 
					metadata.getBomData().getHasBom() && metadata.getBomData().getKeepBom());
			break;
		case ASS:
			lines = assConverter.convertToListOfStrings(Util.jsonToAssSubtitleFileData(subtitleFileJson).getLines(), 
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
			lines = srtConverter.convertToListOfStrings(srtData.getLines(), metadata.getBomData().getHasBom() && metadata.getBomData().getKeepBom());
			break;
		case VTT:
			VttSubtitleConversionFileData vttData = redisService.getVttUserSubtitleConversionData(userId);
			lines = vttConverter.convertToListOfStrings(vttData.getLines(), metadata.getBomData().getHasBom() && metadata.getBomData().getKeepBom());
			break;
		case ASS:
			AssSubtitleConversionFileData assData = redisService.getAssUserSubtitleConversionData(userId);
			lines = assConverter.convertToListOfStrings(assData.getLines(), metadata.getBomData().getHasBom() && metadata.getBomData().getKeepBom());
			break;
		}
	
		File downloadableFile = new File(metadata.getFilename());
		FileUtil.writeLinesToFile(downloadableFile, lines, StandardCharsets.UTF_8);
		fileData.setFile(downloadableFile);
		fileData.setMetadata(metadata);
		
		return fileData;
	}

}
