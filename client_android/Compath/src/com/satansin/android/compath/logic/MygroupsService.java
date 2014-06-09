package com.satansin.android.compath.logic;

import java.util.List;

public interface MygroupsService {

	public List<Group> getMygroupsList() throws NetworkTimeoutException, UnknownErrorException;

	public boolean addToMygroups(int groupId) throws NetworkTimeoutException, UnknownErrorException;

}
