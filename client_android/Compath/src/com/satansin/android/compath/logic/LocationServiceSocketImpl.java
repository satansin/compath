package com.satansin.android.compath.logic;

public class LocationServiceSocketImpl implements LocationService {

	private Location location = new Location();

	public Location getLocationByPoint1(int latitude, int longitude) {
		location = new Location("53647", "ÎåÌ¨É½", (int) (32.056774 * 1e6),
				(int) (118.780659 * 1e6));
		return location;
	}

	public Location getLocationByPoint(int latitude, int longitude) throws NetworkTimeoutException, UnknownErrorException {
		String msg = SocketMessageAnalyzer.getSendingMsg(
				SocketMessageAnalyzer.ASK_FOR_LOCATION,
				new String[] { "latitude", "longitude" },
				new String[] { String.valueOf(latitude),
						String.valueOf(longitude) });
		SocketConnector connector = new SocketConnector();
		String result = connector.send(msg, 8000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (SocketMessageAnalyzer.getMsgType(result) == SocketMessageAnalyzer.LOCATION) {
			location = (Location) SocketMessageAnalyzer
					.getBeanFromSocketMessage(SocketMessageAnalyzer
							.getMsgContent(result, "location"), SocketMessageAnalyzer.BEAN_LOCATION);
		} else {
			throw new UnknownErrorException();
		}
		
		return location;
	}

}
