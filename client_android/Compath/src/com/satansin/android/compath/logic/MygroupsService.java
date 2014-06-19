package com.satansin.android.compath.logic;

import java.util.List;

public interface MygroupsService {

	public List<Group> getMygroupsList(String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;
	
	public List<Group> getMyFavoriteList(String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;

	public boolean favorGroup(int groupId, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;
	
	public boolean removeFromFavor(int groupId, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;

	public boolean getGroupFavorStatus(int groupId, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;

}
