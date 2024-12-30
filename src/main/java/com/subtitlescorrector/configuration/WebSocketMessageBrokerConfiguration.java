package com.subtitlescorrector.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfiguration implements WebSocketMessageBrokerConfigurer{

	@Autowired
	ApplicationProperties properties;
	
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
	
		//registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/app");
		//registry.setUserDestinationPrefix("/user"); //with this enabled, messages are not being transmitted correctly
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
		String allowedOrigins = "http://localhost:8080";
		if(properties.isProdEnvironment()) {
			allowedOrigins = "https://subtitles-corrector.com";
		}
		
		registry.addEndpoint("/sc-ws-connection-entrypoint")
        .setAllowedOriginPatterns(allowedOrigins) // Allow requests from any origin (adjust for production)
        .withSockJS(); // Enable SockJS fallback options
		
	}
	
}
