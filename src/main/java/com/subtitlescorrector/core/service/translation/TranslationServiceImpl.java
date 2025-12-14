package com.subtitlescorrector.core.service.translation;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.domain.UserSubtitleCorrectionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.ass.AssSubtitleFileData;
import com.subtitlescorrector.core.domain.ass.AssSubtitleUnitData;
import com.subtitlescorrector.core.domain.deepl.DeepLResponse;
import com.subtitlescorrector.core.domain.deepl.DeepLTranslation;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleUnitData;
import com.subtitlescorrector.core.domain.translation.SubtitleTranslationDataResponse;
import com.subtitlescorrector.core.domain.translation.TranslationLanguage;
import com.subtitlescorrector.core.domain.vtt.VttSubtitleFileData;
import com.subtitlescorrector.core.domain.vtt.VttSubtitleUnitData;
import com.subtitlescorrector.core.port.DeeplClientPort;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.port.SubtitlesCloudStoragePort;
import com.subtitlescorrector.core.service.DeepLUsageMetricsExposerService;
import com.subtitlescorrector.core.service.corrections.ass.AssSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.vtt.VttSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.JsonSerializationUtil;
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
	ExternalCacheServicePort redisService;
	
	@Autowired
	SrtSubtitleLinesToSubtitleUnitDataParser srtParser;
	
	@Autowired
	VttSubtitleLinesToSubtitleUnitDataParser vttParser;
	
	@Autowired
	AssSubtitleLinesToSubtitleUnitDataParser assParser;
	
	@Autowired
	DeepLUsageMetricsExposerService usageMetricsExposer;
	
	
	@Override
	public SubtitleTranslationDataResponse translate(File file, String originalFileName, TranslationLanguage language) {
		
		SubtitleTranslationDataResponse response = new SubtitleTranslationDataResponse();
		
		if(language == null) {
			response.setHttpResponseMessage("Language must be provided!");
			return response;
		}
		
		List<String> lines = FileUtil.loadTextFile(file);
		
		BomData bomData = new BomData();
		Util.populateBomData(bomData, lines);
		
		MDC.put("subtitle_name", originalFileName);
		log.info("Uploading file for translation...");
		MDC.remove("subtitle_name");
		
		String s3Key = user.getUserId() + "_" + originalFileName;
		s3Service.storeIfProd("translation_" + language.getDisplayName() + "_" + s3Key, file);
		
		Charset detectedEncoding = FileUtil.detectEncodingOfFile(file);
		SubtitleFormat sourceFormat = Util.detectSubtitleFormat(lines);
		
		
		String subtitleFileDataJson = null;

		
		switch (sourceFormat) {
		case SRT:
			SrtSubtitleFileData srtFileData = srtParser.convertToSubtitleUnits(lines);

			response.setNumberOfLines(srtFileData.getLines().size());
			DeepLResponse translatedSrt = deepLClient.translate(srtFileData.getLines().stream()
				.map(SrtSubtitleUnitData::getText)
				.toList(), language.getIsoCode());
			
			List<String> translatedLinesSrt = translatedSrt.getTranslations().stream()
				.map(DeepLTranslation::getText)
				.toList();
	
			if(translatedLinesSrt.size() == srtFileData.getLines().size()) {
				for(int i=0; i<translatedLinesSrt.size(); i++) {
					srtFileData.getLines().get(i).setText(translatedLinesSrt.get(i));
				}
				subtitleFileDataJson = JsonSerializationUtil.srtSubtitleFileDataToJson(srtFileData);
			}else {
				String message = "Translation error!";
				log.error(message);
				throw new RuntimeException(message);
			}

			break;
		case VTT:
			VttSubtitleFileData vttFileData = vttParser.convertToSubtitleUnits(lines);

			response.setNumberOfLines(vttFileData.getLines().size());
			DeepLResponse translated = deepLClient.translate(vttFileData.getLines().stream()
					.map(VttSubtitleUnitData::getText)
					.toList(), language.getIsoCode());
			
			List<String> translatedLinesVtt = translated.getTranslations().stream()
			.map(DeepLTranslation::getText)
			.toList();
	
			if(translatedLinesVtt.size() == vttFileData.getLines().size()) {
				for(int i=0; i<translatedLinesVtt.size(); i++) {
					vttFileData.getLines().get(i).setText(translatedLinesVtt.get(i));
				}
				subtitleFileDataJson = JsonSerializationUtil.vttSubtitleFileDataToJson(vttFileData);
			}else {
				String message = "Translation error!";
				log.error(message);
				throw new RuntimeException(message);
			}
			break;
		case ASS:
			AssSubtitleFileData assFileData = assParser.convertToSubtitleUnits(lines);

			response.setNumberOfLines(assFileData.getLines().size());
			DeepLResponse translatedAss = deepLClient.translate(assFileData.getLines().stream()
					.map(AssSubtitleUnitData::getText)
					.toList(), language.getIsoCode());
			
			List<String> translatedLinesAss = translatedAss.getTranslations().stream()
			.map(DeepLTranslation::getText)
			.toList();
	
			if(translatedLinesAss.size() == assFileData.getLines().size()) {
				for(int i=0; i<translatedLinesAss.size(); i++) {
					assFileData.getLines().get(i).setText(translatedLinesAss.get(i));
				}
				subtitleFileDataJson = JsonSerializationUtil.assSubtitleFileDataToJson(assFileData);
			}else {
				String message = "Translation error!";
				log.error(message);
				throw new RuntimeException(message);
			}
			break;
		}
		
		
		redisService.addUserSubtitleCurrentVersion(subtitleFileDataJson, user.getUserId());
		
		UserSubtitleCorrectionCurrentVersionMetadata metadata = new UserSubtitleCorrectionCurrentVersionMetadata();
		metadata.setBomData(bomData);
		metadata.setFilename(originalFileName);
		metadata.setFormat(sourceFormat);
		
		redisService.addUsersLastUpdatedSubtitleFileMetadata(metadata, user.getUserId());
		
		response.setDetectedEncoding(detectedEncoding.displayName());
		response.setDetectedSourceFormat(sourceFormat);
		response.setFilename(originalFileName);
		
		//Refresh usage metrics after using DeepL Api
		usageMetricsExposer.reportUsage();
		
		return response;
		
	}
	
}
