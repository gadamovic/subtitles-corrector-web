package com.subtitlescorrector.controller.rest;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.subtitlescorrector.domain.S3BucketNames;
import com.subtitlescorrector.service.StorageService;
import com.subtitlescorrector.service.s3.S3Service;
import com.subtitlescorrector.service.subtitles.SubtitlesFileProcessor;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class FileUploadController {

	Logger log = LoggerFactory.getLogger(FileUploadController.class);

	
	@Autowired
	StorageService fileSystemStorageService;
	
	@Autowired
	SubtitlesFileProcessor processor;
	
	@Autowired
	S3Service s3Service;
	
	@RequestMapping(path = "/upload", method = RequestMethod.POST)
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		
		File storedFile = fileSystemStorageService.store(file);
		File processedFile = processor.process(storedFile);
		
		//s3Service.uploadFileToS3(storedFile.getName(), S3BucketNames.SUBTITLES_UPLOADED_FILES.getBucketName(), processedFile);
		
		return ResponseEntity.ok("https://subtitles-uploaded-files.s3.eu-north-1.amazonaws.com/" + storedFile.getName());
		
	}
}

