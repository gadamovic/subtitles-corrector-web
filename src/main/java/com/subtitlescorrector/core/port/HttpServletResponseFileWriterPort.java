package com.subtitlescorrector.core.port;

import com.subtitlescorrector.core.domain.UserSubtitleConversionData;
import com.subtitlescorrector.core.domain.UserSubtitleData;

import jakarta.servlet.http.HttpServletResponse;

public interface HttpServletResponseFileWriterPort {

	void writeFileToResponse(UserSubtitleData data, HttpServletResponse response);
	void writeFileToResponse(UserSubtitleConversionData data, HttpServletResponse response);
	
}
