package com.satansin.android.compath.logic;

public class LoginServiceSocketImpl implements LoginService {
	
	private boolean firstLogin = false;
	
	public String authenticate(String usrname, String password) {
		return "iIDnfs766dsD";
	}

	public String authenticate1(String usrname, String password) throws NetworkTimeoutException, UnknownErrorException {
		String session = "";
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_SESSION,
				new String[] {"usrname", "password"},
				new String[] {usrname, password});
		
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 8000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}
		
		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.SESSION) {
			session = SocketMessageAnalyzer.getMsgContent(result, "session");
			firstLogin = Boolean.parseBoolean(SocketMessageAnalyzer.getMsgContent(result, "first_login"));
		} else {
			throw new UnknownErrorException();
		}
		
		return session;
	}

	@Override
	public boolean isFirstLogin() {
		return firstLogin;
	}

}
