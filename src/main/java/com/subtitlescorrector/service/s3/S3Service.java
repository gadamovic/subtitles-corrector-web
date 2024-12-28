package com.subtitlescorrector.service.s3;

import java.io.File;

import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public interface S3Service {

	PutObjectResponse uploadFileToS3(String key, String bucket, File file, String ip);

}