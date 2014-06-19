package com.satansin.android.compath.logic;

import java.util.ArrayList;

public interface MessageService {

	public boolean sendMessage(Message message, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException;

	public ArrayList<Message> receiveMessages(int groupId, String session) throws UnknownErrorException, NotLoginException;

}
