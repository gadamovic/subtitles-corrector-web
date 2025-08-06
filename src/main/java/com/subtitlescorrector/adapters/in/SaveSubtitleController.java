package com.subtitlescorrector.adapters.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.subtitlescorrector.core.domain.SubtitleFileData;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.service.DownloadSubtitlesFileService;
import com.subtitlescorrector.core.util.Util;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class SaveSubtitleController {

	Logger log = LoggerFactory.getLogger(SaveSubtitleController.class);

	@Autowired
	ExternalCacheServicePort redisService;
	
	@Autowired
	DownloadSubtitlesFileService downloadService;

	@RequestMapping(path = "/save")
	public void save(@RequestBody SubtitleFileData subtitleData, @RequestParam("userId") String userId) {
		Util.convertBrTagsToNewLineCharacters(subtitleData);
		redisService.addUserSubtitleCurrentVersion(subtitleData, userId);
	}

	@RequestMapping(path = "/downloadFile")
	public void download(@RequestParam("userId") String userId, HttpServletResponse response) {
		downloadService.downloadSubtitlesFileForUser(userId, response);
	}
	
	@RequestMapping(path = "/downloadConvertedFile")
	public void downloadConvertedFile(@RequestParam("userId") String userId, @RequestParam("targetFormat") String targetFormat, HttpServletResponse response) {
		downloadService.downloadSubtitlesConvertedFileForUser(userId, targetFormat, response);	
	}

}
