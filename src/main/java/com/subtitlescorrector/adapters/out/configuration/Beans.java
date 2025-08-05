package com.subtitlescorrector.adapters.out.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {

	@Bean
	public ApplicationProperties getGlobalConfig() {
		ApplicationProperties config = new ApplicationProperties();
		config.init();
		return config;
	}
	
}
