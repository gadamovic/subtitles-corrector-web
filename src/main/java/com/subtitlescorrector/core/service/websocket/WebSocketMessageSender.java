package com.subtitlescorrector.core.service.websocket;

import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;

public interface WebSocketMessageSender {

	public void sendMessage(SubtitleCorrectionEvent event);
	
}
