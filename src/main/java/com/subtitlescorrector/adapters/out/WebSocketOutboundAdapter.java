package com.subtitlescorrector.adapters.out;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.subtitlescorrector.core.port.WebSocketOutboundPort;
import com.subtitlescorrector.core.port.WebSocketSessionRegistryPort;

@Service
public class WebSocketOutboundAdapter implements WebSocketOutboundPort {

	Logger log = LoggerFactory.getLogger(WebSocketOutboundAdapter.class);
	
	@Autowired
	WebSocketSessionRegistryPort sessionRegistry;
	
	public void sendMessage(String message, String webSocketSessionId) {

		if (StringUtils.isNotBlank(webSocketSessionId)) {
			WebSocketSession session = sessionRegistry.findSession(webSocketSessionId);

			if (session != null) {
				try {
					session.sendMessage(new TextMessage(message));
				} catch (IOException e) {
					log.error("Error sending message to websocket!", e);
				}
			}

		} else {
			log.error("Message not sent, WebSocket session not found!");
		}
	}
}
