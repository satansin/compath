package com.satansin.android.compath.logic;

import java.util.List;

public interface FeedService {
	
	public List<Group> getGroupListByLocationId(String locationId) throws NetworkTimeoutException, UnknownErrorException;

}
