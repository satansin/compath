package com.satansin.android.compath.socket;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.satansin.android.compath.logic.City;
import com.satansin.android.compath.logic.Group;
import com.satansin.android.compath.logic.Location;
import com.satansin.android.compath.logic.Message;

public class SocketMsg {
	
	// operation codes
	/**
	 * {"opcode":"101",
	 *  "usrname":"MrJin",
	 *  "password":"iIDns9sHD8dhs",
	 *  "session":不用判断}
	 */
	public static final int ASK_FOR_SESSION = 101;
	/**
	 * {"opcode":"102",
	 *  "location_id":"5362",
	 *  "session":不用判断}
	 */
	public static final int ASK_FOR_FEED = 102;
	/**
	 * {"opcode":"103",
	 *  "latitude":"118273632",
	 *  "longitude":"33283733",
	 *  "session":不用判断}
	 */
	public static final int ASK_FOR_LOCATION = 103;
	/**
	 * {"opcode":"104",
	 *  "content":"hi",
	 *  "time":"6348264376285(in millisecond)",
	 *  "group_id":"36293",
	 *  "session":"dfsfdif"(根据session读出发送者id作为消息的from值)}
	 */
	public static final int ASK_FOR_MESSAGE_SENDING = 104;
	/**
	 * r|743|djifsjoiods(根据session读出id，不接收from此id的消息)}
	 */
	public static final int ASK_FOR_MESSAGE_RECEIVING = 105;
	/**
	 * {"opcode":"106",
	 * 	"session":"dfisojf88d"}
	 */
	public static final int ASK_FOR_MYGROUPS = 106;
	/**
	 * {"opcode":"107",
	 * 	"group_id":"6352",
	 * 	"session":"dfisojf88d"}
	 */
	public static final int ASK_FOR_GROUP_FAVORING = 107;
	/**
	 * {"opcode":"108",
	 * 	"usrname":"doubi",
	 * 	"password":"jidjfHIHDSUIFS",
	 * 	"session":不用判断}
	 */
	public static final int ASK_FOR_REGISTER = 108;
	/**
	 * {"opcode":"109",
	 * 	"city_id":"1",
	 * 	"session":"djisfs"}
	 */
	public static final int ASK_FOR_CITY_SETTING = 109;
	/**
	 * {"opcode":"110",
	 * 	"group_title":"hey, look",
	 * 	"location_id":"36273",
	 * 	"session":"difhso"}
	 */
	public static final int ASK_FOR_GROUP_CREATING = 110;
	/**
	 * {"opcode":"111",
	 * 	"session":"dfisojf88d"}
	 */
	public static final int ASK_FOR_FAVORITE_GROUPS = 111;
	/**
	 * {"opcode":"112",
	 * 	"group_id":"6352",
	 * 	"session":"dfisojf88d"}
	 */
	public static final int ASK_FOR_GROUP_FAVOR_REMOVING = 112;
	/**
	 * {"opcode":"113",
	 * 	"group_id":"6352",
	 * 	"session":"dfisojf88d"}
	 */
	public static final int ASK_FOR_GROUP_ENTERING = 113;
	/**
	 * {"opcode":"114",
	 * 	"group_id":"6352",
	 * 	"session":"dfisojf88d"}
	 */
	public static final int ASK_FOR_GROUP_EXITING = 114;
	/**
	 * {"opcode":"115",
	 * 	"group_id":"6352",
	 * 	"session":"dfisojf88d"}
	 */
	public static final int ASK_FOR_GROUP_FAVOR_STATUS = 115;
	/**
	 * {"opcode":"116",
	 * 	"session":"djfiusfs"}
	 */
	public static final int ASK_FOR_CITY_FEED = 116;
	/**
	 * {"opcode":"117",
	 * 	"session":"dijfisf"}
	 */
	public static final int ASK_FOR_MYCITY = 117;
	/**
	 * {"opcode":"118",
	 * 	"session":"df8s9fhs"}
	 */
	public static final int ASK_FOR_LOGOUT = 118;
	/**
	 * {"opcode":"119",
	 * 	"location_name":"abc",
	 * 	"latitude":"32874332",
	 * 	"longitude":"118276437",
	 * 	"session":"fdhsufds"}
	 */
	public static final int ASK_FOR_LOCATION_CREATION = 119;
	/**
	 * {"opcode":"120",
	 * 	"session":"4"}
	 */
	public static final int ASK_FOR_UPLOAD_TOKEN = 120;
	/**
	 * {"opcode":"121",
	 * 	"action":"2",
	 * 	"session":"4",
	 * 	(optional)"location_id":"3623"}
	 */
	public static final int ASK_FOR_IMAGE_UPDATE = 121;
	/**
	 * {"opcode":"122",
	 * 	"session":"4"}
	 */
	public static final int ASK_FOR_MYICON_URL = 122;
	/**
	 * {"opcode":"123",
	 * 	"latitude":"32847263",
	 * 	"longitude":"118273643"}
	 */
	public static final int ASK_FOR_LOCATIONS = 123;
	/**
	 * {"opcode":"124",
	 * 	"group_id":"2321"}
	 */
	public static final int ASK_FOR_GROUP_PICS = 124;
	
