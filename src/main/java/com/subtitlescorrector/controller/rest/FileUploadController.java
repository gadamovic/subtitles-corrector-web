package com.subtitlescorrector.controller.rest;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.subtitlescorrector.service.StorageService;
import com.subtitlescorrector.service.subtitles.SubtitlesFileProcessor;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class FileUploadController {

	@Autowired
	StorageService fileSystemStorageService;
	
	@Autowired
	SubtitlesFileProcessor processor;
	
	@RequestMapping(path = "/upload", method = RequestMethod.POST)
	public void uploadFile(@RequestParam("file") MultipartFile file) {
		
		File storedFile = fileSystemStorageService.store(file);
		processor.process(storedFile);
		
	}
	
}

