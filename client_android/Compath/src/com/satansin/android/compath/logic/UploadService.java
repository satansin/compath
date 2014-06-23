package com.satansin.android.compath.logic;

public interface UploadService {
	
	// TODO “Ï≥£ª˘¿‡
	public String iconUploadToken(String session) throws UnknownErrorException, NotLoginException, NetworkTimeoutException;
	
	public boolean iconUpdate(String session, String url) throws UnknownErrorException, NotLoginException, NetworkTimeoutException;
	
	public String photoUploadToken(String session, int locationId) throws UnknownErrorException, NotLoginException, NetworkTimeoutException;
	
	public boolean photoUpdate(String session, int locationId, String url) throws UnknownErrorException, NotLoginException, NetworkTimeoutException;

}
