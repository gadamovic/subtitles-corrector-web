package com.subtitlescorrector.core.service.conversion;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.adapters.in.SaveSubtitleController;
import com.subtitlescorrector.core.domain.ConversionParameters;
import com.subtitlescorrector.core.domain.SubtitleConversionFileData;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.service.converters.SubtitlesConverterFactory;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.Util;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SubtitleConversionServiceImpl implements SubtitleConversionService {

	@Autowired
	SubtitlesConverterFactory converterFactory;
	
	@Autowired
	ExternalCacheServicePort redisService;
	
	Logger log = LoggerFactory.getLogger(SubtitleConversionServiceImpl.class);
	
	@Override
	public SubtitleConversionFileData applyConversionOperations(ConversionParameters conversionParameters, File uploadedFile,
			HttpServletRequest request, String originalFilename) {
		
		log.info("Converting a file");
		
		MDC.put("subtitle_name", originalFilename);
		log.info("Uploading file for conversion...");
		MDC.remove("subtitle_name");
		
		List<String> lines = FileUtil.loadTextFile(uploadedFile);
		
		Charset detectedEncoding = FileUtil.detectEncodingOfFile(uploadedFile);
		
		SubtitleFormat sourceFormat = Util.detectSubtitleFormat(lines);
		
		SubtitleConversionFileData conversionFileData = new SubtitleConversionFileData();
		conversionFileData.setFilename(originalFilename);
		conversionFileData.setSourceFormat(sourceFormat);
		conversionFileData.setDetectedEncoding(detectedEncoding.displayName());
		
		conversionFileData.setLines(converterFactory.getConverter(sourceFormat).convertToSubtitleUnits(lines));
		redisService.addUserSubtitleConversionData(conversionFileData, conversionParameters.getUserId());
		
		return conversionFileData;
	}

}
