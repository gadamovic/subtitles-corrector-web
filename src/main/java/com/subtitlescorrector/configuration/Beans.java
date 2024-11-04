package com.subtitlescorrector.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;

@Configuration
public class Beans {

	@Bean
	public ApplicationProperties getGlobalConfig() {
		ApplicationProperties config = new ApplicationProperties();
		config.init();
		return config;
	}
	
}
