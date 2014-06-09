package com.satansin.android.compath.logic;

import java.util.ArrayList;

public interface MessageService {

	public boolean sendMessage(Message message) throws NetworkTimeoutException, UnknownErrorException;

	public ArrayList<Message> receiveMessages(int groupId);

}
