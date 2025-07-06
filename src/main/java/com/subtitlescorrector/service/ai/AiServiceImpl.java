package com.subtitlescorrector.service.ai;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subtitlescorrector.controller.rest.TestController;
import com.subtitlescorrector.domain.openai.ChatResponse;

import reactor.core.publisher.Mono;

@Service
public class AiServiceImpl {

	private static final String GPT_4O_MINI_MODEL = "gpt-4o-mini";

	private static final String CHAT_COMPLETIONS_URI = "/chat/completions";

	Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

	@Autowired
	WebClient aiWebClient;
	
	public ChatResponse askOpenAi(String prompt) {
	    String response = aiWebClient.post()
	        .uri(CHAT_COMPLETIONS_URI)
	        .bodyValue(Map.of(
	            "model", GPT_4O_MINI_MODEL,
	            "temperature", 0,
	            "messages", List.of(
	                Map.of("role", "user", "content", prompt)
	            )
	        ))
	        .retrieve()
	        .bodyToMono(String.class).block();
	    
	    ObjectMapper mapper = new ObjectMapper();
	    try {
			ChatResponse chatResponse = mapper.readValue(response, ChatResponse.class);
			return chatResponse;
		} catch (JsonProcessingException e) {
			log.error("Error deserializing json!\n" + response, e);
			return null;
		}
	    
	}
	
}
