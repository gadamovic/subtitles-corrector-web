package com.subtitlescorrector.adapters.out.configuration;

import java.io.File;
import java.util.Collections;

import org.apache.catalina.Valve;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.catalina.valves.RemoteIpValve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.core.domain.VariablesEnum;

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

		if (properties.getTlsEnabled()) {
			addHttpsConnector(factory);
	        addAdditionalHttpConnector(factory);
			addSslConfiguration(factory);
		}
		
		if(properties.isProdEnvironment()) {
			factory.setEngineValves(Collections.singletonList(remoteIpValve()));
			factory.addContextValves(accessLogValve());
			factory.setBaseDirectory(new File("/home/logs/access_logs"));
		}

	}

	private void addSslConfiguration(TomcatServletWebServerFactory factory) {
		Ssl ssl = new Ssl();
		ssl.setKeyStore(new File(properties.getKeystoreLocation()).getAbsolutePath());
		ssl.setKeyStorePassword(System.getenv(VariablesEnum.TOMCAT_KEYSTORE_PASSWORD.getName()));
		ssl.setKeyStoreType(PKCS12_KEYSTORE_TYPE);

		factory.setSsl(ssl);
	}

	private void addAdditionalHttpConnector(TomcatServletWebServerFactory factory) {
		Connector httpConnector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		httpConnector.setScheme(HTTP_SCHEME);
		httpConnector.setPort(NON_TLS_PORT);
		httpConnector.setSecure(false);
		
		factory.addAdditionalTomcatConnectors(httpConnector);
	}

	private void addHttpsConnector(TomcatServletWebServerFactory factory) {
		factory.addConnectorCustomizers(connector -> {
			connector.setScheme(HTTPS_SCHEME);
			connector.setSecure(true);
			connector.setPort(TLS_PORT);
		});
	}
	
    private Valve remoteIpValve() {
    	RemoteIpValve remoteIpValve = new RemoteIpValve();
    	remoteIpValve.setInternalProxies("127\\.0\\.0\\.1|::1|10\\..*|172\\..*|192\\.168\\..*");
    	remoteIpValve.setRemoteIpHeader("x-forwarded-for");
    	remoteIpValve.setProxiesHeader("x-forwarded-by");
    	remoteIpValve.setProtocolHeader("x-forwarded-proto");
		return remoteIpValve;
	}

	private Valve accessLogValve() {
        AccessLogValve logValve = new AccessLogValve();
        logValve.setPattern("%h %l %u %t \"%r\" %s %b");
        logValve.setDirectory("tomcat_access_logs");
        logValve.setPrefix("access_log");
        logValve.setSuffix(".log");
        logValve.setRequestAttributesEnabled(true);
        logValve.setEnabled(true);
        return logValve;
    }
}
