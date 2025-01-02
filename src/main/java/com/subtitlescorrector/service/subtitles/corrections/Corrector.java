package com.subtitlescorrector.service.subtitles.corrections;

import java.util.List;

public interface Corrector {

	/**
	 * 
	 * @param lines of the subtitle file to be corrected
	 * @param webSocketSessionId that is used to send log data back to the client (browser)
	 * @return corrected lines
	 */
	public List<String> correct(List<String> lines, String webSocketSessionId);
	
}
