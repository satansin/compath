package com.satansin.android.compath.logic;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SocketMessageAnalyzer {

	// operation codes
	public static final int ASK_FOR_SESSION = 101;
	public static final int ASK_FOR_FEED = 102;
	public static final int ASK_FOR_LOCATION = 103;
	public static final int ASK_FOR_MESSAGE_SENDING = 104;
	public static final int ASK_FOR_MESSAGE_RECEIVING = 105;
	public static final int ASK_FOR_MYGROUPS = 106;
	public static final int ASK_FOR_ADDING_TO_MYGROUPS = 107;
	public static final int ASK_FOR_REGISTER = 108;
	public static final int ASK_FOR_CITY_SETTING = 109;
	
	// return type code
	public static final int SESSION = 201;
	public static final int FEED = 202;
	public static final int LOCATION = 203;
	public static final int MESSAGE_SENT = 204;
	public static final int MESSAGE_RECEIVED = 205;
	public static final int MYGROUPS = 206;
	public static final int ADDED_TO_MYGROUPS = 207;
	public static final int REGISTERED = 208;
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
