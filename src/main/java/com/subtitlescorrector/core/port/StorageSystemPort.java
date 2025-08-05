package com.subtitlescorrector.core.port;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public interface StorageSystemPort {

	File store(MultipartFile file);

}
