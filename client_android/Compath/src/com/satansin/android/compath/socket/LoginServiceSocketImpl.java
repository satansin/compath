package com.satansin.android.compath.socket;

import com.satansin.android.compath.logic.LoginService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.UnknownErrorException;
import com.satansin.android.compath.util.PasswordEncryptor;

public class LoginServiceSocketImpl implements LoginService {
	
	private boolean firstLogin = false;
	private String iconUrl = "";
	
	public String authenticate1(String usrname, String password) {
		return "iIDnfs766dsD";
	}

	public String authenticate(String usrname, String password) throws NetworkTimeoutException, UnknownErrorException {
		String session = "";
		
		password = PasswordEncryptor.getEncryptedPassword(password);
		if (password == null) {
			throw new UnknownErrorException();
		}
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_SESSION);
		msg.putString(SocketMsg.PARAM_USRNAME, usrname);
		msg.putString(SocketMsg.PARAM_PASSWORD, password);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}
		
		if (result.getMsgType() == SocketMsg.RE_SESSION) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_USRNAME_PASSWD_NOT_MATCHED:
				return "";
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			session = result.getStringMsgContent(SocketMsg.PARAM_SESSION);
			iconUrl = result.getStringMsgContent(SocketMsg.PARAM_URL);
			firstLogin = result.getBoolMsgContent(SocketMsg.PARAM_FIRST_LOGIN);
		} else {
			throw new UnknownErrorException();
		}
		
		return session;
	}

	@Override
	public boolean isFirstLogin() {
		return firstLogin;
	}
	
	@Override
	public String getIconUrl() {
		return iconUrl;
	}

	@Override
	public boolean logout(String session) throws NetworkTimeoutException,
			UnknownErrorException, NotLoginException {
		boolean loggedOut = false;
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_LOGOUT);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}
		
		if (result.getMsgType() == SocketMsg.RE_LOGGED_OUT) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			loggedOut = result.getBoolMsgContent(SocketMsg.PARAM_LOGGED_OUT);
		} else {
			throw new UnknownErrorException();
		}
		
		return loggedOut;
	}

}
