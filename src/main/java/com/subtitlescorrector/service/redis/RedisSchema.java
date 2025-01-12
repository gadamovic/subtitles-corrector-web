package com.subtitlescorrector.service.redis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 
 * @author Gavrilo Adamovic
 * Class containing static methods for obtaining Redis keys for objects
 */
public class RedisSchema {

	/**
	 * Timestamp of last s3 upload
	 * @param ip 
	 * @return
	 */
	public static String createLastS3UploadForIp(String ip) {
		return RedisKeys.LAST_S3_UPLOAD.getKey() + "_" + ip;
	}
	
	public static String createWebsocketUserIdWebSocketSessionId(String userId) {
		return RedisKeys.WEBSOCKET_USER_ID_WEBSOCKET_SESSION_ID.getKey() + ":" + userId;
	}
	
	public static String createNumberOfEmailsSentInCurrentHour() {
		return RedisKeys.NUMBER_OF_EMAILS_PER_HOUR.getKey() + "_" + getCurrentDateAndHour();
	}

	private static String getCurrentDateAndHour() {
		
		LocalDateTime now = LocalDateTime.now();
		
		String key = now.format(DateTimeFormatter.ofPattern("YYYY-MM-dd-HH"));
		
		return key;
	}
	
	public static String createUserSubtitleCurrentVersionKey(String userId) {
		return RedisKeys.USER_SUBTITLE_CURRENT_VERSION.getKey() + ":" + userId;
	}
	
}
