package com.subtitlescorrector.service.subtitles;

import java.io.File;

import com.subtitlescorrector.domain.SubtitleFileData;

public interface SubtitlesFileProcessor {

	SubtitleFileData process(File storedFile, String webSocketSessionId);

}
