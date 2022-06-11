package com.example.demo.consumer;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import com.example.demo.entities.Bid;
import com.example.demo.entities.Product;
import com.example.demo.exception.BidException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.models.CreateBidRequest;
import com.example.demo.models.ResponseBase;
import com.example.demo.repositories.BidRepository;
import com.example.demo.repositories.ProductRepository;

@Component
public class ReplyingKafkaConsumer {

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	BidRepository bidRepository;

	//errorHandler = "listen3ErrorHandler"
	@KafkaListener(topics = "${kafka.topic.request-topic}", errorHandler = "listen3ErrorHandler")
	@SendTo
	public ResponseBase<Object> listen(CreateBidRequest request) throws BidException {
		Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new BidException(ErrorCode.INVALID_PRODUCT_ID));
		Bid bid = Bid.builder().bidId(UUID.randomUUID().toString()).bidAmount(request.getBidAmount()).product(product).build();
		bidRepository.save(bid);
		return ResponseBase.builder().response(bid).build();
	}
}
