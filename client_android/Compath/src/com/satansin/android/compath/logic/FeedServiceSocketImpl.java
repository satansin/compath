package com.satansin.android.compath.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FeedServiceSocketImpl implements FeedService {

	private ArrayList<Group> resultList;

	public List<Group> getGroupListByLocationId1(String locationId) {
		resultList = new ArrayList<Group>();
		resultList.add(new Group(1, "五台山体育馆演唱会", Calendar.getInstance(),
				"Absolute today", 24, "五台山"));
		resultList.add(new Group(2, "先锋书店图书节", Calendar.getInstance(), "神烦小清新",
				21, "五台山"));
		resultList.add(new Group(3, "这边的高校真多= =", Calendar.getInstance(),
				"S先生", 11, "五台山"));
		resultList.add(new Group(4, "怎么去附近的景点", Calendar.getInstance(), "S先生",
				3, "五台山"));
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
