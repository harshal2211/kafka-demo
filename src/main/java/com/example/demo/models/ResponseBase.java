package com.example.demo.models;

import com.example.demo.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@JsonInclude(value = Include.NON_EMPTY)
@Data
@Builder
public class ResponseBase<T> {
	private T response;
	
	private ErrorCode errorCode;

	public ResponseBase(T response, ErrorCode errorCode) {
		super();
		this.response = response;
		this.errorCode = errorCode;
	}

	public ResponseBase(T response) {
		this(response, null);
	}

	public ResponseBase() {
		super();
	}
	
	
}
