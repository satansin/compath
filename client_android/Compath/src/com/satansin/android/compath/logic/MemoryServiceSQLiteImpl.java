package com.satansin.android.compath.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MemoryServiceSQLiteImpl implements MemoryService {

	@Override
	public boolean writeSession(String usrname, String session) {
		return true;
	}

	@Override
	public List<Message> loadHistoryMessage(int groupId) {
		return new ArrayList<Message>();
	}

	@Override
	public Message insertSendingMessage(String text) {
		return new Message(0, text, Calendar.getInstance(), false, "Mr");
	}

	@Override
	public void insertMessage(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getMyUsrname() {
		// TODO Auto-generated method stub
		return "Mr";
	}

	@Override
	public String getMyCity() {
		// TODO Auto-generated method stub
		return "�Ͼ�";
	}

	@Override
	public String getMySession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getCityNamesByProvinceName(String provinceName) {
		// TODO Auto-generated method stub
		return new String[]{ "�Ͼ�", "����" , "����", "����", "��", "̩��", "��ͨ", "�γ�", "����", "����", "���Ƹ�", "��Ǩ" };
	}

}
