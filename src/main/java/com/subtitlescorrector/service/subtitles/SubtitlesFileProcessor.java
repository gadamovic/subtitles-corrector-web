package com.subtitlescorrector.service.subtitles;

import java.io.File;

import com.subtitlescorrector.domain.SubtitlesFileProcessorResponse;

public interface SubtitlesFileProcessor {

	SubtitlesFileProcessorResponse process(File storedFile, String webSocketSessionId);

}
