package com.subtitlescorrector.service.subtitles.corrections;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.ConversionParameters;
import com.subtitlescorrector.domain.SubtitleConversionFileData;
import com.subtitlescorrector.domain.SubtitleFormat;
import com.subtitlescorrector.service.redis.RedisService;
import com.subtitlescorrector.service.subtitles.SubtitlesConverterFactory;
import com.subtitlescorrector.util.FileUtil;
import com.subtitlescorrector.util.Util;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SubtitleConversionServiceImpl implements SubtitleConversionService {

	@Autowired
	SubtitlesConverterFactory converterFactory;
	
	@Autowired
	RedisService redisService;
	
	@Override
	public SubtitleConversionFileData applyConversionOperations(ConversionParameters conversionParameters, File uploadedFile,
			HttpServletRequest request, String originalFilename) {
		
		List<String> lines = FileUtil.loadTextFile(uploadedFile);
		SubtitleFormat sourceFormat = Util.detectSubtitleFormat(lines);
		SubtitleFormat targetFormat = conversionParameters.getTargetFormat();
		
		SubtitleConversionFileData conversionFileData = new SubtitleConversionFileData();
		conversionFileData.setFilename(originalFilename);
		conversionFileData.setSourceFormat(sourceFormat);
		conversionFileData.setTargetFormat(targetFormat);
		
		conversionFileData.setLines(converterFactory.getConverter(sourceFormat).convertToSubtitleUnits(lines));
		redisService.addUserSubtitleConversionData(conversionFileData, conversionParameters.getUserId());
		
		return conversionFileData;
	}

}
