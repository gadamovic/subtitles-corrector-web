package com.subtitlescorrector.core.service.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.port.WebSocketOutboundPort;
import com.subtitlescorrector.core.util.Util;


@Service
public class WebSocketMessageSenderImpl implements WebSocketMessageSender {

	@Autowired
	WebSocketOutboundPort webSocketPort;
	
	@Autowired
	UserData user;
	
	@Override
	public void sendMessage(SubtitleCorrectionEvent event) {
		webSocketPort.sendMessage(Util.subtitleCorrectionEventToJson(event), user.getWebSocketSessionId());
	}

}
