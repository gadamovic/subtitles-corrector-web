package com.subtitlescorrector.core.service.corrections;

import java.io.File;
import java.util.List;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.SubtitleFileData;

import jakarta.servlet.http.HttpServletRequest;

public interface SubtitlesCorrectionService {

	public SubtitleFileData applyCorrectionOperations(AdditionalData clientParameters, File uploadedFile, List<String> lines, BomData bomData);
	
}