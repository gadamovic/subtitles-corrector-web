package com.subtitlescorrector.adapters.in;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.adapters.WebSocketMessageBrokerAdapter;
import com.subtitlescorrector.core.port.SubtitleCorrectionEventConsumerPort;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

//@Service
public class SubtitleCorrectionEventConsumerImpl implements SubtitleCorrectionEventConsumerPort {

	Logger log = LoggerFactory.getLogger(SubtitleCorrectionEventConsumerImpl.class);
	
	private final WebSocketMessageSender webSocketMessageSender;
	
	@Autowired
	public SubtitleCorrectionEventConsumerImpl(WebSocketMessageSender webSocketMessageSender) {
		this.webSocketMessageSender = webSocketMessageSender;
	}
	
	@KafkaListener(id = "subtitlesCorrectionListenerContainer", topics = "subtitlesCorrections", containerFactory = "kafkaListenerContainerFactory")
	public void consumeCorrections(List<SubtitleCorrectionEvent> events) {
		for(SubtitleCorrectionEvent event : events) {
			webSocketMessageSender.sendMessage(event);
			log.info(event.toString());
		}
	}

}
