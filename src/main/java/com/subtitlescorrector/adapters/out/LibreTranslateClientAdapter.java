package com.subtitlescorrector.adapters.out;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.subtitlescorrector.core.domain.deepl.DeepLUsageData;
import com.subtitlescorrector.core.domain.libre.LibreResponse;
import com.subtitlescorrector.core.domain.translation.TranslationLine;
import com.subtitlescorrector.core.domain.translation.TranslationResponse;
import com.subtitlescorrector.core.port.TranslationPort;

@Service
@Primary
@PropertySource("classpath:businessProperties.properties")
public class LibreTranslateClientAdapter implements TranslationPort {

	WebClient client;

	public LibreTranslateClientAdapter(WebClient.Builder builder,
			@Value("${subtitle.translation.libre.url}") String apiUrl) {
		client = builder.baseUrl(apiUrl).defaultHeader("Content-Type", "application/json").build();
	}

	@Override
	public TranslationResponse translate(List<String> lines, String isoLanguage) {

		TranslationResponse translationResponse = new TranslationResponse();
		translationResponse.setTranslations(new ArrayList<TranslationLine>());

		LibreResponse response = client.post()
				.uri("/translate")
				.bodyValue(Map.of("q", lines, "source", "auto", "target", isoLanguage))
				.retrieve()
				.bodyToMono(LibreResponse.class)
				.block();
		
		return response.toTranslationResponse();
	}

	@Override
	public DeepLUsageData getUsageInfo() {
		DeepLUsageData data = new DeepLUsageData();
		data.setCharacterCount(0L);
		data.setCharacterLimit(0L);
		return data;
	}

}
