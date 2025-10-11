package com.subtitlescorrector.core.service.corrections;

import java.io.File;
import java.util.List;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleFileData;

public interface SubtitlesFileProcessor {

	SrtSubtitleFileData process(File storedFile, List<String> lines, AdditionalData processOptions, BomData bomData);

}
