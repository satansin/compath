package com.satansin.android.compath.logic;

public interface RegisterService {

	public boolean register(String usrname, String password) throws NetworkTimeoutException, UnknownErrorException;

}
