package com.subtitlescorrector.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.subtitlescorrector.service.StorageService;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class FileUploadController {

	@Autowired
	StorageService fileSystemStorageService;
	
	@RequestMapping(path = "/upload", method = RequestMethod.POST)
	public void uploadFile(@RequestParam("file") MultipartFile file) {
		
		fileSystemStorageService.store(file);
		
	}
	
}

