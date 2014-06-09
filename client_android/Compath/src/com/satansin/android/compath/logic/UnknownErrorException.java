package com.satansin.android.compath.logic;

public class UnknownErrorException extends Exception {

	private static final long serialVersionUID = -4602424614047320907L;
	
	public UnknownErrorException() {
	}
	
	public UnknownErrorException(String detailMessage) {
		super(detailMessage);
	}

}
