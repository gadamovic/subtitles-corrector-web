package com.subtitlescorrector.configuration;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.domain.VariablesEnum;

@Component
public class TomcatWebConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

	private static final int TLS_PORT = 8443;
	private static final String HTTPS_SCHEME = "https";
	private static final String PKCS12_KEYSTORE_TYPE = "PKCS12";
	
	private static final int NON_TLS_PORT = 8080;
	private static final String HTTP_SCHEME = "http";
	
	@Autowired
	ApplicationProperties properties;

	@Override
	public void customize(TomcatServletWebServerFactory factory) {

		String contextPath = properties.getContextRoot();
		if (contextPath.equals("/")) {
			contextPath = "";
		}

		factory.setContextPath(contextPath);

		//TLS settings for production
		if (properties.getTlsEnabled()) {

			factory.addConnectorCustomizers(connector -> {
				connector.setScheme(HTTPS_SCHEME);
				connector.setSecure(true);
				connector.setPort(TLS_PORT);
			});
			
			factory.addConnectorCustomizers(connector -> {
				connector.setScheme(HTTP_SCHEME);
				connector.setPort(NON_TLS_PORT);
			});

			Ssl ssl = new Ssl();
			ssl.setKeyStore(new File(properties.getKeystoreLocation()).getAbsolutePath());
			ssl.setKeyStorePassword(System.getenv(VariablesEnum.TOMCAT_KEYSTORE_PASSWORD.getName()));
			ssl.setKeyStoreType(PKCS12_KEYSTORE_TYPE);

			factory.setSsl(ssl);
		}

	}
}