	// return type codes
	/**
	 * {"type":"201",
	 * 	"session":"iIDns9sHD8dhs"/"",
	 * 	"first_login":"true/false",
	 * 	"error":"300/301"}
	 */
	public static final int RE_SESSION = 201;
	/**
	 * {"type":"202",
	 * 	"feeds":[
	 * 		{"id":"53723",
	 * 		 "title":"How to get there",
	 * 		 "last_active_time":"3623627342734(in millisecond)",
	 * 		 "owner_name":"MrJie",
	 * 		 "number_of_members":"53",
	 * 		 "location_name":"Street No 5"},
	 * 		...(没有feed数组长度0)],
	 * 	"location":{
	 * 		"id"...}
	 * 	"error":"300"}
	 */
	public static final int RE_FEED = 202;
	/**
	 * {"type":"203",
	 * 	"location":
	 * 		{"id":"3523",
	 * 		 "name":"Street No 8",
	 * 		 "latitude(纬度)":"38273823",
	 * 		 "longitude(经度)":"116838200"},
	 * 	"error":"300/302"}
	 */
	public static final int RE_LOCATION = 203;
	/**
	 * {"type":"204",
	 * 	"message_sent":"true/false"
	 * 	"error":"300/303"}
	 */
	public static final int RE_MESSAGE_SENT = 204;
	/**
	 * {"type":"205",
	 * 	"messages":[
	 * 		{"content":"hi",
	 * 		 "time":"37264736274",
	 * 		 "from":"MrJie"},
	 * 		...(没有message数组长度为0)],
	 * 	"error":"300/303"}
	 */
	public static final int RE_MESSAGE_RECEIVED = 205;
	/**
	 * {"type":"206",
	 * 	"mygroups":[
	 * 		{"id":"53723",
	 * 		 "title":"How to get there",
	 * 		 "last_active_time":"3623627342734(in millisecond)",
	 * 		 "owner_name":"MrJie",
	 * 		 "number_of_members":"53",
	 * 		 "location_name":"Street No 5"},
	 * 		...(没有的话数组长度0)],
	 * 	"error":"300/303"}
	 */
	public static final int RE_MYGROUPS = 206;
	/**
	 * {"type":"207",
	 * 	"favored":"true/false",
	 * 	"error":"300/303"}
	 */
	public static final int RE_GROUP_FAVORED = 207;
	/**
	 * {"type":"208",
	 * 	"registered":"true/false",
	 * 	"error":"300/304"}
	 */
	public static final int RE_REGISTERED = 208;
	/**
	 * {"type":"209",
	 * 	"city_set":"true/false",
	 * 	"error":"300/303"}
	 */
	public static final int RE_CITY_SET = 209;
	/**
	 * {"type":"210",
	 * 	"created":"true/false"
	 * 	"new_id":"36273",
	 * 	"error":"300/303"}
	 */
	public static final int RE_GROUP_CREATED = 210;
	/**
	 * {"type":"211",
	 * 	"favorite_groups":[
	 * 		{"id":"53723",
	 * 		 "title":"How to get there",
	 * 		 "last_active_time":"3623627342734(in millisecond)",
	 * 		 "owner_name":"MrJie",
	 * 		 "number_of_members":"53",
	 * 		 "location_name":"Street No 5"},
	 * 		...(没有的话数组长度0)],
	 * 	"error":"300/303"}
	 */
	public static final int RE_FAVORITE_GROUPS = 211;
	/**
	 * {"type":"212",
	 * 	"removed":"true/false",
	 * 	"error":"300/303"}
	 */
	public static final int RE_FAVOR_GROUP_REMOVED = 212;
	/**
	 * {"type":"213",
	 * 	"entered":"true/false",
	 * 	"error":"300/303"}
	 */
	public static final int RE_GROUP_ENTERED = 213;
	/**
	 * {"type":"214",
	 * 	"exited":"true/false",
	 * 	"error":"300/303"}
	 */
	public static final int RE_GROUP_EXITED = 214;
	/**
	 * {"type":"215",
	 * 	"has_favored":"true/false",
	 * 	"error":"300/303"}
	 */
	public static final int RE_GROUP_FAVOR_STATE = 215;
	/**
	 * {"type":"216",
	 * 	"feeds":[
	 * 		{"id":"53723",
	 * 		 "title":"How to get there",
	 * 		 "last_active_time":"3623627342734(in millisecond)",
	 * 		 "owner_name":"MrJie",
	 * 		 "number_of_members":"53",
	 * 		 "location_name":"Street No 5"},
	 * 		...(没有feed数组长度0)],
	 * 	"city":
	 * 		{"id":"33",
	 * 		 "name":"南京",
	 * 		 "latitude":"62637223",
	 * 		 "longitude":"3265312"},
	 * 	"error":"300/303"}
	 */
	public static final int RE_CITY_FEED = 216;
	/**
	 * {"type":"217",
	 * 	"city_id":"23",
	 * 	"error":"300/303"}
	 */
	public static final int RE_MYCITY = 217;
	/**
	 * {"type":"218",
	 * 	"logged_out":"true/false",
	 * 	"error":"300/303"}
	 */
	public static final int RE_LOGGED_OUT = 218;
	/**
	 * {"type":"219",
	 * 	"created":"true/false"
	 * 	"new_id":"36273",
	 * 	"error":"300/303"}
	 */
	public static final int RE_LOCATION_CREATED = 219;
	/**
	 * {"type":"220",
	 * 	"token":"difjsdhfis",
	 * 	"error":"300/303"}
	 */
	public static final int RE_UPLOAD_TOKEN = 220;
	/**
	 * {"type":"221",
	 * 	"updated":"true/false",
	 * 	"error":"300/303"}
	 */
	public static final int RE_IMAGE_UPDATED = 221;
	/**
	 * {"type":"222",
	 * 	"url":"http...",
	 * 	"error":"300/303"}
	 */
	public static final int RE_MYICON_URL = 222;
	/**
	 * {"type":"203",
	 * 	"locations":[
	 * 		{"id":"3523",
	 * 		 "name":"Street No 8",
	 * 		 "latitude(纬度)":"38273823",
	 * 		 "longitude(经度)":"116838200"},
	 * 		...(没有location数组长度0)],
	 * 	"error":"300/302"}
	 */
	public static final int RE_LOCATIONS = 223;
	public static final int RE_GROUP_PICS = 224;
	
