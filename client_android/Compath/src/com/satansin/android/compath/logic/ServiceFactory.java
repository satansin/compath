package com.satansin.android.compath.logic;

import android.content.Context;

import com.satansin.android.compath.file.MemoryServiceFileImpl;
import com.satansin.android.compath.qiniu.ImageServiceQiniuImpl;
import com.satansin.android.compath.socket.FeedServiceSocketImpl;
import com.satansin.android.compath.socket.GroupCreationServiceSocketImpl;
import com.satansin.android.compath.socket.GroupParticipationServiceSocketImpl;
import com.satansin.android.compath.socket.LocationServiceSocketImpl;
import com.satansin.android.compath.socket.LoginServiceSocketImpl;
import com.satansin.android.compath.socket.MessageServiceSocketImpl;
import com.satansin.android.compath.socket.MygroupsServiceSocketImpl;
import com.satansin.android.compath.socket.PersonalSettingsServiceSocketImpl;
import com.satansin.android.compath.socket.RegisterServiceSocketImpl;

public class ServiceFactory {

	public static FeedService getFeedService() {
		return new FeedServiceSocketImpl();
	}

	public static LocationService getLocationService() {
		return new LocationServiceSocketImpl();
	}

	public static MygroupsService getMygroupsService() {
		return new MygroupsServiceSocketImpl();
	}

	public static LoginService getLoginService() {
		return new LoginServiceSocketImpl();
	}

	public static RegisterService getRegisterService() {
		return new RegisterServiceSocketImpl();
	}

	public static MemoryService getMemoryService(Context context) {
		return MemoryServiceFileImpl.getInstance(context);
	}

	public static MessageService getMessageService() {
		return new MessageServiceSocketImpl();
	}

	public static PersonalSettingsService getPersonalSettingsService() {
		return new PersonalSettingsServiceSocketImpl();
	}

	public static GroupCreationService getGroupCreationService() {
		return new GroupCreationServiceSocketImpl();
	}

	public static GroupParticipationService getGroupParticipationService() {
		return new GroupParticipationServiceSocketImpl();
	}

	public static ImageService getImageService(Context context) {
		return new ImageServiceQiniuImpl(context);
	}

}
