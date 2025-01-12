package com.subtitlescorrector.controller.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.service.SubtitleLinesToSubtitleUnitDataConverter;
import com.subtitlescorrector.service.redis.RedisService;
import com.subtitlescorrector.util.FileUtil;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class SaveSubtitleController {

	Logger log = LoggerFactory.getLogger(SaveSubtitleController.class);

	@Autowired
	RedisService redisService;

	@Autowired
	SubtitleLinesToSubtitleUnitDataConverter converter;

	@RequestMapping(path = "/save")
	public void save(@RequestBody SubtitleFileData subtitleData, @RequestParam("userId") String userId) {

		redisService.addUserSubtitleCurrentVersion(subtitleData, userId);

	}

	@RequestMapping(path = "/downloadFile")
	public void download(@RequestParam("userId") String userId, HttpServletResponse response) {

		//TODO: Sredi ime fajla koji se skida
		//TODO: Download dugme da bude ime fajla i klik za download
		
		SubtitleFileData data = redisService.getUserSubtitleCurrentVersion(userId);

		File downloadableFile = new File(data.getFilename());
		FileUtil.writeLinesToFile(downloadableFile, converter.convertToListOfStrings(data.getLines()), StandardCharsets.UTF_8);

        // Set response headers
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + data.getFilename() + "\"");
        response.setContentLengthLong(downloadableFile.length());
		
        try (FileInputStream fis = new FileInputStream(downloadableFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } finally {
            // Delete the file after response is sent
            if (!downloadableFile.delete()) {
                log.error("Failed to delete the file: " + downloadableFile.getAbsolutePath());
            }
        }
	}

}
