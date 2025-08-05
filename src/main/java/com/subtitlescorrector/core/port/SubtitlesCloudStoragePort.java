package com.subtitlescorrector.core.port;

import java.io.File;

import com.subtitlescorrector.core.domain.SubtitlesFileProcessorResponse;

import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public interface SubtitlesCloudStoragePort {
	void store(String key, File file);
	void storeIfProd(String key, File file);
}