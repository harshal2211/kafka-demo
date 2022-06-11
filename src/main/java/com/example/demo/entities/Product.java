package com.example.demo.entities;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Builder;

@Entity
@Builder
public class Product {

	@Id
	private String productId;
	
	private String productName;
	
	private float initialPrice;
	
	@OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
	private Set<Bid> bids;

	public Product() {
		super();
	}
	
	public Product(String productId, String productName, float initialPrice, Set<Bid> bids) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.initialPrice = initialPrice;
		this.bids = bids;
	}
	

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public float getInitialPrice() {
		return initialPrice;
	}

	public void setInitialPrice(float initialPrice) {
		this.initialPrice = initialPrice;
	}

	public Set<Bid> getBids() {
		return bids;
	}

	public void setBids(Set<Bid> bids) {
		this.bids = bids;
	}
	
}
