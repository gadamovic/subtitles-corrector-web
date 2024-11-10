package com.subtitlescorrector.producers;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class SubtitleCorrectionProducerProvider {

	private Producer<Long, SubtitleCorrectionEvent> producer = null;

	@PostConstruct
	public void initProducer() {
		Properties producerProperties = new Properties();
	    producerProperties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
	    producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
	    producerProperties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
	    producerProperties.put("schema.registry.url", "http://localhost:8081");
	    producerProperties.put("avro.use.logical.type.converters", "true");
	    
	    //producerProperties.put("interceptor.classes", "com.say.say.events.interceptors.SayingPostedEventProducerInterceptor");

	    Producer<Long, SubtitleCorrectionEvent> producer = new KafkaProducer<Long, SubtitleCorrectionEvent>(producerProperties);
	    this.producer = producer;
	    
	}
	
	public Producer<Long, SubtitleCorrectionEvent> getProducer(){
		return producer;
	}
	
	@PreDestroy
	public void destroy() {
		producer.close();
	}
	
}
