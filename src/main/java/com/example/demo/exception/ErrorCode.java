package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	INTERNAL_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong"),
	INVALID_PRODUCT_ID(HttpStatus.INTERNAL_SERVER_ERROR, "product id is invalid no product found");
	
	private HttpStatus httpStatus;
	private String errorMessage;
	
	private ErrorCode(HttpStatus httpStatus, String errorMessage) {
		this.httpStatus = httpStatus;
		this.errorMessage = errorMessage;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
