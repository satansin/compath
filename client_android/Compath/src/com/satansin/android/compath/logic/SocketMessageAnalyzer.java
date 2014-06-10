package com.satansin.android.compath.logic;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SocketMessageAnalyzer {

	// operation codes
	/**
	 * {"opcode":"101",
	 *  "usrname":"MrJin",
	 *  "password":"iIDns9sHD8dhs",
	 *  "session":�����ж�}
	 */
	public static final int ASK_FOR_SESSION = 101;
	/**
	 * {"opcode":"102",
	 *  "location_id":"5362",
	 *  "session":�����ж�}
	 */
	public static final int ASK_FOR_FEED = 102;
	/**
	 * {"opcode":"103",
	 *  "latitude":"118273632",
	 *  "longitude":"33283733",
	 *  "session":�����ж�}
	 */
	public static final int ASK_FOR_LOCATION = 103;
	/**
	 * {"opcode":"104",
	 *  "message":
	 *  	{"content":"hi",
	 *  	"time":"6348264376285(in millisecond)",
	 *  	"group_id":"36293"},
	 *  "session":"dfsfdif"(����session����������id��Ϊ��Ϣ��fromֵ)}
	 */
	public static final int ASK_FOR_MESSAGE_SENDING = 104;
	/**
	 * {"opcode":"105",
	 * 	"group_id":"6352",
	 * 	"session":"dfisojf88d"(����session����id��������from��id����Ϣ)}
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
	public static final int ASK_FOR_ADDING_TO_MYGROUPS = 107;
	/**
	 * {"opcode":"108",
	 * 	"usrname":"doubi",
	 * 	"password":"jidjfHIHDSUIFS",
	 * 	"session":�����ж�}
	 */
	public static final int ASK_FOR_REGISTER = 108;
	/**
	 * {"opcode":"109",
	 * 	"province":"����",
	 * 	"city":"�Ͼ�",
	 * 	"session":"djisfs"}
	 */
	public static final int ASK_FOR_CITY_SETTING = 109;
	
	// return type codes
	/**
	 * {"type":"201",
	 * 	"session":"iIDns9sHD8dhs"/"",
	 * 	"first_login":"true/false"}
	 */
	public static final int SESSION = 201;
	/**
	 * {"type":"202",
	 * 	"feeds":[
	 * 		{"id":"53723",
	 * 		 "title":"How to get there",
	 * 		 "last_active_time":"3623627342734(in millisecond)",
	 * 		 "owner_name":"MrJie",
	 * 		 "number_of_members":"53",
	 * 		 "location_name":"Street No 5"},
	 * 		...(û��feed���鳤��0)]}
	 */
	public static final int FEED = 202;
	/**
	 * {"type":"203",
	 * 	"location":
	 * 		{"id":"3523",
	 * 		 "name":"Street No 8",
	 * 		 "latitude(γ��)":"38273823",
	 * 		 "longitude(����)":"116838200"}}
	 */
	public static final int LOCATION = 203;
	/**
	 * {"type":"204",
	 * 	"message_sent":"true/false"}
	 */
	public static final int MESSAGE_SENT = 204;
	/**
	 * {"type":"205",
	 * 	"messages":[
	 * 		{"content":"hi",
	 * 		 "time":"37264736274",
	 * 		 "from":"MrJie"},
	 * 		...(û��message���鳤��Ϊ0)]}
	 */
	public static final int MESSAGE_RECEIVED = 205;
	/**
	 * {"type":"206",
	 * 	"mygroups":[
	 * 		{"id":"53723",
	 * 		 "title":"How to get there",
	 * 		 "last_active_time":"3623627342734(in millisecond)",
	 * 		 "owner_name":"MrJie",
	 * 		 "number_of_members":"53",
	 * 		 "location_name":"Street No 5"},
	 * 		...(û��feed���鳤��0)]}
	 */
	public static final int MYGROUPS = 206;
	/**
	 * {"type":"207",
	 * 	"added":"true/false"}
	 */
	public static final int ADDED_TO_MYGROUPS = 207;
	/**
	 * {"type":"208",
	 * 	"registered":"true/false"}
	 */
	public static final int REGISTERED = 208;
	/**
	 * {"type":"209",
	 * 	"city_set":"true/false"}
	 */
	public static final int CITY_SET = 209;
	
	private static final String OPCODE_KEY = "opcode";
	private static final String SESSION_KEY = "session";
	private static final String RETURN_TYPE = "type";
	
	private static MemoryService memoryService = ServiceFactory.getMemoryService();
	
	public static String getSendingMsg(int opCode) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(OPCODE_KEY, String.valueOf(opCode));
		map.put(SESSION_KEY, memoryService.getMySession());
		JSONObject json = new JSONObject(map);
		return json.toString();
	}

	public static String getSendingMsg(int opCode, String[] keys, String[] params) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(OPCODE_KEY, String.valueOf(opCode));
		map.put(SESSION_KEY, memoryService.getMySession());
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], params[i]);
		}
		JSONObject json = new JSONObject(map);
		return json.toString();
	}

	public static String getSendingMsg(int opCode, String beanKey, Object bean) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(OPCODE_KEY, String.valueOf(opCode));
		map.put(SESSION_KEY, memoryService.getMySession());
		map.put(beanKey, BeanJsonAnalyzer.getJsonFromBean(bean));
		JSONObject json = new JSONObject(map);
		return json.toString();
	}

	public static int getMsgType(String message) {
		int type = 0;
		try {
			JSONObject json = new JSONObject(message);
			type = Integer.parseInt(json.getString(RETURN_TYPE));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return type;
	}

	public static String getMsgContent(String message, String key) {
		String content = "";
		try {
			JSONObject json = new JSONObject(message);
			content = json.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String[] getMsgArrayContents(String message, String key) {
		String[] result = new String[]{};
		try {
			JSONObject json = new JSONObject(message);
			JSONArray jsonArray = json.getJSONArray(key);
			result = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				result[i] = jsonArray.getString(i); // TODO check the validity of this call
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Object getBeanFromSocketMessage(String content) {
		// TODO Auto-generated method stub
		return null;
	}

}
