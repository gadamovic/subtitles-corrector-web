package com.subtitlescorrector.adapters.out;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.subtitlescorrector.core.domain.deepl.DeepLResponse;
import com.subtitlescorrector.core.domain.deepl.DeepLUsageData;
import com.subtitlescorrector.core.port.DeepLUsageMetricsPort;
import com.subtitlescorrector.core.port.DeeplClientPort;

@Service
public class DeeplClientAdapter implements DeeplClientPort{

	@Autowired
	DeepLUsageMetricsPort usageMetrics;
	
	WebClient client;
	
	public DeeplClientAdapter(WebClient.Builder builder, @Value("${deepl.api.key}") String apiKey) {
		client = builder
			    .baseUrl("https://api-free.deepl.com/v2")
			    .defaultHeader("Content-Type", "application/json")
			    .defaultHeader("Authorization", "DeepL-Auth-Key " + apiKey)
			    .build();
	}
	
	public DeepLResponse translate(List<String> lines, String isoLanguage){	
		
		DeepLResponse response = client.post()
		    .uri("/translate")
		    .bodyValue(Map.of("text", lines, "target_lang", isoLanguage))
		    .retrieve()
		    .bodyToMono(DeepLResponse.class)
		    .block();
		
		return response;
	}

	@Override
	public DeepLUsageData getUsageInfo() {
		
		DeepLUsageData response = client.get()
			    .uri("/usage")
			    .retrieve()
			    .bodyToMono(DeepLUsageData.class)
			    .block();
		
		return response;
	}
	
}
