package com.satansin.android.compath.logic;

public interface GroupParticipationService {

	public boolean enter(int groupId, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;

	public boolean exit(int groupId, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;

}
