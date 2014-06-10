package com.satansin.android.compath.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MygroupsServiceSocketImpl implements MygroupsService {

	public List<Group> getMygroupsList1() {
		ArrayList<Group> resultList = new ArrayList<Group>();
		resultList.add(new Group(4, "怎么去附近的景点", Calendar.getInstance(), "S先生",
				3, "五台山"));
		return resultList;
	}

	public List<Group> getMygroupsList() throws NetworkTimeoutException,
			UnknownErrorException {
		ArrayList<Group> resultList = new ArrayList<Group>();
		String msg = SocketMessageAnalyzer
				.getSendingMsg(SocketMessageAnalyzer.ASK_FOR_MYGROUPS);
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.MYGROUPS) {
			String[] contents = SocketMessageAnalyzer.getMsgArrayContents(
					result, "mygroups");
			for (String content : contents) {
				resultList.add((Group) SocketMessageAnalyzer
						.getBeanFromSocketMessage(content));
			}
		} else {
			throw new UnknownErrorException();
		}

		return resultList;
	}

	@Override
	public boolean addToMygroups(int groupId) throws NetworkTimeoutException, UnknownErrorException {
		boolean added = false;
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_ADDING_TO_MYGROUPS,
				new String[] { "group_id" },
				new String[] { String.valueOf(groupId) });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 3000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.ADDED_TO_MYGROUPS) {
			String content = SocketMessageAnalyzer.getMsgContent(result, "added");
			if (content.equals("true")) {
				added = true;
			} // TODO error handling
		} else {
			throw new UnknownErrorException();
		}

		return added;
	}

}
