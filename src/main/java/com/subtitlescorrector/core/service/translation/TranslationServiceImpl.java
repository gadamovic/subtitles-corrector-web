package com.subtitlescorrector.core.service.translation;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.domain.deepl.DeepLUsageData;
import com.subtitlescorrector.core.domain.translation.SubtitleTranslationDataResponse;
import com.subtitlescorrector.core.domain.translation.TranslationLanguage;
import com.subtitlescorrector.core.port.DeepLUsageMetricsPort;
import com.subtitlescorrector.core.port.DeeplClientPort;
import com.subtitlescorrector.core.port.SubtitlesCloudStoragePort;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.Util;

@Service
public class TranslationServiceImpl implements TranslationService{

	Logger log = LoggerFactory.getLogger(TranslationServiceImpl.class);
	
	@Autowired
	UserData user;
	
	@Autowired
	SubtitlesCloudStoragePort s3Service;
	
	@Autowired
	DeeplClientPort deepLClient;
	
	@Autowired
	DeepLUsageMetricsPort usageMetrics;
	
	@Override
	public SubtitleTranslationDataResponse translate(File file, TranslationLanguage language) {
		
		List<String> lines = FileUtil.loadTextFile(file);
		
		MDC.put("subtitle_name", file.getName());
		log.info("Uploading file for translation...");
		MDC.remove("subtitle_name");
		
		String s3Key = user.getUserId() + "_" + file.getName();
		s3Service.storeIfProd("translation_" + language.getDisplayName() + "_" + s3Key, file);
		
		Charset detectedEncoding = FileUtil.detectEncodingOfFile(file);
		SubtitleFormat sourceFormat = Util.detectSubtitleFormat(lines);
		
		SubtitleTranslationDataResponse response = new SubtitleTranslationDataResponse();
		response.setDetectedEncoding(detectedEncoding.displayName());
		response.setDetectedSourceFormat(sourceFormat);
		response.setFilename(file.getName());
		response.setNumberOfLines(lines.size());
		
		//List<String> translated = deepLClient.translate(lines);
		DeepLUsageData data = deepLClient.getUsageInfo();
		
		long current = data.getCharacterCount();
		long limit = data.getCharacterLimit();
		
		usageMetrics.updateRemainingCount(limit - current);
		usageMetrics.updatePercentUsed((double) current / (double) limit * 100.0);
		return response;
		
	}

}
