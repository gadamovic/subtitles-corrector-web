package com.subtitlescorrector.core.service.corrections;

import java.io.File;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SubtitleFileData;

public interface SubtitlesFileProcessor {

	SubtitleFileData process(File storedFile, AdditionalData processOptions);

}
