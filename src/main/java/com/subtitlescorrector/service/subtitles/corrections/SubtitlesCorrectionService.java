package com.subtitlescorrector.service.subtitles.corrections;

import java.io.File;

import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.SubtitleFileData;

import jakarta.servlet.http.HttpServletRequest;

public interface SubtitlesCorrectionService {

	public SubtitleFileData applyCorrectionOperations(AdditionalData clientParameters, File uploadedFile, HttpServletRequest request, String originalFileName);
	
}