package com.satansin.android.compath.socket;

import java.util.ArrayList;
import java.util.List;

import com.satansin.android.compath.logic.City;
import com.satansin.android.compath.logic.FeedService;
import com.satansin.android.compath.logic.Group;
import com.satansin.android.compath.logic.Location;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.UnknownErrorException;

public class FeedServiceSocketImpl implements FeedService {
	
	private Location cityLocation = new Location();
	
	@Override
	public Location getUpdatedLocation() {
		return cityLocation;
	}

	@Override
	public List<Group> getFeedByLocationId(int locationId) throws NetworkTimeoutException, UnknownErrorException {
		ArrayList<Group> resultList = new ArrayList<Group>();
		
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_FEED);
		msg.putInt(SocketMsg.PARAM_LOCATION_ID, locationId);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_FEED) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			SocketMsg[] contents = result.getArrayMsgContents(SocketMsg.PARAM_GROUPS);
			for (SocketMsg content : contents) {
				resultList.add((Group) content.getBeanFromSocketMessage(SocketMsg.BEAN_GROUP));
			}
		} else {
			throw new UnknownErrorException();
		}

		return resultList;
	}

	@Override
	public List<Group> getFeedByMycity(String session)
			throws NetworkTimeoutException, UnknownErrorException, NotLoginException {
		ArrayList<Group> resultList = new ArrayList<Group>();
		
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_CITY_FEED);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_CITY_FEED) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			SocketMsg[] contents = result.getArrayMsgContents(SocketMsg.PARAM_GROUPS);
			for (SocketMsg content : contents) {
				resultList.add((Group) content.getBeanFromSocketMessage(SocketMsg.BEAN_GROUP));
			}
			
			SocketMsg cityContent = result.getObjectMsgContent(SocketMsg.PARAM_CITY);
			if (cityContent == null) {
				throw new UnknownErrorException();
			}
			City city = (City) cityContent.getBeanFromSocketMessage(SocketMsg.BEAN_CITY);
			if (city.getId() <= 0) {
				throw new UnknownErrorException();
			}
			cityLocation = new Location(0, city.getName(), city.getLatitude(), city.getLongitude());
		} else {
			throw new UnknownErrorException();
		}

		return resultList;
	}

}
