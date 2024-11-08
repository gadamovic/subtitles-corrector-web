package com.subtitlescorrector.service.s3;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.subtitlescorrector.service.FileSystemStorageService;
import com.subtitlescorrector.service.S3ServiceMonitor;
import com.subtitlescorrector.service.redis.RedisService;

@Service
public class S3ServiceImpl implements S3Service {

	Logger log = LoggerFactory.getLogger(S3ServiceImpl.class);
	
	@Autowired
	S3ServiceMonitor monitor;
	
	@Autowired
	RedisService redis;
	
	@Override
	public PutObjectResult uploadFileToS3(String key, String bucket, File file){
		
		if(monitor.uploadAllowed()) {
			AmazonS3 s3Client = AmazonS3Client.builder()
				    .withRegion(Regions.EU_NORTH_1).build();
				    
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, file);
			
			redis.updateLastS3UploadTimestamp();
			return s3Client.putObject(putObjectRequest);
		}else {
			log.warn("Monitor didn't allow s3 upload!");
			return null;
		}
		
	}
	
}
