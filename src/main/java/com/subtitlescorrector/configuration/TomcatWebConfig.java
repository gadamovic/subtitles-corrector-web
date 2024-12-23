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
	private static final String SCHEME = "https";
	private static final String PKCS12_KEYSTORE_TYPE = "PKCS12";
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
		if (properties.getEnvironment().equals(ApplicationProperties.PROD_ENVIRONMENT)) {

			factory.addConnectorCustomizers(connector -> {
				connector.setScheme(SCHEME);
				connector.setSecure(true);
				connector.setPort(TLS_PORT);
			});

			Ssl ssl = new Ssl();
			ssl.setKeyStore(new File(properties.getKeystoreLocation()).getAbsolutePath());
			ssl.setKeyStorePassword(System.getenv(VariablesEnum.TOMCAT_KEYSTORE_PASSWORD.getName()));
			ssl.setKeyStoreType(PKCS12_KEYSTORE_TYPE);

			factory.setSsl(ssl);
		}

	}
}
