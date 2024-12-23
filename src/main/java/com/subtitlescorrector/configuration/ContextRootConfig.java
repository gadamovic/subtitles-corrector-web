package com.subtitlescorrector.configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;

@Component
public class ContextRootConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

	@Autowired
	ApplicationProperties properties;
	
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
    	
    	String contextPath = properties.getContextRoot();
    	if(contextPath.equals("/")) {
    		contextPath = "";
    	}
    	
        factory.setContextPath(contextPath);
    }
}
