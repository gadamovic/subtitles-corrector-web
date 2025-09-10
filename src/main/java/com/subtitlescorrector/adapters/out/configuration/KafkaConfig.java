package com.subtitlescorrector.adapters.out.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import org.apache.kafka.common.serialization.StringDeserializer;

import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.VariablesEnum;
import com.subtitlescorrector.core.util.Constants;


import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

//@Configuration
//@EnableKafka
public class KafkaConfig {

	@Bean
	public ProducerFactory<Void, SubtitleCorrectionEvent> producerFactory() {
	    return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public Map<String, Object> producerConfigs() {
		
	    Map<String, Object> props = new HashMap<>();
	    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv(VariablesEnum.KAFKA_BOOTSTRAP_SERVERS.getName()));
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
	    props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, System.getenv(VariablesEnum.KAFKA_SCHEMA_REGISTRY_URL.getName()));
	    
	    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
	    props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,System.getenv(VariablesEnum.KAFKA_CLIENT_KEYSTORE_LOCATION.getName()));
	    props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, System.getenv(VariablesEnum.KAFKA_CLIENT_KEYSTORE_PASSWORD.getName()));
	    props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, System.getenv(VariablesEnum.KAFKA_CLIENT_KEY_PASSWORD.getName()));
	    props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,System.getenv(VariablesEnum.KAFKA_CLIENT_TRUSTSTORE_LOCATION.getName()));
	    props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, System.getenv(VariablesEnum.KAFKA_CLIENT_TRUSTSTORE_PASSWORD.getName()));
	    
	    return props;
	}

	@Bean
	public KafkaTemplate<Void, SubtitleCorrectionEvent> kafkaTemplate() {
	    return new KafkaTemplate<Void, SubtitleCorrectionEvent>(producerFactory());
	}
	
	
	
    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Void, SubtitleCorrectionEvent>> kafkaListenerContainerFactory() {
    	
        ConcurrentKafkaListenerContainerFactory<Void, SubtitleCorrectionEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(3000);
        
        return factory;
    }

    @Bean
    public ConsumerFactory<Void, SubtitleCorrectionEvent> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
    	
        Map<String, Object> props = new HashMap<>();
        
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv(VariablesEnum.KAFKA_BOOTSTRAP_SERVERS.getName()));
        //props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "0");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, System.getenv(VariablesEnum.KAFKA_SCHEMA_REGISTRY_URL.getName()));
        
	    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
	    props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,System.getenv(VariablesEnum.KAFKA_CLIENT_KEYSTORE_LOCATION.getName()));
	    props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, System.getenv(VariablesEnum.KAFKA_CLIENT_KEYSTORE_PASSWORD.getName()));
	    props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, System.getenv(VariablesEnum.KAFKA_CLIENT_KEY_PASSWORD.getName()));
	    props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,System.getenv(VariablesEnum.KAFKA_CLIENT_TRUSTSTORE_LOCATION.getName()));
	    props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, System.getenv(VariablesEnum.KAFKA_CLIENT_TRUSTSTORE_PASSWORD.getName()));

        return props;
    }
    
    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv(VariablesEnum.KAFKA_BOOTSTRAP_SERVERS.getName()));
        return new KafkaAdmin(configs);
    }
    
//    @Bean
//    public NewTopic topic1() {
//        return TopicBuilder.name(Constants.SUBTITLE_UPLOADED_TOPIC_NAME)
//                .partitions(10)
//                .replicas(3)
//                .compact()
//                .build();
//    }
    
    
	
}
