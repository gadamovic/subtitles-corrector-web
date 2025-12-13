package com.subtitlescorrector.adapters.out;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.subtitlescorrector.core.domain.deepl.DeepLResponse;
import com.subtitlescorrector.core.port.DeeplClientPort;

@Service
public class DeeplClientAdapter implements DeeplClientPort{

	@Value("${deepl.api.key}")
	String apiKey;
	
	public List<String> translate(List<String> lines){	
		
	WebClient client = WebClient.builder()
		    .baseUrl("https://api-free.deepl.com/v2")
		    .defaultHeader("Authorization", "DeepL-Auth-Key " + apiKey)
		    .defaultHeader("Content-Type", "application/json")
		    .build();

		DeepLResponse response = client.post()
		    .uri("/translate")
		    .bodyValue(Map.of("text",List.of(lines.get(2)),"target_lang", "DE"))
		    .retrieve()
		    .bodyToMono(DeepLResponse.class)
		    .block();
		
		return null;
		
	}
	
}
