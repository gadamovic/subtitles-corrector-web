package com.subtitlescorrector.service.s3;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.service.S3ServiceMonitor;
import com.subtitlescorrector.service.redis.RedisService;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class S3ServiceImpl implements S3Service {

	Logger log = LoggerFactory.getLogger(S3ServiceImpl.class);
	
	@Autowired
	S3ServiceMonitor monitor;
	
	@Autowired
	RedisService redis;
	
	@Override
	public PutObjectResponse uploadFileToS3(String key, String bucket, File file){
		
		if(monitor.uploadAllowed()) {
			
			S3Client s3Client = S3Client.builder()
				    .region(Region.EU_NORTH_1)
				    .build();
				    
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
					.bucket(bucket)
					.key(key)
					.build();
			
			redis.updateLastS3UploadTimestamp();

			return s3Client.putObject(putObjectRequest, file.toPath());
		}else {
			log.warn("Monitor didn't allow s3 upload!");
			return null;
		}
		
	}
	
}
