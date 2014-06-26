package com.satansin.android.compath.file;

public class StorageNotFoundException extends Exception {
	
	private static final long serialVersionUID = 5922299011520209876L;

	public StorageNotFoundException() {
	}
	
	public StorageNotFoundException(String detailMessage) {
		super(detailMessage);
	}

}
