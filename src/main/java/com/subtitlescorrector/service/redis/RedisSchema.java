package com.subtitlescorrector.service.redis;

/**
 * 
 * @author Gavrilo Adamovic
 * Class containing static methods for obtaining Redis keys for objects
 */
public class RedisSchema {

	/**
	 * Timestamp of last s3 upload
	 * @return
	 */
	public static String createUserLastPostTimestampKey() {
		return RedisKeys.LAST_S3_UPLOAD.getKey();
	}
	
}
