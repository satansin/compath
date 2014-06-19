package com.satansin.android.compath.socket;

import com.satansin.android.compath.logic.GroupCreationService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.UnknownErrorException;

public class GroupCreationServiceSocketImpl implements GroupCreationService {
	
	private int newCreatedGroupId = 0;

	@Override
	public boolean createGroup(String groupTitle, int locationId, String session)
			throws NetworkTimeoutException, UnknownErrorException, NotLoginException {
		boolean created = false;
		
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_GROUP_CREATING);
		msg.putString(SocketMsg.PARAM_GROUP_TITLE, groupTitle);
		msg.putInt(SocketMsg.PARAM_LOCATION_ID, locationId);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_GROUP_CREATED) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			created = result.getBoolMsgContent(SocketMsg.PARAM_CREATED);
			if (created) {
				newCreatedGroupId = result.getIntMsgContent(SocketMsg.PARAM_GROUP_ID);
				if (newCreatedGroupId <= 0) {
					throw new UnknownErrorException();
				}
			}
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
