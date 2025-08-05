package com.subtitlescorrector.core.service.corrections;

import java.io.File;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SubtitleFileData;

import jakarta.servlet.http.HttpServletRequest;

public interface SubtitlesCorrectionService {

	public SubtitleFileData applyCorrectionOperations(AdditionalData clientParameters, File uploadedFile, HttpServletRequest request, String originalFileName);
	
}