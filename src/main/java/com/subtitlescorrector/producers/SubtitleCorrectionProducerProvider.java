package com.subtitlescorrector.producers;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.domain.VariablesEnum;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

//@Component
@Deprecated
public class SubtitleCorrectionProducerProvider {

	private Producer<Long, SubtitleCorrectionEvent> producer = null;

	@PostConstruct
	public void initProducer() {
		Properties producerProperties = new Properties();
	    producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
	    producerProperties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
	    producerProperties.put("schema.registry.url", System.getenv(VariablesEnum.KAFKA_SCHEMA_REGISTRY_URL.getName()));
	    producerProperties.put("avro.use.logical.type.converters", "true");
	    
	    producerProperties.put("security.protocol", "SSL");
	    producerProperties.put("ssl.keystore.location",System.getenv(VariablesEnum.KAFKA_CLIENT_KEYSTORE_LOCATION.getName()));
	    producerProperties.put("ssl.keystore.password", System.getenv(VariablesEnum.KAFKA_CLIENT_KEYSTORE_PASSWORD.getName()));
	    producerProperties.put("ssl.key.password", System.getenv(VariablesEnum.KAFKA_CLIENT_KEY_PASSWORD.getName()));
	    producerProperties.put("ssl.truststore.location",System.getenv(VariablesEnum.KAFKA_CLIENT_TRUSTSTORE_LOCATION.getName()));
	    producerProperties.put("ssl.truststore.password", System.getenv(VariablesEnum.KAFKA_CLIENT_TRUSTSTORE_PASSWORD.getName()));

	    producerProperties.put("bootstrap.servers", System.getenv(VariablesEnum.KAFKA_BOOTSTRAP_SERVERS.getName()));
	    
	    //producerProperties.put("interceptor.classes", "com.say.say.events.interceptors.SayingPostedEventProducerInterceptor");

	    Producer<Long, SubtitleCorrectionEvent> producer = new KafkaProducer<Long, SubtitleCorrectionEvent>(producerProperties);
	    this.producer = producer;
	    
	}
	
	public Producer<Long, SubtitleCorrectionEvent> getProducer(){
		return producer;
	}
	
	public <K,V> Producer<K, V> getSpecificProducer(Properties producerProperties){
		
		Producer<K, V> producer = new KafkaProducer<K, V>(producerProperties);
		
		return producer;
	}
	
	@PreDestroy
	public void destroy() {
		producer.close();
	}
	
}
