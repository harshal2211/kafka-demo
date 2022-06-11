package com.example.demo.exception;


public class BidException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1396792665626668197L;
	
	private ErrorCode errorCode;

	public BidException(ErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}	
	
	
}
