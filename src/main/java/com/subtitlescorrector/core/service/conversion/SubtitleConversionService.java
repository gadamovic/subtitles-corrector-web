package com.subtitlescorrector.core.service.conversion;

import java.io.File;

import com.subtitlescorrector.core.domain.ConversionParameters;
import com.subtitlescorrector.core.domain.SubtitleConversionFileData;
import com.subtitlescorrector.core.domain.SubtitleFileData;

import jakarta.servlet.http.HttpServletRequest;

public interface SubtitleConversionService {

	SubtitleConversionFileData applyConversionOperations(ConversionParameters conversionParameters, File storedFile,
			HttpServletRequest request, String originalFilename);

}