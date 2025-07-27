package com.subtitlescorrector.service.redis;

public enum RedisKeys {

	LAST_S3_UPLOAD("cache:last_s3_upload"),
	WEBSOCKET_USER_ID_WEBSOCKET_SESSION_ID("cache:websocket_user_id_websocket_session_id"),
	NUMBER_OF_EMAILS_PER_HOUR("cache:number_emails_per_hour"),
	USER_SUBTITLE_CURRENT_VERSION("cache:subtitle_edit_current_version"),
	USER_SUBTITLE_CONVERSION_DATA("cache:subtitle_conversion_data"),
	NUMBER_OF_PROCESSED_SUBTITLES_PER_USER("cache:subtitles_processed_count");

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

