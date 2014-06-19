package com.satansin.android.compath.socket;

import java.util.ArrayList;
import java.util.List;

import com.satansin.android.compath.logic.Group;
import com.satansin.android.compath.logic.MygroupsService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.UnknownErrorException;

public class MygroupsServiceSocketImpl implements MygroupsService {

	public List<Group> getMygroupsList(String session) throws NetworkTimeoutException,
			UnknownErrorException, NotLoginException {
		ArrayList<Group> resultList = new ArrayList<Group>();
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_MYGROUPS);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_MYGROUPS) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			SocketMsg[] contents = result.getArrayMsgContents(SocketMsg.PARAM_GROUPS);
			for (SocketMsg content : contents) {
				resultList.add((Group) content.getBeanFromSocketMessage(SocketMsg.BEAN_GROUP));
			}
		} else {
			throw new UnknownErrorException();
		}

		return resultList;
	}

	@Override
	public List<Group> getMyFavoriteList(String session) throws NetworkTimeoutException,
			UnknownErrorException, NotLoginException {
		ArrayList<Group> resultList = new ArrayList<Group>();
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_FAVORITE_GROUPS);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);

		if (result.getMsgType() == SocketMsg.RE_FAVORITE_GROUPS) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			SocketMsg[] contents = result.getArrayMsgContents(SocketMsg.PARAM_GROUPS);
			for (SocketMsg content : contents) {
				resultList.add((Group) content.getBeanFromSocketMessage(SocketMsg.BEAN_GROUP));
			}
		} else {
			throw new UnknownErrorException();
		}

		return resultList;
	}

	@Override
	public boolean favorGroup(int groupId, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException {
		boolean added = false;
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_FAVORITE_GROUPS);
		msg.putInt(SocketMsg.PARAM_GROUP_ID, groupId);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 3000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_GROUP_FAVORED) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			added = result.getBoolMsgContent(SocketMsg.PARAM_FAVORED);
		} else {
			throw new UnknownErrorException();
		}

		return added;
	}

	@Override
	public boolean removeFromFavor(int groupId, String session) throws NetworkTimeoutException,
			UnknownErrorException, NotLoginException {
		boolean removed = false;
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_GROUP_FAVOR_REMOVING);
		msg.putInt(SocketMsg.PARAM_GROUP_ID, groupId);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 3000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_FAVOR_GROUP_REMOVED) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			removed = result.getBoolMsgContent(SocketMsg.PARAM_REMOVED);
		} else {
			throw new UnknownErrorException();
		}

		return removed;
	}

	@Override
	public boolean getGroupFavorStatus(int groupId, String session) throws NetworkTimeoutException,
			UnknownErrorException, NotLoginException {
		boolean hasFavored = false;
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_GROUP_FAVOR_STATUS);
		msg.putInt(SocketMsg.PARAM_GROUP_ID, groupId);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 5000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_GROUP_FAVOR_STATE) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			hasFavored = result.getBoolMsgContent(SocketMsg.PARAM_HAS_FAVORED);
		} else {
			throw new UnknownErrorException();
		}

		return hasFavored;
	}

}
