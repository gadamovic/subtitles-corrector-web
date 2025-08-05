package com.subtitlescorrector.core.service.websocket;

import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

public interface WebSocketMessageSender {

	public void sendMessage(SubtitleCorrectionEvent event);
	
}
