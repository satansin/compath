package com.satansin.android.compath.logic;

public class RegisterServiceSocketImpl implements RegisterService {

	public boolean register1(String usrname, String password) {
		return true;
	}

	public boolean register(String usrname, String password) throws NetworkTimeoutException, UnknownErrorException {
		boolean registered = false;
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_REGISTER,
				new String[] { "usrname", "password" },
				new String[] { String.valueOf(usrname),
						String.valueOf(password) });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.REGISTERED) {
			String content = SocketMessageAnalyzer.getMsgContent(result, "registered");
			if (content.equals("true")) {
				registered = true;
			} // TODO error handling
		} else {
			throw new UnknownErrorException();
		}
		
		return registered;
	}

}
