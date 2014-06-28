package com.satansin.android.compath.logic;

import java.util.List;

public interface LocationService {

	public Location getLocationByPoint(int latitude, int longitude)
			throws NetworkTimeoutException, UnknownErrorException,
			NonLocationException;

	public int getNewCreatedLocationId();

	public boolean createLocation(String locationName, int latitude,
			int longitude, int cityId, String session)
			throws UnknownErrorException, NetworkTimeoutException,
			NotLoginException;

	public List<Location> getLocationsByPoint(int latitude, int longitude)
			throws UnknownErrorException, NetworkTimeoutException,
			NonLocationException;

}
