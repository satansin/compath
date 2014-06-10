package com.satansin.android.compath.logic;

import java.util.List;

public interface MemoryService {

	public boolean writeSession(String usrname, String session);

	public List<Message> loadHistoryMessage(int groupId);

	public Message insertMessage(String text, boolean isComingMsg);

	public Message insertMessage(Message message, boolean isComingMsg);

	public String getMyUsrname();

	public String getMyCity();

	public String getMySession();

	public String[] getCityNamesByProvinceName(String provinceName);

}
