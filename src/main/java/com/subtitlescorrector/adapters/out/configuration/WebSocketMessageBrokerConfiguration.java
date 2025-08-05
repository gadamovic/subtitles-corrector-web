package com.subtitlescorrector.adapters.out.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//@Configuration
//@EnableWebSocketMessageBroker
@Deprecated
public class WebSocketMessageBrokerConfiguration implements WebSocketMessageBrokerConfigurer{

	@Autowired
	ApplicationProperties properties;
	
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
	
		//registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/app");
		registry.setPreservePublishOrder(true);
		//registry.setUserDestinationPrefix("/user"); //with this enabled, messages are not being transmitted correctly
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
		List<String> allowedOrigins = new ArrayList<>();
		allowedOrigins.add("http://localhost:8080");
		if(properties.isProdEnvironment()) {
			allowedOrigins.add("https://subtitles-corrector.com");
			allowedOrigins.add("https://www.subtitles-corrector.com");
		}
		
		registry.addEndpoint("/sc-ws-connection-entrypoint")
        .setAllowedOriginPatterns(allowedOrigins.toArray(new String[0]))
        .withSockJS(); // Enable SockJS fallback options
		
	}
	
}
