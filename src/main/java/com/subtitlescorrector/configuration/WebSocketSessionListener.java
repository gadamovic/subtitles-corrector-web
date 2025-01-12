package com.subtitlescorrector.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import com.subtitlescorrector.service.redis.RedisService;

//@Component
@Deprecated
public class WebSocketSessionListener {

	@Autowired
	RedisService redisService;
	
    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String webSocketUserId = StompHeaderAccessor.wrap((Message<?>) headerAccessor.getHeader("simpConnectMessage")).getFirstNativeHeader("webSocketUserId");
        
        redisService.addWebSocketUserToCache(webSocketUserId, sessionId);
        
    }
}