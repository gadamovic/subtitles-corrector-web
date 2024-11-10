package com.subtitlescorrector.producers;

import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.KafkaTopic;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

@Service
public class SubtitleCorrectionEventProducerImpl implements SubtitleCorrectionEventProducer {

	Logger log = LoggerFactory.getLogger(SubtitleCorrectionEventProducerImpl.class);

	@Autowired
	SubtitleCorrectionProducerProvider provider;
	
	@Override
	public Future<RecordMetadata> generateCorrectionEvent(SubtitleCorrectionEvent event) {

		Producer<Long, SubtitleCorrectionEvent> producer = provider.getProducer();

		ProducerRecord<Long, SubtitleCorrectionEvent> record = new ProducerRecord<>(KafkaTopic.SUBTITLE_CORRECTIONS.getTopicName(), event);
		Future<RecordMetadata> recordMetadata = producer.send(record);
		return recordMetadata;

	}
	
}
