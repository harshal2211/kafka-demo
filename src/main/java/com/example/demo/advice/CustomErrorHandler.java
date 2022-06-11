package com.example.demo.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.exception.BidException;
import com.example.demo.exception.ErrorCode;

@ControllerAdvice
public class CustomErrorHandler {

	@ExceptionHandler({BidException.class})
	public ResponseEntity<String> handleBidException(BidException bidException) {
		ErrorCode errorCode = bidException.getErrorCode();
		return new ResponseEntity<>(errorCode.getErrorMessage(), errorCode.getHttpStatus());
	} 
	
	@ExceptionHandler({Exception.class})
	public ResponseEntity<String> handleException(Exception bidException) {
		return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
