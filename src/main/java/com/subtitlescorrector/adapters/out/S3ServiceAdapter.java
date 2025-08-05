package com.subtitlescorrector.adapters.out;

import java.io.File;
import java.time.Duration;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.SubtitlesFileProcessorResponse;
import com.subtitlescorrector.core.domain.SubtitlesProcessingStatus;
import com.subtitlescorrector.core.port.SubtitlesCloudStoragePort;
import com.subtitlescorrector.core.util.Util;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class S3ServiceAdapter implements SubtitlesCloudStoragePort {

	Logger log = LoggerFactory.getLogger(S3ServiceAdapter.class);
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	Util util;
	
	@Override
	public void store(String key, File file){
			
		if(StringUtils.isBlank(key)) {
			String s3KeyUUIDPrefix = UUID.randomUUID().toString();
			key = s3KeyUUIDPrefix + file.getName();
		}
		
		S3Client s3Client = S3Client.builder()
			    .region(Region.EU_NORTH_1)
			    .build();
			    
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(S3BucketNames.SUBTITLES_UPLOADED_FILES.getBucketName())
				.key(key)
				.tagging("ttl=7days")
				.build();
		
		log.info("Uploaded to s3 with key: {}", key);

		s3Client.putObject(putObjectRequest, file.toPath());
		
	}
	
	public void storeIfProd(String key, File file) {
		
		if(properties.isProdEnvironment()) {
			store(key, file);
		}
		
	}
	
}
