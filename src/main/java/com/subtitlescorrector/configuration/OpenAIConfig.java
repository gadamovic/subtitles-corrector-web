package com.subtitlescorrector.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAIConfig {

	private static final String OPEN_AI_API_KEY_ENV_VARIABLE_NAME = "OPEN_AI_API_KEY";
	private static final String OPENAI_BASE_URL = "https://api.openai.com/v1";

	@Bean
	public WebClient openAiWebClient() {
	    return WebClient.builder()
	        .baseUrl(OPENAI_BASE_URL)
	        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + System.getenv(OPEN_AI_API_KEY_ENV_VARIABLE_NAME))
	        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	        .build();
	}
	
}
