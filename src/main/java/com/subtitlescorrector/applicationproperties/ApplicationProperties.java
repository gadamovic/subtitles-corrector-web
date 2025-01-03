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

	public static final String PROD_ENVIRONMENT = "prod";

	private static final Logger log = LoggerFactory.getLogger(ApplicationProperties.class);

	private Map<String,String> propertyMap;
	
	private static final String UPLOAD_FOLDER_LOCATION = "upload.folder_location";
	private static final String REDIS_CONNECTION_PORT_KEY = "redis.connection.port";
	private static final String REDIS_CONNECTION_HOST_KEY = "redis.connection.host";
	private static final String S3_UPLOAD_COOLDOWN = "aws.s3.upload.cooldown.minutes";
	private static final String SUBTITLE_CORRECTION_PROCESSED_FILE_NAME_PREFIX = "subtitle.correction.filename.prefix";
	private static final String CERTBOT_CHALLENGE_PATH = "tls.certbot.challenge.path";
	private static final String APPLICATION_CONTEXT_ROOT = "application.context.root";
	private static final String TLS_KEYSTORE_LOCATION = "tls.keystore.location";
	private static final String TLS_ENABLED = "tls.enabled";
	private static final String SUBTITLES_KAFKA_ENABLED = "subtitle.correction.kafka.enabled";
	
	private static final String MAIL_SENDER_HOST_KEY = "mailSender.config.host";
	private static final String MAIL_SENDER_PORT_KEY = "mailSender.config.port";
	private static final String MAIL_SENDER_PROTOCOL_KEY = "mailSender.config.protocol";
	private static final String MAIL_SENDER_USERNAME_KEY = "mailSender.config.username";
	private static final String MAIL_SENDER_PROPERTIES_SMTP_AUTH_KEY = "mailSender.properties.mail.smtp.auth";
	private static final String MAIL_SENDER_PROPERTIES_SMTP_ENABLE_STARTTLS_KEY = "mailSender.properties.mail.smtp.starttls.enable";
	private static final String MAIL_SENDER_PROPERTIES_SMTP_DEBUG_KEY = "mailSender.properties.mail.smtp.debug";
	private static final String MAIL_SENDER_PROPERTIES_SMTP_SSL_TRUST_KEY = "mailSender.properties.mail.smtp.ssl.trust";
	
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
	
	public String getSubtitleCorrectionProcessedFileNamePrefix() {
		return getProperty(SUBTITLE_CORRECTION_PROCESSED_FILE_NAME_PREFIX);
	}
	
	public String getCertbotChallengePath() {
		return getProperty(CERTBOT_CHALLENGE_PATH);
	}

	public String getContextRoot() {
		return getProperty(APPLICATION_CONTEXT_ROOT);
	}
	
	public String getKeystoreLocation() {
		return getProperty(TLS_KEYSTORE_LOCATION);
	}
	
	public String getEnvironment() {
		return System.getenv(VariablesEnum.APPLICATION_ENVIRONMENT.getName());
	}
	
	public Boolean getTlsEnabled() {
		return Boolean.parseBoolean(getProperty(TLS_ENABLED));
	}
	
	public Boolean getSubtitlesKafakEnabled() {
		return Boolean.parseBoolean(getProperty(SUBTITLES_KAFKA_ENABLED));
	}
	
	public Boolean isProdEnvironment() {
		return PROD_ENVIRONMENT.equals(System.getenv(VariablesEnum.APPLICATION_ENVIRONMENT.getName()));
	}
	
	public String getMailSenderHost() {
		return getProperty(MAIL_SENDER_HOST_KEY);
	}
	
	public Integer getMailSenderPort() {
		String portStr = getProperty(MAIL_SENDER_PORT_KEY);
		return Integer.parseInt(portStr);
	}
	
	public String getMailSenderProtocol() {
		return getProperty(MAIL_SENDER_PROTOCOL_KEY);
	}
	
	public String getMailSenderUsername() {
		return getProperty(MAIL_SENDER_USERNAME_KEY);
	}
	
	public String getMailSenderPassword() {
		return System.getenv(VariablesEnum.MAIL_SENDER_PASSWORD.getName());
	}
	
	public String getMailSenderSmtpAuthProperty() {
		return getProperty(MAIL_SENDER_PROPERTIES_SMTP_AUTH_KEY);
	}
	
	public String getMailSenderSmtpEnableStarttlsProperty() {
		return getProperty(MAIL_SENDER_PROPERTIES_SMTP_ENABLE_STARTTLS_KEY);
	}
	
	public String getMailSenderSmtpDebugProperty() {
		return getProperty(MAIL_SENDER_PROPERTIES_SMTP_DEBUG_KEY);
	}

	public String getMailSenderSmtpSSlTrustProperty() {
		return getProperty(MAIL_SENDER_PROPERTIES_SMTP_SSL_TRUST_KEY);
	}

	public String getEmailForSendingEmails() {
		return System.getenv(VariablesEnum.EMAIL_ADDRESS_FOR_SENDING.getName());
	}
	
	public String getAdminEmailAddress() {
		return System.getenv(VariablesEnum.ADMIN_EMAIL_ADDRESS.getName());
	}
	
}
