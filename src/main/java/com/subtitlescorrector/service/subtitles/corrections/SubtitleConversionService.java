package com.subtitlescorrector.service.subtitles.corrections;

import java.io.File;

import com.subtitlescorrector.domain.ConversionParameters;
import com.subtitlescorrector.domain.SubtitleConversionFileData;
import com.subtitlescorrector.domain.SubtitleFileData;

import jakarta.servlet.http.HttpServletRequest;

public interface SubtitleConversionService {

	SubtitleConversionFileData applyConversionOperations(ConversionParameters conversionParameters, File storedFile,
			HttpServletRequest request, String originalFilename);

}