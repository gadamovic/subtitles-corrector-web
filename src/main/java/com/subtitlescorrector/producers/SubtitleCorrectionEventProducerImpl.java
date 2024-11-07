package com.subtitlescorrector.producers;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.KafkaTopic;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

@Service
public class SubtitleCorrectionEventProducerImpl implements SubtitleCorrectionEventProducer {

	Logger log = LoggerFactory.getLogger(SubtitleCorrectionEventProducerImpl.class);
	
	@Override
	public Future<RecordMetadata> generateCorrectionEvent(SubtitleCorrectionEvent event){
		
		Properties producerProperties = new Properties();
	    producerProperties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
	    producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
	    producerProperties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
	    producerProperties.put("schema.registry.url", "http://localhost:8081");
	    producerProperties.put("avro.use.logical.type.converters", "true");
	    
	    //producerProperties.put("interceptor.classes", "com.say.say.events.interceptors.SayingPostedEventProducerInterceptor");

	    try(Producer<Long, SubtitleCorrectionEvent> producer = new KafkaProducer<Long, SubtitleCorrectionEvent>(producerProperties)){
	    	
	    	ProducerRecord<Long, SubtitleCorrectionEvent> record = new ProducerRecord<>(KafkaTopic.SUBTITLE_CORRECTIONS.getTopicName(), event);
	    	
	    	Future<RecordMetadata> recordMetadata = producer.send(record);
	    	return recordMetadata;
	    }
	}
	
}
