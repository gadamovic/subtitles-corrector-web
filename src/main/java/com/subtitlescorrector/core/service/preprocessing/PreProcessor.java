package com.subtitlescorrector.core.service.preprocessing;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SubtitleFileData;

public interface PreProcessor {

	SubtitleFileData process(SubtitleFileData data, AdditionalData params);
	
}
