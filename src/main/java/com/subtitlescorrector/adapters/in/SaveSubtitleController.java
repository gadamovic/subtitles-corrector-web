package com.subtitlescorrector.adapters.in;

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

import com.subtitlescorrector.adapters.out.S3BucketNames;
import com.subtitlescorrector.core.domain.SubtitleConversionFileData;
import com.subtitlescorrector.core.domain.SubtitleFileData;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.SubtitleUnitData;
import com.subtitlescorrector.core.domain.UserSubtitleData;
import com.subtitlescorrector.core.port.RedisServicePort;
import com.subtitlescorrector.core.port.SubtitlesCloudStoragePort;
import com.subtitlescorrector.core.service.SubtitleFileProviderForUser;
import com.subtitlescorrector.core.service.converters.SubtitleLinesToSubtitleUnitDataConverter;
import com.subtitlescorrector.core.service.converters.SubtitlesConverterFactory;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.Util;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class SaveSubtitleController {

	Logger log = LoggerFactory.getLogger(SaveSubtitleController.class);

	@Autowired
	RedisServicePort redisService;

	@Autowired
	SubtitlesConverterFactory converterFactory;
	
	@Autowired
	SubtitlesCloudStoragePort s3Service;
	
	@Autowired
	SubtitleFileProviderForUser subtitleFileProvider;

	@RequestMapping(path = "/save")
	public void save(@RequestBody SubtitleFileData subtitleData, @RequestParam("userId") String userId) {
		Util.convertBrTagsToNewLineCharacters(subtitleData);
		redisService.addUserSubtitleCurrentVersion(subtitleData, userId);
	}

	@RequestMapping(path = "/downloadFile")
	public void download(@RequestParam("userId") String userId, HttpServletResponse response) {
		
		UserSubtitleData userData = subtitleFileProvider.provideFileForUser(userId);

		String webSocketSessionIdAsFilename = redisService.getWebSocketSessionIdForUser(userId);
		s3Service.storeIfProd("v3_" + webSocketSessionIdAsFilename + "_" + userData.getSubtitleFileData().getFilename(), userData.getFile());

        // Set response headers
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + userData.getSubtitleFileData().getFilename() + "\"");
        response.setContentLengthLong(userData.getFile().length());
		
        try (FileInputStream fis = new FileInputStream(userData.getFile())) {
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
            if (!userData.getFile().delete()) {
                log.error("Failed to delete the file: " + userData.getFile().getAbsolutePath());
            }
        }
	}
	
	@RequestMapping(path = "/downloadConvertedFile")
	public void downloadConvertedFile(@RequestParam("userId") String userId, @RequestParam("targetFormat") String targetFormat, HttpServletResponse response) {
		
		
		SubtitleConversionFileData data = redisService.getUserSubtitleConversionData(userId);

		MDC.put("subtitle_name", data.getFilename());
		MDC.put("source_format", data.getSourceFormat().toString());
		MDC.put("target_format", targetFormat);
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

}
