package com.subtitlescorrector.adapters.out;

public enum S3BucketNames {
	
	SUBTITLES_UPLOADED_FILES("subtitles-uploaded-files");

	String bucketName;

	S3BucketNames(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getBucketName() {
		return this.bucketName;
	}
}
