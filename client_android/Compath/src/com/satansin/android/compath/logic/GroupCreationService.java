package com.satansin.android.compath.logic;

public interface GroupCreationService {

	public boolean createGroup(String groupTitle, int locationId, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;

	public int getNewCreatedGroupId();

}
