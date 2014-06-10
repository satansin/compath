package com.satansin.android.compath.logic;

public class GroupCreationServiceSocketImpl implements GroupCreationService {
	
	private int newCreatedGroupId = 0;

	@Override
	public boolean createGroup(String groupTitle, String locationId)
			throws NetworkTimeoutException, UnknownErrorException {
		boolean created = false;
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_GROUP_CREATING,
				new String[] { "group_title", "location_id" },
				new String[] { groupTitle, locationId });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.GROUP_CREATED) {
			String content = SocketMessageAnalyzer.getMsgContent(result, "created");
			if (content.equals("true")) {
				created = true;
				newCreatedGroupId = Integer.parseInt(SocketMessageAnalyzer.getMsgContent(result, "new_group_id"));
			} // TODO error handling
		} else {
			throw new UnknownErrorException();
		}
		
		return created;
	}

	@Override
	public int getNewCreatedGroupId() {
		return newCreatedGroupId;
	}

}
