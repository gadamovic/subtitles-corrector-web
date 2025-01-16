package com.subtitlescorrector.service.subtitles.corrections;

import java.util.List;

import com.subtitlescorrector.domain.SubtitleFileData;

public interface Corrector {

	/**
	 * 
	 * @param lines of the subtitle file to be corrected
	 * @param webSocketSessionId that is used to send log data back to the client (browser)
	 * @return corrected lines
	 */
	public SubtitleFileData correct(SubtitleFileData data, String webSocketSessionId);
	
}
