package com.satansin.android.compath.socket;

import java.util.ArrayList;
import java.util.List;

import com.satansin.android.compath.logic.Location;
import com.satansin.android.compath.logic.LocationService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NonLocationException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.UnknownErrorException;

public class LocationServiceSocketImpl implements LocationService {
	
	private int newCreatedLocationId = 0;

	public Location getLocationByPoint(int latitude, int longitude) throws NetworkTimeoutException, UnknownErrorException, NonLocationException {
		Location location = new Location();
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_LOCATION);
		msg.putInt(SocketMsg.PARAM_LATITUDE, latitude);
		msg.putInt(SocketMsg.PARAM_LONGITUDE, longitude);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_LOCATION) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NON_LOC:
				throw new NonLocationException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			SocketMsg locationContent = result.getObjectMsgContent(SocketMsg.PARAM_LOCATION);
			if (locationContent == null) {
				throw new NonLocationException();
			}
			location = (Location) locationContent.getBeanFromSocketMessage(SocketMsg.BEAN_LOCATION);
			if (location.getId() <= 0) {
				throw new NonLocationException();
			}
		} else {
			throw new UnknownErrorException();
		}
		
		return location;
	}

	@Override
	public int getNewCreatedLocationId() {
		return newCreatedLocationId;
	}

	@Override
	public boolean createLocation(String locationName, int latitude,
			int longitude, int cityId, String session) throws UnknownErrorException, NetworkTimeoutException, NotLoginException {
		boolean created = false;
		
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_LOCATION_CREATION);
		msg.putString(SocketMsg.PARAM_LOCATION_NAME, locationName);
		msg.putInt(SocketMsg.PARAM_LATITUDE, latitude);
		msg.putInt(SocketMsg.PARAM_LONGITUDE, longitude);
		msg.putInt(SocketMsg.PARAM_CITY_ID, cityId);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_LOCATION_CREATED) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			created = result.getBoolMsgContent(SocketMsg.PARAM_CREATED);
			if (created) {
				newCreatedLocationId = result.getIntMsgContent(SocketMsg.PARAM_LOCATION_ID);
				if (newCreatedLocationId <= 0) {
					throw new UnknownErrorException();
				}
			}
		} else {
			throw new UnknownErrorException();
		}
		
		return created;
	}

	@Override
	public List<Location> getLocationsByPoint(int latitude, int longitude)
			throws UnknownErrorException, NetworkTimeoutException,
			NonLocationException {
		List<Location> locations = new ArrayList<Location>();
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_LOCATIONS);
		msg.putInt(SocketMsg.PARAM_LATITUDE, latitude);
		msg.putInt(SocketMsg.PARAM_LONGITUDE, longitude);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_LOCATIONS) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NON_LOC:
				throw new NonLocationException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			SocketMsg[] contents = result.getArrayMsgContents(SocketMsg.PARAM_LOCATIONS);
			for (SocketMsg content : contents) {
				locations.add((Location) content.getBeanFromSocketMessage(SocketMsg.BEAN_LOCATION));
			}
		} else {
			throw new UnknownErrorException();
		}
		
		return locations;
	}

}
