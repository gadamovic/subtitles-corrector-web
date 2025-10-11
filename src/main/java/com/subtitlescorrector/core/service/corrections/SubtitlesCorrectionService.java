package com.subtitlescorrector.core.service.corrections;

import java.io.File;

import com.subtitlescorrector.core.domain.AdditionalData;

public interface SubtitlesCorrectionService {

	public SubtitleCorrectionFileDataWebDto applyCorrectionOperations(AdditionalData clientParameters, File uploadedFile);
	
}