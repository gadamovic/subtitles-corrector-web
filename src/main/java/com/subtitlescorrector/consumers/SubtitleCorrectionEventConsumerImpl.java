package com.subtitlescorrector.consumers;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.KafkaTopic;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

@Service
public class SubtitleCorrectionEventConsumerImpl implements SubtitleCorrectionEventConsumer {

	Logger log = LoggerFactory.getLogger(SubtitleCorrectionEventConsumerImpl.class);

	private static final String SUBTITLE_CORRECTIONS_CONSUMER_GROUP_ID = "0";

	@Override
	@Async //TODO: not sure if we need @Async here
	public void consumeCorrections() {

		Properties kaProperties = new Properties();
		kaProperties.put("bootstrap.servers", "localhost:9092,localhost:9093");
		kaProperties.put("enable.auto.commit", "false");
		kaProperties.put("group.id", SUBTITLE_CORRECTIONS_CONSUMER_GROUP_ID);
		kaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		kaProperties.put("value.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
		kaProperties.put("specific.avro.reader", "true");
		kaProperties.put("schema.registry.url", "http://localhost:8081");
		
		try (KafkaConsumer<String, SubtitleCorrectionEvent> consumer = new KafkaConsumer<>(kaProperties)) {

			consumer.subscribe(List.of(KafkaTopic.SUBTITLE_CORRECTIONS.getTopicName()));

			while (true) {
				ConsumerRecords<String, SubtitleCorrectionEvent> records = consumer.poll(Duration.ofMillis(250));
				for (ConsumerRecord<String, SubtitleCorrectionEvent> record : records) {

					SubtitleCorrectionEvent consumedEvent = record.value();

					log.info("subtitle_corrections offset = {}, value = {}", record.offset(), consumedEvent);
					
					OffsetAndMetadata offsetMeta = new OffsetAndMetadata(record.offset() + 1, "");

					Map<TopicPartition, OffsetAndMetadata> kaOffsetMap = new HashMap<>();
					kaOffsetMap.put(new TopicPartition(KafkaTopic.SUBTITLE_CORRECTIONS.getTopicName(), record.partition()), offsetMeta);

					consumer.commitSync(kaOffsetMap);
				}
			}
		}

	}

}
