package com.subtitlescorrector.core.service.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.port.WebSocketOutboundPort;
import com.subtitlescorrector.core.util.Util;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

@Service
public class WebSocketMessageSenderImpl implements WebSocketMessageSender {

	@Autowired
	WebSocketOutboundPort webSocketPort;
	
	@Override
	public void sendMessage(SubtitleCorrectionEvent event) {
		webSocketPort.sendMessage(Util.subtitleCorrectionEventToJson(event), event.getWebSocketSessionId().toString());
	}

}
