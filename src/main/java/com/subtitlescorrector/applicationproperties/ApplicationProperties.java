package com.subtitlescorrector.applicationproperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.subtitlescorrector.domain.VariablesEnum;
import com.subtitlescorrector.util.Util;

/**
 * Class holding configuration parameters and data
 * @author Gavrilo Adamovic
 *
 */
public class ApplicationProperties {

	private static final String PROD_ENVIRONMENT = "prod";

	private static final Logger log = LoggerFactory.getLogger(ApplicationProperties.class);

	private Map<String,String> propertyMap;
	
	private static final String UPLOAD_FOLDER_LOCATION = "upload.folder_location";
	private static final String REDIS_CONNECTION_PORT_KEY = "redis.connection.port";
	private static final String REDIS_CONNECTION_HOST_KEY = "redis.connection.host";
	private static final String S3_UPLOAD_COOLDOWN = "aws.s3.upload.cooldown.minutes";
	
	public void init() {
		
		try {
			InputStream is = new ClassPathResource("businessProperties.properties").getInputStream();
			propertyMap = Util.loadPropertiesFileIntoMap(is);
			
			String environment = System.getenv(VariablesEnum.APPLICATION_ENVIRONMENT.getName());
			
			if(environment.equalsIgnoreCase(PROD_ENVIRONMENT)) {
				InputStream is2 = new ClassPathResource("businessProperties-prod.properties").getInputStream();
				propertyMap.putAll(Util.loadPropertiesFileIntoMap(is2));
			}
			
			log.info("Application properties initialized!");
		} catch (IOException e) {
			log.error("Error loading business properties!", e);
		}
		
	}
	
	public String getProperty(String key) {
		String property = propertyMap.get(key);
		if(Util.isNotBlank(property)) {
			return property;
		}else {
			log.error("Property with key {} not found!", key);
			return null;
		}
	}

	public String getUploadFolderLocation() {
		return getProperty(UPLOAD_FOLDER_LOCATION);
	}
	
	public String getRedisConnectionHostAsString() {
		return getProperty(REDIS_CONNECTION_HOST_KEY);
	}
	
	public Integer getRedisConnectionPortAsInt() {
		return Integer.parseInt(getProperty(REDIS_CONNECTION_PORT_KEY));
	}
	
	public Long getS3UploadCooldownMinutes() {
		return Long.parseLong(getProperty(S3_UPLOAD_COOLDOWN));
	}
	
}
