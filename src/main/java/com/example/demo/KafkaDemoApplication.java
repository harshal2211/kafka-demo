package com.example.demo;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.entities.Bid;
import com.example.demo.entities.Product;
import com.example.demo.repositories.BidRepository;
import com.example.demo.repositories.ProductRepository;

@SpringBootApplication
public class KafkaDemoApplication implements CommandLineRunner{
	
	@Autowired
	ProductRepository productRepository;

	@Autowired
	BidRepository bidRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		productRepository.save(
				Product.builder().productId("a1").productName("ABC").initialPrice(47).build());
		productRepository.save(
				Product.builder().productId("a2").productName("DEF").initialPrice(32).build());
		productRepository.save(
				Product.builder().productId("a3").productName("GHI").initialPrice(78).build());
		
		//Product product = productRepository.findById("a1").orElseThrow(() -> new RuntimeException("product not found"));
		//Bid bid = Bid.builder().bidId(UUID.randomUUID().toString()).bidAmount(68).product(product).build();
		//bidRepository.save(bid);
	}

}
