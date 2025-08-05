package com.subtitlescorrector.adapters.out;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.port.StorageSystemPort;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.Util;

/**
 * Implementation of storage service used for storing uploaded files on disk
 * @author Gavrilo Adamovic
 *
 */
@Service
public class FileSystemStorageAdapter implements StorageSystemPort{

	Logger log = LoggerFactory.getLogger(FileSystemStorageAdapter.class);
	
	@Autowired
	ApplicationProperties properties;
	
	@Override
	public File store(MultipartFile file) {
		
		File storageFolderOnDisk = new File(properties.getUploadFolderLocation());
		if(!storageFolderOnDisk.exists()) {
			storageFolderOnDisk.mkdirs();
		}
		
		File fileOnDisk = new File(storageFolderOnDisk, Util.getCurrentTimestampAsString() + "_"+ file.getOriginalFilename());
		
		try (InputStream fis = file.getInputStream(); FileOutputStream fos = new FileOutputStream(fileOnDisk)) {
	        FileUtil.writeToFile(fis, fos);
	        log.info("File: {} successfully uploaded!", file.getOriginalFilename());
	    } catch (IOException e) {
	        log.error("Error handling file: [{}] upload", file.getOriginalFilename(), e);
	    }
		
		return fileOnDisk;
		
	}

}

