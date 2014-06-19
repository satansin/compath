package com.satansin.android.compath.socket;

import com.satansin.android.compath.logic.GroupParticipationService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.UnknownErrorException;

public class GroupParticipationServiceSocketImpl implements
		GroupParticipationService {

	@Override
	public boolean enter(int groupId, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException {
		boolean entered = false;
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_GROUP_ENTERING);
		msg.putInt(SocketMsg.PARAM_GROUP_ID, groupId);
		msg.putString(SocketMsg.PARAM_SESSION, session);

		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_GROUP_ENTERED) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			entered = result.getBoolMsgContent(SocketMsg.PARAM_ENTERED);
		} else {
			throw new UnknownErrorException();
		}
		
		return entered;
	}

	@Override
	public boolean exit(int groupId, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException {
		boolean exited = false;
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_GROUP_EXITING);
		msg.putInt(SocketMsg.PARAM_GROUP_ID, groupId);
		msg.putString(SocketMsg.PARAM_SESSION, session);

		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_GROUP_EXITED) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			exited = result.getBoolMsgContent(SocketMsg.PARAM_EXITED);
		} else {
			throw new UnknownErrorException();
		}
		
		return exited;
	}

}
