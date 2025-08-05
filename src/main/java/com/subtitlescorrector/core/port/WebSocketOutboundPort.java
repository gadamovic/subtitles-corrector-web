package com.subtitlescorrector.core.port;

public interface WebSocketOutboundPort {

	public void sendMessage(String message, String webSocketSessionId);
	
}
