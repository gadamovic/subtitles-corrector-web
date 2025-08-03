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
import org.slf4j.MDC;
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

import com.subtitlescorrector.domain.S3BucketNames;
import com.subtitlescorrector.domain.SubtitleConversionFileData;
import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleFormat;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.service.redis.RedisService;
import com.subtitlescorrector.service.s3.S3Service;
import com.subtitlescorrector.service.subtitles.SubtitleLinesToSubtitleUnitDataConverter;
import com.subtitlescorrector.service.subtitles.SubtitlesConverterFactory;
import com.subtitlescorrector.util.FileUtil;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class SaveSubtitleController {

	Logger log = LoggerFactory.getLogger(SaveSubtitleController.class);

	@Autowired
	RedisService redisService;

	@Autowired
	SubtitlesConverterFactory converterFactory;
	
	@Autowired
	S3Service s3Service;

	@RequestMapping(path = "/save")
	public void save(@RequestBody SubtitleFileData subtitleData, @RequestParam("userId") String userId) {
		
		//for html line breaks are represented as <br> tags, so put them back to \n
		for(SubtitleUnitData data : subtitleData.getLines()) {
			
			data.setText(data.getText().replace("<br>", "\n"));
			data.setTextBeforeCorrection(data.getTextBeforeCorrection().replace("<br>", "\n"));
			
		}
		
		redisService.addUserSubtitleCurrentVersion(subtitleData, userId);

	}

	@RequestMapping(path = "/downloadFile")
	public void download(@RequestParam("userId") String userId, HttpServletResponse response) {
		
		
		SubtitleFileData data = redisService.getUserSubtitleCurrentVersion(userId);

		MDC.put("subtitle_name", data.getFilename());
		log.info("Downloading corrected file...");
		MDC.remove("subtitle_name");

		List<String> lines = converterFactory.getConverter(data.getFormat()).convertToListOfStrings(data.getLines());
		
		if(data.getHasBom() && data.getKeepBom()) {
			lines = addBom(lines);
		}
		
		File downloadableFile = new File(data.getFilename());
		FileUtil.writeLinesToFile(downloadableFile, lines, StandardCharsets.UTF_8);

		String webSocketSessionIdAsFilename = redisService.getWebSocketSessionIdForUser(userId);
		s3Service.uploadFileToS3IfProd("v3_" + webSocketSessionIdAsFilename + "_" + data.getFilename(), S3BucketNames.SUBTITLES_UPLOADED_FILES.getBucketName(), downloadableFile, "ttl=7days");

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
	
	@RequestMapping(path = "/downloadConvertedFile")
	public void downloadConvertedFile(@RequestParam("userId") String userId, @RequestParam("targetFormat") String targetFormat, HttpServletResponse response) {
		
		
		SubtitleConversionFileData data = redisService.getUserSubtitleConversionData(userId);

		MDC.put("subtitle_name", data.getFilename());
		MDC.put("source_format", data.getSourceFormat().toString());
		MDC.put("target_format", data.getTargetFormat().toString());
		log.info("Downloading converted file...");
		MDC.remove("filename");
		MDC.remove("source_format");
		MDC.remove("target_format");

		List<String> lines = converterFactory.getConverter(SubtitleFormat.valueOf(SubtitleFormat.class, targetFormat)).convertToListOfStrings(data.getLines());
		
		File downloadableFile = new File(data.getFilename());
		FileUtil.writeLinesToFile(downloadableFile, lines, StandardCharsets.UTF_8);

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

	private List<String> addBom(List<String> lines) {
		
		if(lines.size() > 0) {
			String firstLine = lines.get(0);
			if(!firstLine.startsWith("\uFEFF")) {
				firstLine = "\uFEFF" + firstLine;
				lines.set(0, firstLine);
			}
		}
		
		return lines;
		
	}

}
