package com.satansin.android.compath.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FeedServiceSocketImpl implements FeedService {

	private ArrayList<Group> resultList;

	public List<Group> getGroupListByLocationId1(String locationId) {
		resultList = new ArrayList<Group>();
		resultList.add(new Group(1, "��̨ɽ�������ݳ���", Calendar.getInstance(),
				"Absolute today", 24, "��̨ɽ"));
		resultList.add(new Group(2, "�ȷ����ͼ���", Calendar.getInstance(), "��С����",
				21, "��̨ɽ"));
		resultList.add(new Group(3, "��ߵĸ�У���= =", Calendar.getInstance(),
				"S����", 11, "��̨ɽ"));
		resultList.add(new Group(4, "��ôȥ�����ľ���", Calendar.getInstance(), "S����",
				3, "��̨ɽ"));
		return resultList;
	}

	public List<Group> getGroupListByLocationId(String locationId) throws NetworkTimeoutException, UnknownErrorException {
		resultList = new ArrayList<Group>();
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_FEED,
				new String[] { "location_id" }, new String[] { locationId });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 8000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.FEED) {
			String[] contents = SocketMessageAnalyzer.getMsgArrayContents(
					result, "feeds");
			for (String content : contents) {
				resultList.add((Group) SocketMessageAnalyzer
						.getBeanFromSocketMessage(content));
			}
		} else {
			throw new UnknownErrorException();
		}

		return resultList;
	}

}
