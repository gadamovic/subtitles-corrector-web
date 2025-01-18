package com.subtitlescorrector.service.s3;

import java.io.File;

import com.subtitlescorrector.domain.SubtitlesFileProcessorResponse;

import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public interface S3Service {

	PutObjectResponse uploadFileToS3(String key, String bucket, File file);
	String uploadAndGetDownloadUrl(File correctedFile);
	PutObjectResponse uploadFileToS3IfProd(String key, String bucket, File file);
}