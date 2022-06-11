package com.example.demo.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Builder;

@Entity
@Builder
public class Bid {
	@Id
	private String bidId;
	
	private float bidAmount;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "productId")
	private Product product;

	public Bid() {
		super();
	}
	
	public Bid(String bidId, float bidAmount, Product product) {
		super();
		this.bidId = bidId;
		this.bidAmount = bidAmount;
		this.product = product;
	}
	
	public String getBidId() {
		return bidId;
	}

	public void setBidId(String bidId) {
		this.bidId = bidId;
	}

	public float getBidAmount() {
		return bidAmount;
	}

	public void setBidAmount(float bidAmount) {
		this.bidAmount = bidAmount;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}	

}
