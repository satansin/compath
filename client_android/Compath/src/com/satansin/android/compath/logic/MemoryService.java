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
	 * ���ļ��ж�ȡ��ǰ�û�ָ��Ⱥ�����ʷ��Ϣ������ʹ��startIndex��endIndex�������޶�������Ϣ�ķ�Χ��
	 * ���ڷֶμ�����Ϣ����Ŵ�1��ʼ�����ص�����������endIndex-startIndex+1��
	 * @param groupId ��ʷ��Ϣ��Ⱥ��id
	 * @param startIndex ��ʼ���ص���Ϣ���
	 * @param endIndex ���һ�����ص���Ϣ���
	 * @return ��Ϣ�б�����Ϣʱ�併�����У����뵽����adapterʱ�轫˳��ת
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
