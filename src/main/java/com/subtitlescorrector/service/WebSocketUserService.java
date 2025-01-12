package com.subtitlescorrector.service;

import java.util.List;

import org.springframework.web.socket.WebSocketSession;

public interface WebSocketUserService {

	void processUserRegistrationIfNeeded(String message, WebSocketSession session);

	WebSocketSession findUserSession(String webSocketSessionId, List<WebSocketSession> sessions);

}