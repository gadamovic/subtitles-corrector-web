package com.subtitlescorrector.core.service.preprocessing;

import java.util.List;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleFileData;

public interface PreProcessor {

	List<String> process(List<String> data);
	
}
