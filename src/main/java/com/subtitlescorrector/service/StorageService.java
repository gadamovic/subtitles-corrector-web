package com.subtitlescorrector.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	File store(MultipartFile file);

}
