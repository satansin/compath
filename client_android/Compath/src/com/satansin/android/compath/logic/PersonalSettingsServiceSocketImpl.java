package com.satansin.android.compath.logic;

public class PersonalSettingsServiceSocketImpl implements
		PersonalSettingsService {

	@Override
	public boolean setMyCity(String province, String city) throws NetworkTimeoutException, UnknownErrorException {
		boolean citySet = false;
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_CITY_SETTING,
				new String[] { "province", "city" },
				new String[] { province, city });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.CITY_SET) {
			String content = SocketMessageAnalyzer.getMsgContent(result, "city_set");
			if (content.equals("true")) {
				citySet = true;
			} // TODO error handling
		} else {
			throw new UnknownErrorException();
		}
		
		return citySet;
	}

}
