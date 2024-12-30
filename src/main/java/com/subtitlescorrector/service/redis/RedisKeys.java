package com.subtitlescorrector.service.redis;

public enum RedisKeys {

	LAST_S3_UPLOAD("cache:last_s3_upload"), WEBSOCKET_USER_ID_WEBSOCKET_SESSION_ID("cache:websocket_user_id_websocket_session_id");

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

