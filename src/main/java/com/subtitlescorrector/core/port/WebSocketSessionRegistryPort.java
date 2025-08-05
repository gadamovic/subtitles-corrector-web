package com.subtitlescorrector.core.port;

import org.springframework.web.socket.WebSocketSession;

public interface WebSocketSessionRegistryPort {

	public void register(String sessionId, WebSocketSession session);
	public void remove(String sessionId);
	public WebSocketSession findSession(String sessionId);
	
}
