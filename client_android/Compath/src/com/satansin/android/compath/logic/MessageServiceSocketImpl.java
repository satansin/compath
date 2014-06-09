package com.satansin.android.compath.logic;

import java.util.ArrayList;

public class MessageServiceSocketImpl implements MessageService {

	public boolean sendMessage1(Message message) {
		return true;
	}

	public boolean sendMessage(Message messageToSent)
			throws NetworkTimeoutException, UnknownErrorException {
		boolean messageSent = false;
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_MESSAGE_SENDING, "message",
				messageToSent);
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 5000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.MESSAGE_SENT) {
			String content = SocketMessageAnalyzer.getMsgContent(result,
					"message_sent");
			if (content.equals("true")) {
				messageSent = true;
			}
		} else {
			throw new UnknownErrorException();
		}

		return messageSent;
	}

	public ArrayList<Message> receiveMessages1(int groupId) {
		return new ArrayList<Message>();
	}

	public ArrayList<Message> receiveMessages(int groupId) {
		ArrayList<Message> receivedMessages = new ArrayList<Message>();
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_MESSAGE_RECEIVING,
				new String[] { "group_id" },
				new String[] { String.valueOf(groupId) });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 500);
		
		if (result == null) {
			return receivedMessages;
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.MESSAGE_RECEIVED) {
			String[] contents = SocketMessageAnalyzer.getMsgArrayContents(
					result, "messages");
			for (String content : contents) {
				receivedMessages.add((Message) SocketMessageAnalyzer
						.getBeanFromSocketMessage(content));
			}
		}
		
		return receivedMessages;
	}

}
