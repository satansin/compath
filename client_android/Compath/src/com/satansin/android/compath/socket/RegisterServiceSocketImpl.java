package com.satansin.android.compath.socket;

import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.RegisterService;
import com.satansin.android.compath.logic.UnknownErrorException;
import com.satansin.android.compath.util.PasswordEncryptor;

public class RegisterServiceSocketImpl implements RegisterService {

	public boolean register1(String usrname, String password) {
		return true;
	}

	public boolean register(String usrname, String password) throws NetworkTimeoutException, UnknownErrorException {
		boolean registered = false;
		
		password = PasswordEncryptor.getEncryptedPassword(password);
		if (password == null) {
			throw new UnknownErrorException();
		}
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_REGISTER);
		msg.putString(SocketMsg.PARAM_USRNAME, usrname);
		msg.putString(SocketMsg.PARAM_PASSWORD, password);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_REGISTERED) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_USRNAME_REPEATED:
				return false;
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			registered = result.getBoolMsgContent(SocketMsg.PARAM_REGISTERED);
		} else {
			throw new UnknownErrorException();
		}
		
		return registered;
	}

}
