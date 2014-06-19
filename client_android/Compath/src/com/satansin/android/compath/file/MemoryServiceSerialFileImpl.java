package com.satansin.android.compath.file;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.satansin.android.compath.file.FileHelper.Session;
import com.satansin.android.compath.logic.City;
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.Message;

public class MemoryServiceSerialFileImpl implements MemoryService {
	
	private static MemoryServiceSerialFileImpl instance;
	
//	private int messageAutoIncreasedId = 0;
	
	private Context context;
	
	private Session currentSession;
	
	private MemoryServiceSerialFileImpl(Context context) {
		this.context = context;
		
		FileHelper helper = new FileHelper(context);
		currentSession = (Session) helper.readObjectFromFile(FileHelper.OBJECT_SESSION);
	}
	
	public static MemoryServiceSerialFileImpl getInstance(Context context) {
		if (instance == null) {
			instance = new MemoryServiceSerialFileImpl(context);
		}
		return instance;
	}

	@Override
	public void clearSession() {
		currentSession = null;
		FileHelper helper = new FileHelper(context);
		helper.deleteObject(FileHelper.OBJECT_SESSION);
	}

	@Override
	public boolean writeSession(String usrname, String session) {
		currentSession = new Session(usrname, session);
		FileHelper helper = new FileHelper(context);
		boolean sessionWritten = helper.writeObjectToFile(currentSession, FileHelper.OBJECT_SESSION);
		if (!sessionWritten) {
			currentSession = null;
		}
		return sessionWritten;
	}

	@Override
	public List<Message> loadHistoryMessage(int groupId) {
		// TODO Auto-generated method stub
		return new ArrayList<Message>();
	}

	@Override
	public Message insertMessage(String text, int groupId, boolean isComingMsg) {
//		Message message = new Message(0, text, Calendar.getInstance().getTimeInMillis(), isComingMsg, , groupId);
		return null;
	}

	@Override
	public Message insertMessage(Message message, boolean isComingMsg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMyUsrname() {
		if (currentSession != null) {
			return currentSession.usrname;
		} else {
			return "";
		}
	}

	@Override
	public String getMySession() {
		if (currentSession != null) {
			return currentSession.session;
		} else {
			return "";
		}
	}

	@Override
	public City[] getCitiesByProvinceName(String province) {
		FileHelper helper = new FileHelper(context);
		List<City> allCities = helper.readListFromFile(FileHelper.OBJECT_CITY_LIST);
		if (allCities == null) {
			return new City[0];
		}
		ArrayList<City> cityList = new ArrayList<City>();
		for (City city : allCities) {
			if (city.getProvince().equals(province)) {
				cityList.add(city);
			}
		}
		City[] array = new City[cityList.size()];
		for (int i = 0; i < cityList.size(); i++) {
			array[i] = cityList.get(i);
		}
		return array;
	}

	@Override
	public String getCityName(int cityId) {
		FileHelper helper = new FileHelper(context);
		List<City> allCities = helper.readListFromFile(FileHelper.OBJECT_CITY_LIST);
		if (allCities == null) {
			return "";
		}
		for (City city : allCities) {
			if (city.getId() == cityId) {
				return city.getName();
			}
		}
		return "";
	}

	@Override
	public int getCityIdByName(String cityName) {
		FileHelper helper = new FileHelper(context);
		List<City> allCities = helper.readListFromFile(FileHelper.OBJECT_CITY_LIST);
		if (allCities == null) {
			return 0;
		}
		for (City city : allCities) {
			if (cityName.contains(city.getName())) {
				return city.getId();
			}
		}
		return 0;
	}

}
