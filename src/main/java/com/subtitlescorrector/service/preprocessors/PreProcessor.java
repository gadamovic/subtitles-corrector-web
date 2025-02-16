package com.subtitlescorrector.service.preprocessors;

import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.SubtitleFileData;

public interface PreProcessor {

	SubtitleFileData process(SubtitleFileData data, AdditionalData params);
	
}
