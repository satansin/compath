package com.satansin.android.compath.logic;

public interface PersonalSettingsService {

	public boolean setMyCity(City city, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;
	
	public int getMyCityId(String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;

}
