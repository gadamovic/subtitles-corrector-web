package com.subtitlescorrector.adapters.out;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.subtitlescorrector.core.domain.deepl.DeepLResponse;
import com.subtitlescorrector.core.domain.deepl.DeepLUsageData;
import com.subtitlescorrector.core.port.DeeplClientPort;

@Service
public class DeeplClientAdapter implements DeeplClientPort{

	@Value("${deepl.api.key}")
	String apiKey;
	
	private WebClient client = WebClient.builder()
		    .baseUrl("https://api-free.deepl.com/v2")
		    .defaultHeader("Content-Type", "application/json")
		    .build();
	
	public List<String> translate(List<String> lines){	
		
		DeepLResponse response = client.post()
		    .uri("/translate")
		    .header("Authorization", "DeepL-Auth-Key " + apiKey) //Placed here because @value is not available during object instantiation
		    .bodyValue(Map.of("text",List.of(lines.get(2)),"target_lang", "DE"))
		    .retrieve()
		    .bodyToMono(DeepLResponse.class)
		    .block();
		
		return null;
		
	}

	@Override
	public DeepLUsageData getUsageInfo() {
		
		DeepLUsageData response = client.get()
			    .uri("/usage")
			    .header("Authorization", "DeepL-Auth-Key " + apiKey)
			    .retrieve()
			    .bodyToMono(DeepLUsageData.class)
			    .block();
		
		return response;
	}
	
}
