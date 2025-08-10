package com.subtitlescorrector.adapters.out;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.subtitlescorrector.core.domain.ai.ChatResponse;
import com.subtitlescorrector.core.port.AiServicePort;

@Service
public class AiServiceAdapter implements AiServicePort {

	private static final String GPT_4O_MINI_MODEL = "gpt-4o-mini";
	private static final String GPT_4O_MODEL = "gpt-4o";

	private static final String CHAT_COMPLETIONS_URI = "/chat/completions";

	Logger log = LoggerFactory.getLogger(AiServiceAdapter.class);

	@Autowired
	WebClient openAiWebClient;
	
	@Override
	public ChatResponse askOpenAi(String system, String user) {
		ChatResponse response = openAiWebClient.post()
	            .uri(CHAT_COMPLETIONS_URI)
	            .bodyValue(Map.of(
	                    "model", GPT_4O_MINI_MODEL,
	                    "temperature", 0.3,
	                    "messages", List.of(
	                        Map.of("role", "system", "content", system),
	                        Map.of("role", "user", "content", user)
	                    ),
	                    "response_format", Map.of(
	                        "type", "json_schema",
	                        "json_schema", Map.of(
	                            "name", "SubtitleCorrections",
	                            "schema", Map.of(
	                                "type", "object",
	                                "properties", Map.of(
	                                    "corrections", Map.of(
	                                        "type", "array",
	                                        "items", Map.of(
	                                            "type", "object",
	                                            "properties", Map.of(
	                                                "number", Map.of("type", "string"),
	                                                "correction", Map.of("type", "string"),
	                                                "description", Map.of("type", "string")
	                                            ),
	                                            "required", List.of("number", "correction", "description")
	                                        )
	                                    )
	                                ),
	                                "required", List.of("corrections")
	                            )
	                        )
	                    )
	                ))
	                .retrieve()
	                .bodyToMono(ChatResponse.class)
	                .block();
	    return response;
	}
	
}
