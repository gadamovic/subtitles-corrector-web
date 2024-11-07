package com.subtitlescorrector.service.s3;

import java.io.File;

import com.amazonaws.services.s3.model.PutObjectResult;

public interface S3Service {

	PutObjectResult uploadFileToS3(String key, String bucket, File file);

}