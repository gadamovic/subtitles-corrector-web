package com.subtitlescorrector.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.util.Constants;
import com.subtitlescorrector.util.Util;

@Service
public class CustomWebSocketHandler extends TextWebSocketHandler {

	Logger log = LoggerFactory.getLogger(CustomWebSocketHandler.class);
	
	@Autowired
	WebSocketUserService webSocketUserService;
	
    List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
		log.info("New WebSocket connection: " + session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		try {
			webSocketUserService.processUserRegistrationIfNeeded(message.getPayload(), session);
			log.info("Websocket message: " + message.getPayload());

		} catch (Exception e) {
			session.sendMessage(new TextMessage(Constants.WEB_SOCKET_USER_NACK_SIGNAL));
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		log.info("WebSocket connection closed: " + session.getId());
	}
	
	
	public void sendMessage(SubtitleCorrectionEvent event) {
		
		WebSocketSession session = webSocketUserService.findUserSession(event.getWebSocketSessionId().toString(), sessions);
		
		if(session != null) {
			try {
				session.sendMessage(new TextMessage(Util.subtitleCorrectionEventToJson(event)));
			} catch (IOException e) {
				log.error("Error sending message to websocket!", e);
			}
		}
		
	}
}
