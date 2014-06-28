package com.satansin.android.compath.logic;

import java.util.List;

import android.graphics.Bitmap;
import android.net.Uri;

public interface MemoryService {
	
	public static final int IMG_ORIGIN = 1;
	public static final int IMG_THUMB_H = 2;
	public static final int IMG_THUMB_L = 3;
	public static final int IMG_ALBUM = 4;
	
	public static final int MAX_HISTORY_MSG_COUNT = 10000;

	public boolean writeSession(String usrname, String session, String iconUrl) throws UnknownErrorException;
	
	public boolean updateUsrIcon(String url) throws UnknownErrorException;
	
	public void clearSession() throws UnknownErrorException;

	/**
	 * 从文件中读取当前用户指定群组的历史消息，并且使用startIndex和endIndex参数来限定加载消息的范围，
	 * 用于分段加载消息，序号从1开始，加载的条数不多于endIndex-startIndex+1。
	 * @param groupId 历史消息的群组id
	 * @param startIndex 开始加载的消息序号
	 * @param endIndex 最后一条加载的消息序号
	 * @return 消息列表，按消息时间降序排列，导入到界面adapter时需将顺序反转
	 * @throws UnknownErrorException 
	 */
	public List<Message> loadHistoryMessage(int groupId, int startIndex, int endIndex) throws UnknownErrorException;
	
	@Deprecated
	public boolean saveHistoryMessage(int groupId);

	public Message insertSendingMessage(String text, int groupId) throws UnknownErrorException;

	public Message insertReceivedMessage(Message message, int groupId) throws UnknownErrorException;

	public String getMyUsrname();

	public String getMySession();

	public City[] getCitiesByProvinceName(String string) throws UnknownErrorException;

	public String getCityName(int cityId) throws UnknownErrorException;

	public int getCityIdByName(String cityName) throws UnknownErrorException;
	
	public Bitmap getLocalImage(String fileName, int qualityCode) throws UnknownErrorException;
	
	public Uri putLocalImage(Bitmap bitmap, String fileName, int qualityCode) throws UnknownErrorException;

	public Uri getNewCapturingUri(String fileName) throws UnknownErrorException;

}
