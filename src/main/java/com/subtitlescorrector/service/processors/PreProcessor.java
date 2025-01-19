package com.subtitlescorrector.service.processors;

import com.subtitlescorrector.domain.SubtitleFileData;

public interface PreProcessor {

	SubtitleFileData process(SubtitleFileData data);
	
}
