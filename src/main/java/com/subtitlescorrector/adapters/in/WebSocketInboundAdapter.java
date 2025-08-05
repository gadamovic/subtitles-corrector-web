package com.subtitlescorrector.adapters.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.subtitlescorrector.core.port.WebSocketSessionRegistryPort;
import com.subtitlescorrector.core.service.websocket.WebSocketUserRegistrationService;
import com.subtitlescorrector.core.util.Constants;

@Service
public class WebSocketInboundAdapter extends TextWebSocketHandler {

	Logger log = LoggerFactory.getLogger(WebSocketInboundAdapter.class);
	
	@Autowired
	WebSocketUserRegistrationService userRegistration;
	
	@Autowired
	WebSocketSessionRegistryPort sessionRegistry;
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessionRegistry.register(session.getId(), session);
		log.info("New WebSocket connection: " + session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		boolean success = userRegistration.registerUser(session.getId(), message.getPayload());
		
		if (session.isOpen() && success) {
			session.sendMessage(new TextMessage(Constants.WEB_SOCKET_USER_ACK_SIGNAL));
		}else {
			session.sendMessage(new TextMessage(Constants.WEB_SOCKET_USER_NACK_SIGNAL));
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessionRegistry.remove(session.getId());
		log.info("WebSocket connection closed: " + session.getId());
	}
	
}
