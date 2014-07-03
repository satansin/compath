package com.satansin.android.compath.logic;

import java.util.List;

public interface GroupPicService {
	
	public List<String> getGroupPics(int groupId) throws NetworkTimeoutException, UnknownErrorException;

}
