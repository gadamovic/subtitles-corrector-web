package com.subtitlescorrector.service.redis;

public enum RedisKeys {

	LAST_S3_UPLOAD("cache:last_s3_upload");

	RedisKeys(String key) {
		this.key = key;
	}
	
	String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	
}

