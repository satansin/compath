package com.satansin.android.compath.socket;

import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.UnknownErrorException;
import com.satansin.android.compath.logic.UploadService;

public class UploadServiceSocketImpl implements UploadService {

	// TODO upload不需要传其他参数
	@Override
	public String iconUploadToken(String session) throws UnknownErrorException, NetworkTimeoutException, NotLoginException {
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_UPLOAD_TOKEN);
		msg.putInt(SocketMsg.PARAM_ACTION, SocketMsg.ACTION_ICON);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		return getToken(msg);
	}

	@Override
	public String photoUploadToken(String session, int groupId) throws UnknownErrorException, NetworkTimeoutException, NotLoginException {
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_UPLOAD_TOKEN);
		msg.putInt(SocketMsg.PARAM_ACTION, SocketMsg.ACTION_PHOTO);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		msg.putInt(SocketMsg.PARAM_GROUP_ID, groupId);
		return getToken(msg);
	}
	
	private String getToken(SocketMsg msg) throws UnknownErrorException, NetworkTimeoutException, NotLoginException {
		String token = "";
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);
		
		if (result == null) {
			throw new NetworkTimeoutException();
		}
		
		if (result.getMsgType() == SocketMsg.RE_UPLOAD_TOKEN) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			token = result.getStringMsgContent(SocketMsg.PARAM_TOKEN);
		} else {
			throw new UnknownErrorException();
		}
		
		return token;
	}

	@Override
	public boolean iconUpdate(String session, String url)
			throws UnknownErrorException, NotLoginException,
			NetworkTimeoutException {
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_IMAGE_UPDATE);
		msg.putInt(SocketMsg.PARAM_ACTION, SocketMsg.ACTION_ICON);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		msg.putString(SocketMsg.PARAM_URL, url);
		return imageUpdate(msg);
	}

	@Override
	public boolean photoUpdate(String session, int groupId, String url)
			throws UnknownErrorException, NotLoginException,
			NetworkTimeoutException {
		SocketMsg msg = new SocketMsg(SocketMsg.ASK_FOR_IMAGE_UPDATE);
		msg.putInt(SocketMsg.PARAM_ACTION, SocketMsg.ACTION_PHOTO);
		msg.putString(SocketMsg.PARAM_SESSION, session);
		msg.putString(SocketMsg.PARAM_URL, url);
		msg.putInt(SocketMsg.PARAM_GROUP_ID, groupId);
		return imageUpdate(msg);
	}
	
	private boolean imageUpdate(SocketMsg msg)
			throws UnknownErrorException, NetworkTimeoutException,
			NotLoginException {
		boolean updated = false;
		
		SocketConnector connector = new SocketConnector();
		SocketMsg result = connector.send(msg, 8000);

		if (result == null) {
			throw new NetworkTimeoutException();
		}

		if (result.getMsgType() == SocketMsg.RE_IMAGE_UPDATED) {
			int error = result.getMsgError();
			switch (error) {
			case SocketMsg.ERROR_NOT_LOGIN:
				throw new NotLoginException();
			case SocketMsg.ERROR_UNKNOWN:
				throw new UnknownErrorException();
			default:
				break;
			}
			
			updated = result.getBoolMsgContent(SocketMsg.PARAM_IMAGE_UPDATED);
		} else {
			throw new UnknownErrorException();
		}
		
		return updated;
	}

}
