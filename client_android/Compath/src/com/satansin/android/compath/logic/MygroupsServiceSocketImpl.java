package com.satansin.android.compath.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MygroupsServiceSocketImpl implements MygroupsService {

	public List<Group> getMygroupsList1() {
		ArrayList<Group> resultList = new ArrayList<Group>();
		resultList.add(new Group(4, "怎么去附近的景点", Calendar.getInstance().getTimeInMillis(), "S先生",
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
						.getBeanFromSocketMessage(content, SocketMessageAnalyzer.BEAN_GROUP));
			}
		} else {
			throw new UnknownErrorException();
		}

		return resultList;
	}

	@Override
	public List<Group> getMyFavoriteList() throws NetworkTimeoutException,
			UnknownErrorException {
		ArrayList<Group> resultList = new ArrayList<Group>();
		String msg = SocketMessageAnalyzer
				.getSendingMsg(SocketMessageAnalyzer.ASK_FOR_FAVORITE_GROUPS);
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.FAVORITE_GROUPS) {
			String[] contents = SocketMessageAnalyzer.getMsgArrayContents(
					result, "favorite_groups");
			for (String content : contents) {
				resultList.add((Group) SocketMessageAnalyzer
						.getBeanFromSocketMessage(content, SocketMessageAnalyzer.BEAN_GROUP));
			}
		} else {
			throw new UnknownErrorException();
		}

		return resultList;
	}

	@Override
	public boolean favorGroup(int groupId) throws NetworkTimeoutException, UnknownErrorException {
		boolean added = false;
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_GROUP_FAVORING,
				new String[] { "group_id" },
				new String[] { String.valueOf(groupId) });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 3000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.GROUP_FAVORED) {
			String content = SocketMessageAnalyzer.getMsgContent(result, "favored");
			if (content.equals("true")) {
				added = true;
			} // TODO error handling
		} else {
			throw new UnknownErrorException();
		}

		return added;
	}

	@Override
	public boolean removeFromFavor(int groupId) throws NetworkTimeoutException,
			UnknownErrorException {
		boolean removed = false;
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_GROUP_FAVOR_REMOVING,
				new String[] { "group_id" },
				new String[] { String.valueOf(groupId) });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 3000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.FAVOR_GROUP_REMOVED) {
			String content = SocketMessageAnalyzer.getMsgContent(result, "removed");
			if (content.equals("true")) {
				removed = true;
			} // TODO error handling
		} else {
			throw new UnknownErrorException();
		}

		return removed;
	}

	@Override
	public boolean getGroupFavorStatus(int groupId) throws NetworkTimeoutException,
			UnknownErrorException {
		boolean hasFavored = false;
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_GROUP_FAVOR_STATUS,
				new String[] { "group_id" },
				new String[] { String.valueOf(groupId) });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 3000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.GROUP_FAVOR_STATE) {
			String content = SocketMessageAnalyzer.getMsgContent(result, "has_favored");
			if (content.equals("true")) {
				hasFavored = true;
			} // TODO error handling
		} else {
			throw new UnknownErrorException();
		}

		return hasFavored;
	}

}
