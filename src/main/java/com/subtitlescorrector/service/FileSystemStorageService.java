package com.subtitlescorrector.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.subtitlescorrector.util.Util;

/**
 * Implementation of storage service used for storing uploaded files on disk
 * @author Gavrilo Adamovic
 *
 */
@Service
public class FileSystemStorageService implements StorageService{

	Logger log = LoggerFactory.getLogger(FileSystemStorageService.class);
	
	@Override
	public void store(MultipartFile file) {
		
		File storageFolderOnDisk = new File("D:/upload/");
		if(!storageFolderOnDisk.exists()) {
			storageFolderOnDisk.mkdirs();
		}
		
		File fileOnDisk = new File(storageFolderOnDisk, Util.getCurrentTimestampAsString() + "_"+ file.getOriginalFilename());
		
		try (InputStream fis = file.getInputStream(); FileOutputStream fos = new FileOutputStream(fileOnDisk)) {
	        writeToFile(fis, fos);
	        log.info("File: {} successfully uploaded!", file.getOriginalFilename());
	    } catch (IOException e) {
	        log.error("Error handling file: [{}] upload", file.getOriginalFilename(), e);
	    }
		
	}

	private void writeToFile(InputStream fis, FileOutputStream fos) {
		if(fis != null && fos != null) {
			try {
				int readByte;
				while((readByte = fis.read()) != -1) {
					fos.write(readByte);
				}
			} catch (IOException e) {
				log.error("Error while reading bytes from a multipart file!", e);
			}
		}
	}

}

