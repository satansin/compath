package com.satansin.android.compath.logic;

public interface PersonalSettingsService {

	public boolean setMyCity(String province, String city) throws NetworkTimeoutException, UnknownErrorException;

}
