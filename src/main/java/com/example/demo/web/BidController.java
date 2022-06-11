package com.example.demo.web;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Bid;
import com.example.demo.exception.BidException;
import com.example.demo.models.CreateBidRequest;
import com.example.demo.models.ResponseBase;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/bid")
@Slf4j
public class BidController {

	@Autowired
	ReplyingKafkaTemplate<String, CreateBidRequest, ResponseBase<?>> kafkaTemplate;

	@Value("${kafka.topic.request-topic}")
	String requestTopic;

	@Value("${kafka.topic.requestreply-topic}")
	String requestReplyTopic;

	@Autowired
	ObjectMapper mapper;
	
	@PostMapping()
	public Bid placeBid(@RequestBody CreateBidRequest bidRequest) throws BidException, InterruptedException, ExecutionException {

		// create producer record
		ProducerRecord<String, CreateBidRequest> record = new ProducerRecord<String, CreateBidRequest>(requestTopic, bidRequest);
		
		// set reply topic in header
		record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
		
		// post in kafka topic
		RequestReplyFuture<String, CreateBidRequest, ResponseBase<?>> sendAndReceive = kafkaTemplate.sendAndReceive(record);
		
		//sendAndReceive.addCallback(
        //         result -> {
        //             log.info("callback result: {}", result);
        //         },
        //         ex -> {
        //             log.info("callback ex: {}", ex.getMessage());
        //         }
        // );

		// confirm if producer produced successfully
		SendResult<String, CreateBidRequest> sendResult = sendAndReceive.getSendFuture().get();

		// print all headers
		sendResult.getProducerRecord().headers().forEach(header -> log.info("{}:{}", header.key(), header.value()));

		// get consumer record
		ConsumerRecord<String, ResponseBase<?>> consumerRecord = sendAndReceive.get();
		
		consumerRecord.headers().forEach(header -> log.info("{}:{}", header.key(), header.value()));
		
		// return consumer value
		ResponseBase<?> responseBase = consumerRecord.value();
		
		if(!Objects.nonNull(responseBase.getErrorCode())) {
			Bid bid = mapper.convertValue(responseBase.getResponse(), Bid.class);
			return bid;
		}
		
		throw new BidException(responseBase.getErrorCode());
	}
}
