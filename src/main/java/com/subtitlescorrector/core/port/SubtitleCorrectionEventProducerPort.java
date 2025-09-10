package com.subtitlescorrector.core.port;

import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.RecordMetadata;

import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;



public interface SubtitleCorrectionEventProducerPort {

	Future<RecordMetadata> generateCorrectionEvent(SubtitleCorrectionEvent event);

}