package com.subtitlescorrector.adapters.out.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.subtitlescorrector.adapters.in.WebSocketInboundAdapter;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	WebSocketInboundAdapter handler;
	
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    	
        List<String> allowedOrigins = new ArrayList<>();
        allowedOrigins.add("http://localhost:8080");
        allowedOrigins.add("http://localhost:8081");
        allowedOrigins.add("http://localhost:8082");
        if(properties.isProdEnvironment()) {
            allowedOrigins.add("https://subtitles-corrector.com");
            allowedOrigins.add("https://www.subtitles-corrector.com");
        }
    	
        registry.addHandler(handler, "/sc-ws-connection-entrypoint")
                .setAllowedOrigins(allowedOrigins.toArray(new String[0]));
    }
}