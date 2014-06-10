package com.satansin.android.compath.logic;

public class GroupParticipationServiceSocketImpl implements
		GroupParticipationService {

	@Override
	public boolean enter(int groupId) throws NetworkTimeoutException, UnknownErrorException {
		boolean entered = false;
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_GROUP_ENTERING,
				new String[] { "group_id" },
				new String[] { String.valueOf(groupId) });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.GROUP_ENTERED) {
			String content = SocketMessageAnalyzer.getMsgContent(result, "entered");
			if (content.equals("true")) {
				entered = true;
			} // TODO error handling
		} else {
			throw new UnknownErrorException();
		}
		
		return entered;
	}

	@Override
	public boolean exit(int groupId) throws NetworkTimeoutException, UnknownErrorException {
		boolean exited = false;
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_GROUP_EXITING,
				new String[] { "group_id" },
				new String[] { String.valueOf(groupId) });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.GROUP_EXITED) {
			String content = SocketMessageAnalyzer.getMsgContent(result, "exited");
			if (content.equals("true")) {
				exited = true;
			} // TODO error handling
		} else {
			throw new UnknownErrorException();
		}
		
		return exited;
	}

}
