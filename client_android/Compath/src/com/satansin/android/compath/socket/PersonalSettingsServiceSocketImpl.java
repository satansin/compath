package com.satansin.android.compath.socket;

import com.satansin.android.compath.logic.City;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.PersonalSettingsService;
import com.satansin.android.compath.logic.UnknownErrorException;

public class PersonalSettingsServiceSocketImpl implements
		PersonalSettingsService {

	@Override
	public boolean setMyCity(City city, String session) throws NetworkTimeoutException, UnknownErrorException, NotLoginException {
		boolean citySet = false;
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_CITY_SETTING);
		msg.putInt(SocketMsg.PARAM_CITY_ID, city.getId());
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_CITY_SET) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			citySet = result.getBoolMsgContent(SocketMsg.PARAM_SET);
		} else {
			throw new UnknownErrorException();
		}
		
		return citySet;
	}

	@Override
	public int getMyCityId(String session) throws NetworkTimeoutException,
			UnknownErrorException, NotLoginException {
		int myCityId = 0;
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_MYCITY);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_MYCITY) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			myCityId = result.getIntMsgContent(SocketMsg.PARAM_CITY_ID);
			if (myCityId <= 0) {
				throw new UnknownErrorException();
			}
		} else {
			throw new UnknownErrorException();
		}
		
		return myCityId;
	}

	@Override
	public String getMyIconUrl(String session)
			throws NetworkTimeoutException, UnknownErrorException,
			NotLoginException {
		String url = "";
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_MYICON_URL);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_MYICON_URL) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			url = result.getStringMsgContent(SocketMsg.PARAM_URL);
		} else {
			throw new UnknownErrorException();
		}
		
		return url;
	}

}
