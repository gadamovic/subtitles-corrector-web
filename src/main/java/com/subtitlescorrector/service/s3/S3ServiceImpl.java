package com.subtitlescorrector.service.s3;

import java.io.File;
import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.domain.S3BucketNames;
import com.subtitlescorrector.domain.SubtitlesFileProcessorResponse;
import com.subtitlescorrector.domain.SubtitlesProcessingStatus;
import com.subtitlescorrector.util.Util;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class S3ServiceImpl implements S3Service {

	Logger log = LoggerFactory.getLogger(S3ServiceImpl.class);
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	Util util;
	
	@Override
	public PutObjectResponse uploadFileToS3(String key, String bucket, File file){
			
		S3Client s3Client = S3Client.builder()
			    .region(Region.EU_NORTH_1)
			    .build();
			    
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();
		
		log.info("Uploaded to s3 with key: {}", key);

		return s3Client.putObject(putObjectRequest, file.toPath());
		
	}
	
	public SubtitlesFileProcessorResponse uploadAndGetDownloadUrl(File correctedFile) {

		SubtitlesFileProcessorResponse response = new SubtitlesFileProcessorResponse();
		
		String s3KeyUUIDPrefix = UUID.randomUUID().toString();
		String s3Key = s3KeyUUIDPrefix + correctedFile.getName();
		
		
		if(properties.isProdEnvironment()) {
			log.info("Attempting upload to s3...");
			uploadFileToS3(s3Key, S3BucketNames.SUBTITLES_UPLOADED_FILES.getBucketName(), correctedFile);
		}
		
		if(!properties.isProdEnvironment()) {
			response.setDownloadUrl("https://subtitles-corrector.com");
			response.setStatus(SubtitlesProcessingStatus.SUCCESS_DEVELOPMENT);
		}
		
		
		S3Presigner presigner = S3Presigner.create();
		GetObjectPresignRequest getObjectRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(r -> r.bucket(S3BucketNames.SUBTITLES_UPLOADED_FILES.getBucketName())
                					    .key(s3Key)
                					    .responseContentDisposition("attachment; filename=\"" + util.makeFilenameForDownloadFromS3Key(s3Key) +"\""))
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(getObjectRequest);
        
        response.setDownloadUrl(presignedRequest.url().toString());
		response.setStatus(SubtitlesProcessingStatus.SUCCESS);
        
		return response;

	}
	
}
