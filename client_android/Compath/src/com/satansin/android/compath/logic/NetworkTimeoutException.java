package com.satansin.android.compath.logic;

public class NetworkTimeoutException extends Exception {

	private static final long serialVersionUID = 2995956177823575563L;
	
	public NetworkTimeoutException() {
	}
	
	public NetworkTimeoutException(String detailMessage) {
		super(detailMessage);
	}

}
