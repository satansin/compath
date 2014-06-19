package com.satansin.android.compath.logic;

public interface LoginService {

	public String authenticate(String usrname, String password) throws NetworkTimeoutException, UnknownErrorException;

	public boolean isFirstLogin();

	public boolean logout(String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;;

}
