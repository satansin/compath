package com.satansin.android.compath.socket;

import java.util.ArrayList;

import com.satansin.android.compath.logic.Message;
import com.satansin.android.compath.logic.MessageService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.UnknownErrorException;

public class MessageServiceSocketImpl implements MessageService {

	public boolean sendMessage1(Message message) {
		return true;
	}

	public boolean sendMessage(Message messageToSent, String session)
			throws NetworkTimeoutException, UnknownErrorException, NotLoginException {
		boolean messageSent = false;
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_MESSAGE_SENDING);
		msg.putString(SocketMsg.PARAM_MSG_CONTENT, messageToSent.getContent());
		msg.putLong(SocketMsg.PARAM_MSG_TIME, messageToSent.getTime());
		msg.putInt(SocketMsg.PARAM_GROUP_ID, messageToSent.getGroupId());
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 5000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_MESSAGE_SENT) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			messageSent = result.getBoolMsgContent(SocketMsg.PARAM_SENT);
		} else {
			throw new UnknownErrorException();
		}

		return messageSent;
	}

	public ArrayList<Message> receiveMessages1(int groupId) {
		return new ArrayList<Message>();
	}

	public ArrayList<Message> receiveMessages(int groupId, String session) throws UnknownErrorException, NotLoginException {
		ArrayList<Message> receivedMessages = new ArrayList<Message>();
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_MESSAGE_RECEIVING);
		msg.putInt(SocketMsg.PARAM_GROUP_ID, groupId);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 500);
		
		if (result == null) {
			return receivedMessages;
		}

		if (result.getMsgType() == SocketMsg.RE_MESSAGE_RECEIVED) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			SocketMsg[] contents = result.getArrayMsgContents(SocketMsg.PARAM_MESSAGES);
			for (SocketMsg content : contents) {
				receivedMessages.add((Message) content.getBeanFromSocketMessage(SocketMsg.BEAN_MESSAGE));
			}
		}
		
		return receivedMessages;
	}

}
