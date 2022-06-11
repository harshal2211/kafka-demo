package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.MessageHeaders;

import com.example.demo.exception.BidException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.models.CreateBidRequest;
import com.example.demo.models.ResponseBase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class KafkaConfig {

	@Value("${kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${kafka.topic.requestreply-topic}")
	private String requestReplyTopic;

	@Value("${kafka.consumergroup}")
	private String consumerGroup;

	@Autowired
	ObjectMapper mapper;

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return props;
	}

	@Bean
	public ProducerFactory<String, CreateBidRequest> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, CreateBidRequest> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public <T> ReplyingKafkaTemplate<String, CreateBidRequest, ResponseBase<T>> replyKafkaTemplate(
			ProducerFactory<String, CreateBidRequest> pf,
			KafkaMessageListenerContainer<String, ResponseBase<T>> container) {
		return new ReplyingKafkaTemplate<>(pf, container);
	}

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "helloworld");
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return props;
	}

	@Bean
	public <T> KafkaMessageListenerContainer<String, ResponseBase<T>> replyContainer(
			ConsumerFactory<String, ResponseBase<T>> cf) {
		ContainerProperties containerProperties = new ContainerProperties(requestReplyTopic);
		return new KafkaMessageListenerContainer<>(cf, containerProperties);
	}

	@Bean
	public <T> ConsumerFactory<String, ResponseBase<T>> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
				new JsonDeserializer<>(new TypeReference<ResponseBase<T>>() {
				}));
	}
	
	@Bean
	public ConsumerAwareListenerErrorHandler listen3ErrorHandler() {
	    return (m, e, c) -> {
	        MessageHeaders headers = m.getHeaders();
	        ErrorCode errorCode = ErrorCode.INTERNAL_SYSTEM_ERROR;
	        log.error("{}", e);
	        if(e.getCause() instanceof BidException) {
	        	errorCode = ((BidException)e.getCause()).getErrorCode();
	        }
	        return ResponseBase.builder().errorCode(errorCode).build();
	    };
	}

	@Bean
	public <T> KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ResponseBase<T>>> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, ResponseBase<T>> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setReplyTemplate(kafkaTemplate());
		
		DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler((record, exception) -> {
			log.error("Retry limit exhausted. record:: {}", record );
		});
		defaultErrorHandler.addNotRetryableExceptions(BidException.class);
		factory.setCommonErrorHandler(defaultErrorHandler);
		factory.setConcurrency(2);
		return factory;
	}

}
