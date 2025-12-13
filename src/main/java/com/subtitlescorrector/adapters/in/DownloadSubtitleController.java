package com.subtitlescorrector.adapters.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.subtitlescorrector.core.domain.UserSubtitleCorrectionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.ass.AssSubtitleFileData;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.domain.vtt.VttSubtitleFileData;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.service.DownloadSubtitlesFileService;
import com.subtitlescorrector.core.service.corrections.SubtitleCorrectionFileDataWebDto;
import com.subtitlescorrector.core.util.JsonSerializationUtil;
import com.subtitlescorrector.core.util.Util;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class DownloadSubtitleController {

	Logger log = LoggerFactory.getLogger(DownloadSubtitleController.class);
	
	@Autowired
	DownloadSubtitlesFileService downloadService;

	@RequestMapping(path = "/downloadFile")
	public void download(@RequestParam("userId") String userId, HttpServletResponse response) {
		downloadService.downloadSubtitlesFileForUser(userId, response);
	}
	
	@RequestMapping(path = "/downloadConvertedFile")
	public void downloadConvertedFile(@RequestParam("userId") String userId, @RequestParam("targetFormat") String targetFormat, HttpServletResponse response) {
		downloadService.downloadSubtitlesConvertedFileForUser(userId, targetFormat, response);	
	}
	
	@RequestMapping(path = "/downloadTranslatedFile")
	public void downloadTranslatedFile(@RequestParam("userId") String userId, HttpServletResponse response) {
		downloadService.downloadSubtitlesTranslatedFileForUser(userId, response);	
	}

}
