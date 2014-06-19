package com.satansin.android.compath.logic;

import java.util.List;

public interface MemoryService {

	public boolean writeSession(String usrname, String session);
	
	public void clearSession();

	public List<Message> loadHistoryMessage(int groupId);

	public Message insertMessage(String text, int groupId, boolean isComingMsg);

	public Message insertMessage(Message message, boolean isComingMsg);

	public String getMyUsrname();

	public String getMySession();

	public City[] getCitiesByProvinceName(String string);

	public String getCityName(int cityId);

	public int getCityIdByName(String cityName);

}
