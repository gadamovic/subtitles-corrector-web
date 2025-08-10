package com.subtitlescorrector.core.service.conversion;

import java.io.File;
import java.util.List;

import com.subtitlescorrector.core.domain.ConversionParameters;
import com.subtitlescorrector.core.domain.SubtitleConversionFileData;
import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.SubtitleFileData;

import jakarta.servlet.http.HttpServletRequest;

public interface SubtitleConversionService {

	SubtitleConversionFileData applyConversionOperations(ConversionParameters conversionParameters, File storedFile,
			List<String> lines, BomData bomData);

}