package com.subtitlescorrector.core.service.corrections;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SubtitleFileData;

public interface AiCustomCorrector {

	SubtitleFileData correct(SubtitleFileData data, AdditionalData params);

}