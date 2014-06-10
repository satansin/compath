package com.satansin.android.compath.logic;

public interface GroupCreationService {

	public boolean createGroup(String groupTitle, String locationId) throws NetworkTimeoutException, UnknownErrorException;

	public int getNewCreatedGroupId();

}