	public static final int ERROR_UNKNOWN = 300;
	public static final int ERROR_USRNAME_PASSWD_NOT_MATCHED = 301;
	public static final int ERROR_NON_LOC = 302;
	public static final int ERROR_NOT_LOGIN = 303;
	public static final int ERROR_USRNAME_REPEATED = 304;
	
	public static final String PARAM_USRNAME = "u";
	public static final String PARAM_PASSWORD = "p";
	public static final String PARAM_SESSION = "s";
	public static final String PARAM_GROUPS = "g";
	public static final String PARAM_LOCATION = "l";
	public static final String PARAM_CITY = "c";
	public static final String PARAM_MESSAGES = "m";
	public static final String PARAM_TOKEN = "tk";
	public static final String PARAM_ACTION = "a";
	public static final String PARAM_URL = "ur";
	public static final String PARAM_LOCATIONS = "ls";
	public static final String PARAM_URLS = "us";
	
	public static final String PARAM_SENT = "sn";
	public static final String PARAM_FAVORED = "f";
	public static final String PARAM_REGISTERED = "r";
	public static final String PARAM_SET = "st";
	public static final String PARAM_CREATED = "cr";
	public static final String PARAM_REMOVED = "rm";
	public static final String PARAM_ENTERED = "n";
	public static final String PARAM_EXITED = "x";
	public static final String PARAM_FIRST_LOGIN = "fl";
	public static final String PARAM_HAS_FAVORED = "h";
	public static final String PARAM_LOGGED_OUT = "lg";
	public static final String PARAM_IMAGE_UPDATED = "iu";
	
