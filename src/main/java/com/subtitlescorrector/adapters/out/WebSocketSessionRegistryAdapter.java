package com.subtitlescorrector.adapters.out;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.subtitlescorrector.core.port.WebSocketSessionRegistryPort;

@Service
public class WebSocketSessionRegistryAdapter implements WebSocketSessionRegistryPort {

	Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
	
	@Override
	public void register(String sessionId, WebSocketSession session) {
		sessions.put(sessionId, session);
	}

	@Override
	public void remove(String sessionId) {
		sessions.remove(sessionId);
	}

	@Override
	public WebSocketSession findSession(String sessionId) {
		return sessions.get(sessionId);
	}

}
