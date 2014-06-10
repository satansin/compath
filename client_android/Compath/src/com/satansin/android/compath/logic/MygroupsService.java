package com.satansin.android.compath.logic;

import java.util.List;

public interface MygroupsService {

	public List<Group> getMygroupsList() throws NetworkTimeoutException, UnknownErrorException;
	
	public List<Group> getMyFavoriteList() throws NetworkTimeoutException, UnknownErrorException;

	public boolean favorGroup(int groupId) throws NetworkTimeoutException, UnknownErrorException;

}
