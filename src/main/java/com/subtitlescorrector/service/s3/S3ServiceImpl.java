package com.subtitlescorrector.service.s3;

import java.io.File;

import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

@Service
public class S3ServiceImpl implements S3Service {

	@Override
	public PutObjectResult uploadFileToS3(String key, String bucket, File file){
		
		AmazonS3 s3Client = AmazonS3Client.builder()
			    .withRegion(Regions.EU_NORTH_1).build();
			    
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, file);
		
		return s3Client.putObject(putObjectRequest);
		
	}
	
}
