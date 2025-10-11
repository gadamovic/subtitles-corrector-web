package com.subtitlescorrector.core.service.corrections;

import java.util.List;

/**
 * Subset of main class used to exchange data with the client app
 *
 */
public class SubtitleCorrectionFileDataWebDto{

	String filename;
	
	List<SubtitleCorrectionFileLineDataWebDto> lines;
	
	String httpResponseMessage;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<SubtitleCorrectionFileLineDataWebDto> getLines() {
		return lines;
	}

	public void setLines(List<SubtitleCorrectionFileLineDataWebDto> lines) {
		this.lines = lines;
	}

	public String getHttpResponseMessage() {
		return httpResponseMessage;
	}

	public void setHttpResponseMessage(String httpResponseMessage) {
		this.httpResponseMessage = httpResponseMessage;
	}

	
	
}
