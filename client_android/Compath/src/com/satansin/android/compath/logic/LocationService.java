package com.satansin.android.compath.logic;

public interface LocationService {

	public Location getLocationByPoint(int latitude, int longitude) throws NetworkTimeoutException, UnknownErrorException;

}
