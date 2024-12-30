package com.subtitlescorrector.service.redis;

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
	
}
