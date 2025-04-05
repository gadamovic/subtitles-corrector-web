package com.subtitlescorrector.consumers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.service.CustomWebSocketHandler;
import com.subtitlescorrector.service.WebSocketMessageBrokerService;

//@Service
public class SubtitleCorrectionEventConsumerImpl implements SubtitleCorrectionEventConsumer {

	Logger log = LoggerFactory.getLogger(SubtitleCorrectionEventConsumerImpl.class);
	
	private final CustomWebSocketHandler webSocketHandler;
	
	@Autowired
	public SubtitleCorrectionEventConsumerImpl(CustomWebSocketHandler webSocketHandler) {
		this.webSocketHandler = webSocketHandler;
	}
	
	@KafkaListener(id = "subtitlesCorrectionListenerContainer", topics = "subtitlesCorrections", containerFactory = "kafkaListenerContainerFactory")
	public void consumeCorrections(List<SubtitleCorrectionEvent> events) {
		for(SubtitleCorrectionEvent event : events) {
			webSocketHandler.sendMessage(event);
			log.info(event.toString());
		}
	}

}
