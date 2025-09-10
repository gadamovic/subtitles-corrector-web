package com.subtitlescorrector.core.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.BusinessOperation;
import com.subtitlescorrector.core.domain.SubtitleConversionFileData;
import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.SubtitleFileData;
import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.domain.exception.InvalidBusinessOperationException;
import com.subtitlescorrector.core.port.EmailServicePort;
import com.subtitlescorrector.core.service.conversion.SubtitleConversionService;
import com.subtitlescorrector.core.service.corrections.SubtitlesCorrectionService;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.Util;


import jakarta.servlet.http.HttpServletRequest;

@Service
public class UploadSubtitlesFileEntryPointImpl implements UploadFileEntryPoint {

	
	@Autowired
	EmailServicePort emailService;
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	SubtitlesCorrectionService correctionService;
	
	@Autowired
	SubtitleConversionService conversionService;
	
	@Autowired
	WebSocketMessageSender webSocketMessageSender;
	
	@Override
	public String handleFileUpload(AdditionalData params, File storedFile,
			HttpServletRequest request) throws InvalidBusinessOperationException {
		
		String clientIp = request.getRemoteAddr();
		
		emailService.sendEmailOnlyIfProduction("Ip: " + clientIp + "\nFilename: " + storedFile.getName(), properties.getAdminEmailAddress(),
				"Somebody is uploading a subtitle for: " + params.getBusinessOperation().toString());

		List<String> lines = FileUtil.loadTextFile(storedFile);
		
		BomData bomData = new BomData();
		
		handleBOM(params, bomData, lines);
		
		if (params.getBusinessOperation() != null) {
			switch (params.getBusinessOperation()) {
			case CORRECTION:
				SubtitleFileData correctionData = correctionService.applyCorrectionOperations(params, storedFile, lines, bomData);
				return Util.subtitleFileDataToJson(correctionData);
			case CONVERSION:
				SubtitleConversionFileData conversionData = conversionService.applyConversionOperations(params.getConversionParameters(), storedFile, lines, bomData);
				return Util.subtitleConversionFileDataResponseToJson(Util.subtitleConversionFileDataToResponseObject(conversionData));
			default: 
				throw new InvalidBusinessOperationException("Invalid business operation provided. Contact support for more information.");
			}
		}else {
			throw new InvalidBusinessOperationException("Invalid business operation provided. Contact support for more information.");
		}

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
				if(params.getBusinessOperation() == BusinessOperation.CORRECTION) {
					sendBOMRemovedMessage();
				}
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
