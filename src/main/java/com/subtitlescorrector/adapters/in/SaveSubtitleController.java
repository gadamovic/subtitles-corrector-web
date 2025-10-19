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
public class SaveSubtitleController {

	Logger log = LoggerFactory.getLogger(SaveSubtitleController.class);

	@Autowired
	ExternalCacheServicePort redisService;
	
	@Autowired
	DownloadSubtitlesFileService downloadService;

	@RequestMapping(path = "/save")
	public void save(@RequestBody SubtitleCorrectionFileDataWebDto subtitleData, @RequestParam("userId") String userId) {
		Util.convertBrTagsToNewLineCharacters(subtitleData);
		
		UserSubtitleCorrectionCurrentVersionMetadata metadata = redisService.getUsersLastUpdatedSubtitleFileMetadata(userId);
		String currentJson = redisService.getUserSubtitleCurrentVersionJson(userId);
		String updatedJson = null;
		
		switch(metadata.getFormat()) {
		case SRT:
			SrtSubtitleFileData srtData = JsonSerializationUtil.jsonToSrtSubtitleFileData(currentJson);
			srtData.merge(subtitleData);
			updatedJson = JsonSerializationUtil.srtSubtitleFileDataToJson(srtData);
			break;
		case VTT:
			VttSubtitleFileData vttData = JsonSerializationUtil.jsonToVttSubtitleFileData(currentJson);
			vttData.merge(subtitleData);
			updatedJson = JsonSerializationUtil.vttSubtitleFileDataToJson(vttData);
			break;
		case ASS:
			AssSubtitleFileData assData = JsonSerializationUtil.jsonToAssSubtitleFileData(currentJson);
			assData.merge(subtitleData);
			updatedJson = JsonSerializationUtil.assSubtitleFileDataToJson(assData);
			break;
		}
		
		redisService.addUserSubtitleCurrentVersion(updatedJson, userId);
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
