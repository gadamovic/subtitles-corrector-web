package com.subtitlescorrector.consumers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.service.WebSocketMessageBrokerService;

@Service
public class SubtitleCorrectionEventConsumerImpl implements SubtitleCorrectionEventConsumer {

	Logger log = LoggerFactory.getLogger(SubtitleCorrectionEventConsumerImpl.class);

	@Autowired
	WebSocketMessageBrokerService webSocketService;
	
	@KafkaListener(id = "subtitlesCorrectionListenerContainer", topics = "subtitlesCorrections", containerFactory = "kafkaListenerContainerFactory")
	public void consumeCorrections(List<SubtitleCorrectionEvent> events) {
		for(SubtitleCorrectionEvent event : events) {
			webSocketService.sendSubtitleCorrectionEventToUser(event);
		}
	}

}
