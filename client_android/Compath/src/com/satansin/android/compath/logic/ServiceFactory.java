package com.satansin.android.compath.logic;

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

	public static MemoryService getMemoryService() {
		return new MemoryServiceSQLiteImpl();
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

}
