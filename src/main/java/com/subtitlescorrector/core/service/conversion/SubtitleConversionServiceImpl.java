package com.subtitlescorrector.core.service.conversion;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.ConversionParameters;
import com.subtitlescorrector.core.domain.SubtitleConversionFileData;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.port.SubtitlesCloudStoragePort;
import com.subtitlescorrector.core.service.converters.SubtitlesConverterFactory;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.Util;

@Service
public class SubtitleConversionServiceImpl implements SubtitleConversionService {

	@Autowired
	SubtitlesConverterFactory converterFactory;
	
	@Autowired
	ExternalCacheServicePort redisService;
	
	@Autowired
	UserData user;
	
	@Autowired
	SubtitlesCloudStoragePort s3Service;
	
	Logger log = LoggerFactory.getLogger(SubtitleConversionServiceImpl.class);
	
	@Override
	public SubtitleConversionFileData applyConversionOperations(ConversionParameters conversionParameters, File uploadedFile, List<String> lines, BomData bomData) {
		
		log.info("Converting a file");
		
		MDC.put("subtitle_name", conversionParameters.getOriginalFilename());
		log.info("Uploading file for conversion...");
		MDC.remove("subtitle_name");
		
		String s3Key = user.getWebSocketSessionId() + "_" + uploadedFile.getName();
		s3Service.storeIfProd("pre-conversion_" + s3Key, uploadedFile);
		
		Charset detectedEncoding = FileUtil.detectEncodingOfFile(uploadedFile);
		
		SubtitleFormat sourceFormat = Util.detectSubtitleFormat(lines);
		
		SubtitleConversionFileData conversionFileData = new SubtitleConversionFileData();
		conversionFileData.setFilename(conversionParameters.getOriginalFilename());
		conversionFileData.setSourceFormat(sourceFormat);
		conversionFileData.setDetectedEncoding(detectedEncoding.displayName());
		conversionFileData.setBomData(bomData);
		
		conversionFileData.setLines(converterFactory.getConverter(sourceFormat).convertToSubtitleUnits(lines));
		redisService.addUserSubtitleConversionData(conversionFileData, user.getUserId());
		
		return conversionFileData;
	}

}
