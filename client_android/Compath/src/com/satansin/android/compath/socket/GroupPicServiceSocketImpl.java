package com.satansin.android.compath.socket;

import java.util.ArrayList;
import java.util.List;

import com.satansin.android.compath.logic.GroupPicService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.UnknownErrorException;

public class GroupPicServiceSocketImpl implements GroupPicService {

	@Override
	public List<String> getGroupPics(int groupId) throws NetworkTimeoutException, UnknownErrorException {
		ArrayList<String> resultList = new ArrayList<String>();
		
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_GROUP_PICS);
		msg.putInt(SocketMsg.PARAM_GROUP_ID, groupId);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_GROUP_PICS) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			SocketMsg[] contents = result.getArrayMsgContents(SocketMsg.PARAM_URLS);
			for (SocketMsg content : contents) {
				resultList.add(content.getStringMsgContent(SocketMsg.PARAM_URL));
			}
		} else {
			throw new UnknownErrorException();
		}

		return resultList;
	}

}
