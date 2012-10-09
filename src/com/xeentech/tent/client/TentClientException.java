package com.xeentech.tent.client;

@SuppressWarnings("serial")
public class TentClientException extends Exception {
	public TentClientException (String message, Throwable cause) {
		super(message, cause);
	}
	public TentClientException (String message) {
		super(message);
	}
}
