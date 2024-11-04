package com.subtitlescorrector.producers;

import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.RecordMetadata;

import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

public interface SubtitleCorrectionEventProducer {

	Future<RecordMetadata> generateCorrectionEvent(SubtitleCorrectionEvent saying);

}