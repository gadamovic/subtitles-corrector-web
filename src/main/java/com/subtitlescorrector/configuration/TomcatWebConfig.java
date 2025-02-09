package com.subtitlescorrector.configuration;

import java.io.File;

import org.apache.catalina.Valve;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.valves.AccessLogValve;
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
			
	        Connector httpConnector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
	        httpConnector.setScheme(HTTP_SCHEME);
	        httpConnector.setPort(NON_TLS_PORT);
	        httpConnector.setSecure(false);
			
			factory.addAdditionalTomcatConnectors(httpConnector);

			Ssl ssl = new Ssl();
			ssl.setKeyStore(new File(properties.getKeystoreLocation()).getAbsolutePath());
			ssl.setKeyStorePassword(System.getenv(VariablesEnum.TOMCAT_KEYSTORE_PASSWORD.getName()));
			ssl.setKeyStoreType(PKCS12_KEYSTORE_TYPE);

			factory.setSsl(ssl);
		}
		
		if(properties.isProdEnvironment()) {
			factory.addContextValves(accessLogValve());
			factory.setBaseDirectory(new File("/home/logs/access_logs"));
		}

	}
	
    private Valve accessLogValve() {
        AccessLogValve logValve = new AccessLogValve();
        logValve.setPattern("%h %l %u %t \"%r\" %s %b");
        logValve.setDirectory("tomcat_access_logs");
        logValve.setPrefix("access_log");
        logValve.setSuffix(".log");
        logValve.setEnabled(true);
        return logValve;
    }
}
