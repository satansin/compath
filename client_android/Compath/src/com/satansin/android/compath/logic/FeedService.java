package com.satansin.android.compath.logic;

import java.util.List;

public interface FeedService {
	
	public List<Group> getFeedByLocationId(int locationId) throws NetworkTimeoutException, UnknownErrorException;

	public List<Group> getFeedByMycity(String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;

	public Location getUpdatedLocation();

}
