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
import com.subtitlescorrector.core.domain.SubtitleConversionFileDataResponse;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.domain.UserSubtitleConversionCurrentVersionMetadata;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.port.SubtitlesCloudStoragePort;
import com.subtitlescorrector.core.service.DeepLUsageMetricsExposerService;
import com.subtitlescorrector.core.service.converters.SubtitlesConverterFactory;
import com.subtitlescorrector.core.service.corrections.ass.AssSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.vtt.VttSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.JsonSerializationUtil;
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
	
	@Autowired
	SrtSubtitleLinesToSubtitleUnitDataParser srtParser;
	
	@Autowired
	VttSubtitleLinesToSubtitleUnitDataParser vttParser;
	
	@Autowired
	AssSubtitleLinesToSubtitleUnitDataParser assParser;
	
	
	Logger log = LoggerFactory.getLogger(SubtitleConversionServiceImpl.class);
	
	@Override
	public SubtitleConversionFileDataResponse applyConversionOperations(ConversionParameters conversionParameters, File uploadedFile) {
		
		List<String> lines = FileUtil.loadTextFile(uploadedFile);
		
		BomData bomData = new BomData();
		
		handleBOM(bomData, lines);
		
		log.info("Converting a file");
		
		MDC.put("subtitle_name", conversionParameters.getOriginalFilename());
		log.info("Uploading file for conversion...");
		MDC.remove("subtitle_name");
		
		String s3Key = user.getUserId() + "_" + uploadedFile.getName();
		s3Service.storeIfProd("pre-conversion_" + s3Key, uploadedFile);
		
		Charset detectedEncoding = FileUtil.detectEncodingOfFile(uploadedFile);
		
		SubtitleFormat sourceFormat = Util.detectSubtitleFormat(lines);
		
		SubtitleConversionFileDataResponse response = new SubtitleConversionFileDataResponse();
		
		String subtitleFileDataJson = null;
		
		switch (sourceFormat) {
		case SRT:
			SrtSubtitleConversionFileData srtFileData = srtParser.convertToSubtitleUnits(lines).toSubtitleFileConversionData();
			srtFileData.setFilename(conversionParameters.getOriginalFilename());
			srtFileData.setSourceFormat(sourceFormat);
			srtFileData.setDetectedEncoding(detectedEncoding.displayName());
			srtFileData.setBomData(bomData);

			response.setNumberOfLines(srtFileData.getLines().size());

			subtitleFileDataJson = JsonSerializationUtil.srtSubtitleConversionFileDataToJson(srtFileData);
			break;
		case VTT:
			VttSubtitleConversionFileData vttFileData = vttParser.convertToSubtitleUnits(lines).toSubtitleFileConversionData();
			vttFileData.setFilename(conversionParameters.getOriginalFilename());
			vttFileData.setSourceFormat(sourceFormat);
			vttFileData.setDetectedEncoding(detectedEncoding.displayName());
			vttFileData.setBomData(bomData);
			
			response.setNumberOfLines(vttFileData.getLines().size());
			
			subtitleFileDataJson = JsonSerializationUtil.vttSubtitleConversionFileDataToJson(vttFileData);
			break;
		case ASS:
			AssSubtitleConversionFileData assFileData = assParser.convertToSubtitleUnits(lines).toSubtitleFileConversionData();
			assFileData.setFilename(conversionParameters.getOriginalFilename());
			assFileData.setSourceFormat(sourceFormat);
			assFileData.setDetectedEncoding(detectedEncoding.displayName());
			assFileData.setBomData(bomData);
			
			response.setNumberOfLines(assFileData.getLines().size());
			
			subtitleFileDataJson = JsonSerializationUtil.assSubtitleConversionFileDataToJson(assFileData);
			break;
		}
		
		UserSubtitleConversionCurrentVersionMetadata metadata = new UserSubtitleConversionCurrentVersionMetadata();
		metadata.setBomData(bomData);
		metadata.setFilename(conversionParameters.getOriginalFilename());
		metadata.setSourceFormat(sourceFormat);

		redisService.addUserSubtitleConversionData(subtitleFileDataJson, user.getUserId());
		redisService.addUsersLastUpdatedSubtitleConversionFileMetadata(metadata, user.getUserId());
		
		response.setDetectedSourceFormat(sourceFormat);
		response.setDetectedEncoding(detectedEncoding.displayName());
		response.setFilename(conversionParameters.getOriginalFilename());
		
		return response;
	}
	
	/**
	 * BOM is actually added and removed in converters. Here we just set BOM related parameters to SubtitleFileData object
	 * and send message about correction to the client if needed
	 * @param data
	 * @param lines
	 */
	private void handleBOM(BomData data, List<String> lines) {
		//TODO: Move to util
		if (lines.get(0).startsWith("\uFEFF")) {
			data.setHasBom(true);
			data.setKeepBom(false);
		}else {
			data.setHasBom(false);
		}
	}

}
