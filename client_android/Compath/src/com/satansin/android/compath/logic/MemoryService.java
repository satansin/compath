package com.satansin.android.compath.logic;

import java.util.List;

public interface MemoryService {

	public boolean writeSession(String usrname, String session);

	public List<Message> loadHistoryMessage(int groupId);

	public Message insertSendingMessage(String text);

	public void insertMessage(Message message);

	public String getMyUsrname();

	public String getMyCity();

	public String getMySession();

	public String[] getCityNamesByProvinceName(String provinceName);

}
