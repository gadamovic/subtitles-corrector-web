package com.subtitlescorrector.controller.rest;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.subtitlescorrector.consumers.SubtitleCorrectionEventConsumer;
import com.subtitlescorrector.domain.KafkaTopic;
import com.subtitlescorrector.domain.VariablesEnum;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.producers.SubtitleCorrectionEventProducer;
import com.subtitlescorrector.producers.SubtitleCorrectionEventProducerImpl;
import com.subtitlescorrector.producers.SubtitleCorrectionProducerProvider;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class KafkaController {

	Logger log = LoggerFactory.getLogger(KafkaController.class);
	
	@Autowired
	SubtitleCorrectionEventConsumer consumer;
	
	@Autowired
	SubtitleCorrectionEventProducer producer;
	
	@Autowired
	SubtitleCorrectionProducerProvider provider;
	
	@RequestMapping(path = "/startConsumingCorrections")
	public void startConsumingCorrections() {
		consumer.consumeCorrections();
	}
	
	@RequestMapping(path = "/testSSLConsume")
	public void testSSLConsume() {

		Properties consumerProperties = new Properties();

		consumerProperties.put("security.protocol", "SSL");
		consumerProperties.put("ssl.keystore.location",System.getenv(VariablesEnum.KAFKA_CLIENT_KEYSTORE_LOCATION.getName()));
		consumerProperties.put("ssl.keystore.password", System.getenv(VariablesEnum.KAFKA_CLIENT_KEYSTORE_PASSWORD.getName()));
		consumerProperties.put("ssl.key.password", System.getenv(VariablesEnum.KAFKA_CLIENT_KEY_PASSWORD.getName()));
		consumerProperties.put("ssl.truststore.location",System.getenv(VariablesEnum.KAFKA_CLIENT_TRUSTSTORE_LOCATION.getName()));
		consumerProperties.put("ssl.truststore.password", System.getenv(VariablesEnum.KAFKA_CLIENT_TRUSTSTORE_PASSWORD.getName()));
		consumerProperties.put("bootstrap.servers", System.getenv(VariablesEnum.KAFKA_BOOTSTRAP_SERVERS.getName()));

		consumerProperties.put("enable.auto.commit", "false");
		consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumerProperties.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
		consumerProperties.put("group.id", "0");
		// consumerProperties.put("specific.avro.reader", "true");
		// consumerProperties.put("schema.registry.url", System.getProperty("secret.kafka.schema_registry.url"));

		try (KafkaConsumer<Long, String> consumer = new KafkaConsumer<>(consumerProperties)) {

			consumer.subscribe(List.of("test-ssl-topic"));

			while (true) {
				ConsumerRecords<Long, String> records = consumer.poll(Duration.ofMillis(250));
				for (ConsumerRecord<Long, String> record : records) {

					String consumedEvent = record.value();

					log.info("kafka topic offset = {}, value = {}", record.offset(), consumedEvent);

					OffsetAndMetadata offsetMeta = new OffsetAndMetadata(record.offset() + 1, "");

					Map<TopicPartition, OffsetAndMetadata> kaOffsetMap = new HashMap<>();
					kaOffsetMap.put(new TopicPartition("test-ssl-topic", record.partition()), offsetMeta);

					consumer.commitSync(kaOffsetMap);
				}
			}
		}

	}
	
	@RequestMapping(path = "/testSSLProduce")
	public void testSSLProduce() {
		//System.setProperty("javax.net.debug", "ssl");
		Properties producerProperties = new Properties();

	    producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
	    producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	    
	    producerProperties.put("security.protocol", "SSL");
	    producerProperties.put("ssl.keystore.location",System.getenv(VariablesEnum.KAFKA_CLIENT_KEYSTORE_LOCATION.getName()));
	    producerProperties.put("ssl.keystore.password", System.getenv(VariablesEnum.KAFKA_CLIENT_KEYSTORE_PASSWORD.getName()));
	    producerProperties.put("ssl.key.password", System.getenv(VariablesEnum.KAFKA_CLIENT_KEY_PASSWORD.getName()));
	    producerProperties.put("ssl.truststore.location",System.getenv(VariablesEnum.KAFKA_CLIENT_TRUSTSTORE_LOCATION.getName()));
	    producerProperties.put("ssl.truststore.password", System.getenv(VariablesEnum.KAFKA_CLIENT_TRUSTSTORE_PASSWORD.getName()));
	    producerProperties.put("bootstrap.servers", System.getenv(VariablesEnum.KAFKA_BOOTSTRAP_SERVERS.getName()));
		
		try(Producer<Long, String> producer = provider.getSpecificProducer(producerProperties)){

			ProducerRecord<Long, String> record = new ProducerRecord<>("test-ssl-topic", "test");
			producer.send(record);

		}
	}
	
}
