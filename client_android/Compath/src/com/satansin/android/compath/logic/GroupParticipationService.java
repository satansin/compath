package com.satansin.android.compath.logic;

public interface GroupParticipationService {

	public boolean enter(int groupId) throws NetworkTimeoutException, UnknownErrorException;

	public boolean exit(int groupId) throws NetworkTimeoutException, UnknownErrorException;

}