	public static final String PARAM_LOCATION_ID = "li";
	public static final String PARAM_LATITUDE = "la";
	public static final String PARAM_LONGITUDE = "lo";
	public static final String PARAM_LOCATION_NAME = "ln";
	
	public static final String PARAM_MSG_CONTENT = "mc";
	public static final String PARAM_MSG_TIME = "mt";
	public static final String PARAM_MSG_FROM = "mf";
	
	public static final String PARAM_CITY_ID = "ci";
	public static final String PARAM_CITY_NAME = "cn";
	
	public static final String PARAM_GROUP_ID = "gi";
	public static final String PARAM_GROUP_TITLE = "gt";
	public static final String PARAM_GROUP_LAST_ACTIVE_TIME = "gl";
	public static final String PARAM_GROUP_OWNER_NAME = "go";
	public static final String PARAM_GROUP_NUMBER_MEMBERS = "gn";
	
	public static final int ACTION_ICON = 1;
	public static final int ACTION_MSG = 2;
	public static final int ACTION_PHOTO = 3;
	
	private static final String OPCODE_KEY = "o";
	private static final String RETURN_TYPE = "t";
	private static final String RETURN_ERROR = "e";
	
	private static final int BOOL_TRUE = 1;
	
	private static final String BRIEF_REMINDER_MSG_RECEIVING = "r";
	private static final String BRIEF_SPLITTER = "|";
	
	public static final int BEAN_GROUP = 0;
	public static final int BEAN_MESSAGE = 1;
	public static final int BEAN_LOCATION = 2;
	public static final int BEAN_CITY = 3;
	
	private JSONObject msg;
	
