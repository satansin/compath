package com.satansin.android.compath.file;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.satansin.android.compath.file.FileHelper.Session;
import com.satansin.android.compath.logic.City;
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.Message;
import com.satansin.android.compath.logic.UnknownErrorException;

public class MemoryServiceFileImpl implements MemoryService {
	
	private static MemoryServiceFileImpl instance;
	
	private Context context;
	
	private Session currentSession;
	
	private MemoryServiceFileImpl(Context context) {
		this.context = context;
		
		try {
			FileHelper helper = new FileHelper(context);
			helper.openInputSerialFile(FileHelper.OBJECT_SESSION);
			currentSession = (Session) helper.getObjectFromSerialFile();
			helper.closeSerialFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static MemoryServiceFileImpl getInstance(Context context) {
		if (instance == null) {
			instance = new MemoryServiceFileImpl(context);
		}
		return instance;
	}

	@Override
	public void clearSession() throws UnknownErrorException {
		currentSession = null;
		try {
			FileHelper helper = new FileHelper(context);
			helper.deleteObject(FileHelper.OBJECT_SESSION);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
	}

	@Override
	public boolean updateUsrIcon(String url) throws UnknownErrorException {
		if (currentSession == null) {
			return false;
		}
		
		currentSession.iconUrl = url;
		return writeSessionToFile();
	}

	@Override
	public boolean writeSession(String usrname, String session, String iconUrl) throws UnknownErrorException {
		currentSession = new Session(usrname, session, iconUrl);
		boolean sessionWritten = writeSessionToFile();
		if (!sessionWritten) {
			currentSession = null;
		}
		return sessionWritten;
	}
	
	private boolean writeSessionToFile() throws UnknownErrorException {
		boolean sessionWritten = false;
		try {
			FileHelper helper = new FileHelper(context);
			helper.openOutputSerialFile(FileHelper.OBJECT_SESSION);
			sessionWritten = helper.writeObjectToSerialFile(currentSession);
			helper.closeSerialFile();
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
		return sessionWritten;
	}

	@Override
	public List<Message> loadHistoryMessage(int groupId, int startIndex, int endIndex) throws UnknownErrorException {
		List<Message> list = new ArrayList<Message>();
		
		String usrname = getMyUsrname();
		if (usrname.length() <= 0) {
			return list;
		}
		
		if (startIndex < 1) {
			return list;
		}
		if (endIndex <= startIndex) {
			return list;
		}
		
		try {
			FileHelper helper = new FileHelper(context);
			helper.openUsrInputSerialFile(FileHelper.OBJECT_HISTORY_MESSAGES, usrname);
			int count = helper.getIntFromSerialFile();

			int selectedIndex = 0;
			for (int i = 0; i < count; i++) {
				Message message = (Message) helper.getObjectFromSerialFile();
				if (message.getGroupId() == groupId) {
					selectedIndex++;
					if (selectedIndex > endIndex) {
						break;
					}
					if (selectedIndex < startIndex) {
						continue;
					}
					list.add(message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
		return list;
	}
	
	private Message addToHistoryMessages(Message newMessage, String usrname) throws UnknownErrorException {
		try {
			FileHelper helper = new FileHelper(context);
			helper.openUsrInputSerialFile(FileHelper.OBJECT_HISTORY_MESSAGES, usrname);
			
			int count = helper.getIntFromSerialFile();
			ArrayList<Message> historyMsgList = new ArrayList<Message>();
			historyMsgList.add(newMessage);
			for (int i = 0; i < count && i < MAX_HISTORY_MSG_COUNT - 1; i++) {
				Message message = (Message) helper.getObjectFromSerialFile();
				historyMsgList.add(message);
			}
			
			if (historyMsgList.size() <= 1) {
				newMessage.setId(1);
			} else {
				newMessage.setId(historyMsgList.get(1).getId() + 1);
			}
			
			helper.closeSerialFile();
			
			helper.openUsrOutputSerialFile(FileHelper.OBJECT_HISTORY_MESSAGES, usrname);
			int newSize = historyMsgList.size();
			helper.writeObjectToSerialFile(newSize);
			for (int i = 0; i < historyMsgList.size(); i++) {
				helper.writeObjectToSerialFile(historyMsgList.get(i));
			}
			helper.closeSerialFile();
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
		
		return newMessage;
	}

	@Override
	public boolean setMessageSent(int messageId, boolean messageSent, String url)
			throws UnknownErrorException {
		boolean messageFound = false;
		try {
			FileHelper helper = new FileHelper(context);
			helper.openUsrInputSerialFile(FileHelper.OBJECT_HISTORY_MESSAGES, getMyUsrname());
			
			int count = helper.getIntFromSerialFile();
			ArrayList<Message> historyMsgList = new ArrayList<Message>();
			for (int i = 0; i < count; i++) {
				Message message = (Message) helper.getObjectFromSerialFile();
				if (message.getId() == messageId) {
					messageFound = true;
					if (messageSent) {
						message.setSendingState(Message.STATE_SENT);
						if (message.getType() == Message.TYPE_PIC) {
							message.setContent(url);
						}
					} else {
						message.setSendingState(Message.STATE_FAILED);
					}
				}
				historyMsgList.add(message);
			}
			
			helper.closeSerialFile();
			
			helper.openUsrOutputSerialFile(FileHelper.OBJECT_HISTORY_MESSAGES, getMyUsrname());
			helper.writeObjectToSerialFile(count);
			for (int i = 0; i < historyMsgList.size(); i++) {
				helper.writeObjectToSerialFile(historyMsgList.get(i));
			}
			helper.closeSerialFile();
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
		return messageFound;
	}
	
	@Override
	public Message insertSendingMessage(String text, int groupId, int type) throws UnknownErrorException {
		String usrname = getMyUsrname();
		if (usrname.length() <= 0) {
			return null;
		}
		
		Message newMessage = new Message(0, type, text, Calendar.getInstance().getTimeInMillis(), 
				false, getMyUsrname(), groupId, currentSession.iconUrl, Message.STATE_SENDING);
		
		newMessage = addToHistoryMessages(newMessage, usrname);
		return newMessage;
	}

	@Override
	public Message insertReceivedMessage(Message message, int groupId) throws UnknownErrorException {
		message.setComingMsg(true);
		message.setGroupId(groupId);
		String usrname = getMyUsrname();
		if (usrname.length() <= 0) {
			return null;
		}
		
		message = addToHistoryMessages(message, usrname);
		return message;
	}

	@Override
	public boolean saveHistoryMessage(int groupId) {
		return false;
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
	public City[] getCitiesByProvinceName(String province) throws UnknownErrorException {
		FileHelper helper = null;
		try {
			helper = new FileHelper(context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
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
	public String getCityName(int cityId) throws UnknownErrorException {
		FileHelper helper = null;
		try {
			helper = new FileHelper(context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
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
	public int getCityIdByName(String cityName) throws UnknownErrorException {
		FileHelper helper = null;
		try {
			helper = new FileHelper(context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
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

	@Override
	public Bitmap getLocalImage(String fileName, int qualityCode) throws UnknownErrorException {
		FileHelper helper = null;
		try {
			helper = new FileHelper(context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
		try {
			return helper.getLocalImage(fileName, qualityCode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
	}

	@Override
	public Uri putLocalImage(Bitmap bitmap, String fileName, int qualityCode) throws UnknownErrorException {
		FileHelper helper = null;
		try {
			helper = new FileHelper(context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
		try {
			return helper.putLocalImage(fileName, bitmap, qualityCode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
	}

	@Override
	public Uri getNewCapturingUri(String fileName) throws UnknownErrorException {
		FileHelper helper = null;
		try {
			helper = new FileHelper(context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
		try {
			return helper.getLocalImageUri(fileName, IMG_ALBUM);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownErrorException();
		}
	}

}
