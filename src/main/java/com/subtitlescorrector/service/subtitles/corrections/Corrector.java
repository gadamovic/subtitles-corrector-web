package com.subtitlescorrector.service.subtitles.corrections;

import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleUnitData;

public interface Corrector {

	/**
	 * 
	 * @param lines of the subtitle file to be corrected
	 * @param webSocketSessionId that is used to send log data back to the client (browser)
	 * @return corrected lines
	 */
	public SubtitleFileData correct(SubtitleFileData data, AdditionalData additionalData);

	public void correct(SubtitleUnitData subUnit, AdditionalData params);
	
}