	public SocketMsg(int opCode) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(OPCODE_KEY, opCode);
		msg = new JSONObject(map);
	}
	
	public SocketMsg(String json) throws JSONException {
		msg = new JSONObject(json);
	}
	
	public SocketMsg(JSONObject jsonObject) {
		msg = jsonObject;
	}
	
	private String getBriefMsg(String reminder, String... params) {
		String msg = reminder;
		for (String param : params) {
			msg += BRIEF_SPLITTER + param;
		}
		return msg;
	}
	
	public String toString() {
		try {
			int opcode = msg.getInt(OPCODE_KEY);
			if (opcode == ASK_FOR_MESSAGE_RECEIVING) {
				String groupId = String.valueOf(msg.getInt(PARAM_GROUP_ID));
				String session = msg.getString(PARAM_SESSION);
				return getBriefMsg(BRIEF_REMINDER_MSG_RECEIVING, groupId, session);
			}
		} catch (JSONException e) {
		}
		return msg.toString();
	}
	
	public boolean putString(String key, String param) {
		try {
			msg.put(key, param);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}
	
	public boolean putInt(String key, int param) {
		try {
			msg.put(key, param);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}
	
	public boolean putLong(String key, long param) {
		try {
			msg.put(key, param);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	public int getMsgType() {
		int type = 0;
		try {
			type = msg.getInt(RETURN_TYPE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return type;
	}
	
	public int getMsgError() {
		int error = 0;
		try {
			error = msg.getInt(RETURN_ERROR);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return error;
	}

	/**
	 * 
	 * @param message
	 * @param key
	 * @return the content of the key, if key is not existing, return an empty string
	 */
	public String getStringMsgContent(String key) {
		String content = "";
		try {
			content = msg.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public int getIntMsgContent(String key) {
		int content = 0;
		try {
			content = msg.getInt(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public long getLongMsgContent(String key) {
		long content = 0;
		try {
			content = msg.getLong(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public boolean getBoolMsgContent(String key) {
		boolean content = false;
		// use a certain integer(not 0) to represent true
		int contentInt = getIntMsgContent(key);
		if (contentInt == BOOL_TRUE) {
			content = true;
		}
		return content;
	}

	public SocketMsg[] getArrayMsgContents(String key) {
		SocketMsg[] result = new SocketMsg[]{};
		try {
			JSONArray jsonArray = msg.getJSONArray(key);
			result = new SocketMsg[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				result[i] = new SocketMsg(jsonArray.getJSONObject(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * @param key
	 * @return null if the object is empty or the key does not exist
	 */
	public SocketMsg getObjectMsgContent(String key) {
		SocketMsg result = null;
		try {
			JSONObject jsonObject = msg.getJSONObject(key);
			result = new SocketMsg(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Object getBeanFromSocketMessage(int beanType) {
		switch (beanType) {
		case BEAN_GROUP:
			Group group = new Group();
			group.setId(getIntMsgContent(PARAM_GROUP_ID));
			group.setLastActiveTime(getLongMsgContent(PARAM_GROUP_LAST_ACTIVE_TIME));
			group.setLocation(getStringMsgContent(PARAM_LOCATION_NAME));
			group.setNumberOfMembers(getIntMsgContent(PARAM_GROUP_NUMBER_MEMBERS));
			group.setOwnerName(getStringMsgContent(PARAM_GROUP_OWNER_NAME));
			group.setTitle(getStringMsgContent(PARAM_GROUP_TITLE));
			group.setIconUrl(getStringMsgContent(PARAM_URL));
			return group;
		case BEAN_LOCATION:
			Location location = new Location();
			location.setId(getIntMsgContent(PARAM_LOCATION_ID));
			location.setLatitude(getIntMsgContent(PARAM_LATITUDE));
			location.setLongitude(getIntMsgContent(PARAM_LONGITUDE));
			location.setName(getStringMsgContent(PARAM_LOCATION_NAME));
			return location;
		case BEAN_MESSAGE:
			Message message = new Message();
			message.setContent(getStringMsgContent(PARAM_MSG_CONTENT));
			message.setFrom(getStringMsgContent(PARAM_MSG_FROM));
			message.setTime(getLongMsgContent(PARAM_MSG_TIME));
			message.setIconUrl(getStringMsgContent(PARAM_URL));
			return message;
		case BEAN_CITY:
			City city = new City();
			city.setId(getIntMsgContent(PARAM_CITY_ID));
			city.setName(getStringMsgContent(PARAM_CITY_NAME));
			city.setLatitude(getIntMsgContent(PARAM_LATITUDE));
			city.setLongitude(getIntMsgContent(PARAM_LONGITUDE));
			return city;
		default:
			return null;
		}
	}

}
