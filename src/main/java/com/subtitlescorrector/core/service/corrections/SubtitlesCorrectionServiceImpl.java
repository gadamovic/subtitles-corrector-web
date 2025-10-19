package com.subtitlescorrector.core.service.corrections;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.domain.UserSubtitleCorrectionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.ass.AssSubtitleFileData;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.domain.vtt.VttSubtitleFileData;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.service.corrections.ass.AssSubtitlesFileProcessor;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitlesFileProcessor;
import com.subtitlescorrector.core.service.corrections.vtt.VttSubtitlesFileProcessor;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.JsonSerializationUtil;
import com.subtitlescorrector.core.util.Util;

@Service
public class SubtitlesCorrectionServiceImpl implements SubtitlesCorrectionService {

	@Autowired
	SubtitlesProcessorFactory processorFactory;
	
	@Autowired
	ExternalCacheServicePort redisService;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	UserData user;
	
	@Autowired
	WebSocketMessageSender webSocketMessageSender;
	
	public SubtitleCorrectionFileDataWebDto applyCorrectionOperations(AdditionalData clientParameters, File uploadedFile) {

		List<String> lines = FileUtil.loadTextFile(uploadedFile);
		SubtitleFormat format = Util.detectSubtitleFormat(lines);
		
		BomData bomData = new BomData();
		
		handleBOM(clientParameters, bomData, lines);
		
		String jsonData = null;
		
		
		SubtitleCorrectionFileDataWebDto response = new SubtitleCorrectionFileDataWebDto();
		
		Object subtitlesProcessor = processorFactory.getProcessor(format);
		
		switch (format) {
		case SRT:
			SrtSubtitleFileData srtData = ((SrtSubtitlesFileProcessor) subtitlesProcessor).process(uploadedFile, lines, clientParameters, bomData);
			response.setFilename(srtData.getFilename());
			response.setHttpResponseMessage(srtData.getHttpResponseMessage());
			response.setLines(srtData.linesToResponseLines());
			jsonData = JsonSerializationUtil.srtSubtitleFileDataToJson(srtData);
			break;
		case VTT:
			VttSubtitleFileData vttData = ((VttSubtitlesFileProcessor) subtitlesProcessor).process(uploadedFile, lines, clientParameters, bomData);
			response.setFilename(vttData.getFilename());
			response.setHttpResponseMessage(vttData.getHttpResponseMessage());
			response.setLines(vttData.linesToResponseLines());
			jsonData = JsonSerializationUtil.vttSubtitleFileDataToJson(vttData);
			break;
		case ASS:
			AssSubtitleFileData assData = ((AssSubtitlesFileProcessor) subtitlesProcessor).process(uploadedFile, lines, clientParameters, bomData);
			response.setFilename(assData.getFilename());
			response.setHttpResponseMessage(assData.getHttpResponseMessage());
			response.setLines(assData.linesToResponseLines());
			jsonData = JsonSerializationUtil.assSubtitleFileDataToJson(assData);
			break;
		}
		
		UserSubtitleCorrectionCurrentVersionMetadata metadata = new UserSubtitleCorrectionCurrentVersionMetadata();
		metadata.setBomData(bomData);
		metadata.setFormat(format);
		metadata.setFilename(clientParameters.getOriginalFilename());
		
		//save uploaded and server-corrected version as the first version
		redisService.addUserSubtitleCurrentVersion(jsonData, user.getUserId());
		redisService.addUsersLastUpdatedSubtitleFileMetadata(metadata, user.getUserId());
		
		return response;
		
	}
	
	/**
	 * BOM is actually added and removed in converters. Here we just set BOM related parameters to SubtitleFileData object
	 * and send message about correction to the client if needed
	 * @param params
	 * @param data
	 * @param lines
	 */
	private void handleBOM(AdditionalData params, BomData data, List<String> lines) {

		if (lines.get(0).startsWith("\uFEFF")) {
			data.setHasBom(true);
			if(params.getKeepBOM()) {
				data.setKeepBom(true);
			}else {
				data.setKeepBom(false);
				sendBOMRemovedMessage();
			}
		} else {
			data.setHasBom(false);
			data.setKeepBom(false);
		}
	}
	
	private void sendBOMRemovedMessage() {
		SubtitleCorrectionEvent bomRemoved = new SubtitleCorrectionEvent();
		bomRemoved.setCorrection("Removed BOM");
		webSocketMessageSender.sendMessage(bomRemoved);
	}
	
}
