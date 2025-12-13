package com.subtitlescorrector.core.service;

import jakarta.servlet.http.HttpServletResponse;

public interface DownloadSubtitlesFileService {

	void downloadSubtitlesFileForUser(String userId, HttpServletResponse response);
	void downloadSubtitlesConvertedFileForUser(String userId, String targetFormat, HttpServletResponse response);
	void downloadSubtitlesTranslatedFileForUser(String userId, HttpServletResponse response);

}