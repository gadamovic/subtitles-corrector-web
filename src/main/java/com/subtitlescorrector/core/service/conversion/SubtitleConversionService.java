package com.subtitlescorrector.core.service.conversion;

import java.io.File;
import java.util.List;

import com.subtitlescorrector.core.domain.ConversionParameters;
import com.subtitlescorrector.core.domain.SubtitleConversionFileDataResponse;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.BomData;

import jakarta.servlet.http.HttpServletRequest;

public interface SubtitleConversionService {

	SubtitleConversionFileDataResponse applyConversionOperations(ConversionParameters conversionParameters, File storedFile);

}