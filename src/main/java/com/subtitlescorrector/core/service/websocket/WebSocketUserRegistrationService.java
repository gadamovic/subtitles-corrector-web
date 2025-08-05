package com.subtitlescorrector.core.service.websocket;

public interface WebSocketUserRegistrationService {

	public boolean registerUser(String sessionId, String payload);

}
