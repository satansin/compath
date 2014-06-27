import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.rs.PutPolicy;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TestServer {
	
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

	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost:3306/compath?autoReconnect=true";
	private static final String USER = "root";
	private static final String PASSWORD = "root";
	private Connection connection;
	
	private static final String BUCKET_NAME = "loc-chat-image-server";
	
	private static final int MAX_FEED_COUNT = 20;

	String advice = "{\"type\":\"201\",\"session\":\"pppp\",\"first_login\":\"true\"}";

	// String advice = "{\"type\":\"207\",\"registered\":\"true\"}";

	public TestServer() {
		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void go() {
		BufferedWriter logWriter = null;
		try {
			File log = new File("log.txt");
			if (!log.exists()) {
				log.createNewFile();
			}
			logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(log), "utf-8"));
			
			@SuppressWarnings("resource")
			ServerSocket serverSock = new ServerSocket(9527);
			
			String startInfo = "Server Start...";
			System.out.println(startInfo);
			logWriter.append(startInfo);
			logWriter.newLine();
			logWriter.newLine();
			logWriter.flush();
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			while (true) {
				try {
					Socket sock = serverSock.accept();
					
					InputStreamReader streamReader = new InputStreamReader(
							sock.getInputStream(), "utf-8");
					BufferedReader reader = new BufferedReader(streamReader);
					String input = reader.readLine();
					
					String receivedMsg = format.format(new Date(Calendar.getInstance().getTimeInMillis())) + " Receive: \t" + input;
					System.out.println(receivedMsg);
					logWriter.append(receivedMsg);
					logWriter.newLine();
					logWriter.flush();
					
					String result = analyze(input);
					
					OutputStreamWriter streamWriter = new OutputStreamWriter(
							sock.getOutputStream(), "utf-8");
					BufferedWriter writer = new BufferedWriter(streamWriter);
					
					writer.write(result + "\n");
					writer.close();
					
					String sentMsg = format.format(new Date(Calendar.getInstance().getTimeInMillis())) + " Send: \t" + result;
					System.out.println(sentMsg + "\n");
					logWriter.append(sentMsg);
					logWriter.newLine();
					logWriter.newLine();
					logWriter.flush();
				} catch (Exception e) {
					e.printStackTrace();
					logWriter.append(e.getStackTrace().toString());
					logWriter.newLine();
					logWriter.newLine();
					logWriter.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			String endInfo = "Server Close";
			System.out.println(endInfo);
			logWriter.append(endInfo);
			logWriter.newLine();
			logWriter.newLine();
			logWriter.flush();
			logWriter.close();
		} catch (IOException e) {
		}
	}

	private String analyze(String input) {
		if (input.startsWith("r")) {
			String[] split = input.split("\\|");
			return receiveMessage(Integer.parseInt(split[1]), split[2]);
		}
		JSONObject inputJson = JSONObject.fromObject(input);
		int opcode = inputJson.getInt(OPCODE_KEY);
		switch (opcode) {
		case 101:
			return login(inputJson);
		case 102:
			return getFeed(inputJson);
		case 103:
			return getLocation(inputJson);
		case 104:
			return sendMessage(inputJson);
		case 106:
			return getMygroups(inputJson);
		case 107:
			return favorGroup(inputJson);
		case 108:
			return register(inputJson);
		case 109:
			return setCity(inputJson);
		case 110:
			return createGroup(inputJson);
		case 111:
			return getFavoriteGroups(inputJson);
		case 112:
			return removeFromFavoriteGroups(inputJson);
		case 113:
			return enterGroup(inputJson);
		case 114:
			return exitGroup(inputJson);
		case 115:
			return getFavorStatus(inputJson);
		case 116:
			return getCityFeed(inputJson);
		case 117:
			return getCityId(inputJson);
		case 118:
			return logout(inputJson);
		case 119:
			return createLocation(inputJson);
		case 120:
			return uploadToken(inputJson);
		case 121:
			return imageUpdate(inputJson);
		case 122:
			return getMyiconUrl(inputJson);
		default:
			break;
		}
		return "";
	}

	private String getMyiconUrl(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 222);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_URL, "");
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_URL, "");
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}

		String url = "";
		try {
			String sql = "select `user_detail`.`icon_url` " +
						 "from `user_detail` " +
						 "where `user_detail`.`user_id` = ?;";
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				url = resultSet.getString("icon_url");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_URL, "");
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_URL, url);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	private String imageUpdate(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 221);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_IMAGE_UPDATED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_IMAGE_UPDATED, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}
		
		int action = inputJson.getInt(PARAM_ACTION);
		String url = inputJson.getString(PARAM_URL);
		switch (action) {
		case ACTION_ICON:
			boolean iconUpdated = false;
			try {
				String sql = "update `user_detail` set `icon_url` = ? where `user_id` = ?;";
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, url);
				preparedStatement.setInt(2, userid);
				
				preparedStatement.execute();
				iconUpdated = (preparedStatement.getUpdateCount() == 1);
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
				result.put(PARAM_IMAGE_UPDATED, 0);
				result.put(RETURN_ERROR, 300);
				return result.toString();
			}
			if (!iconUpdated) {
				result.put(PARAM_IMAGE_UPDATED, 0);
				result.put(RETURN_ERROR, 300);
				return result.toString();
			} else {
				result.put(PARAM_IMAGE_UPDATED, 1);
				result.put(RETURN_ERROR, 0);
				return result.toString();
			}
		// TODO other cases
		default:
			break;
		}
		result.put(PARAM_IMAGE_UPDATED, 0);
		result.put(RETURN_ERROR, 300);
		return result.toString();
	}

	private String uploadToken(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 220);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_TOKEN, "");
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_TOKEN, "");
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}
		
		try {
			Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
			PutPolicy putPolicy = new PutPolicy(BUCKET_NAME);
			String token = putPolicy.token(mac);
			
			result.put(PARAM_TOKEN, token);
			result.put(RETURN_ERROR, 0);
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result.put(PARAM_TOKEN, "");
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
	}

	private String createLocation(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 219);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_CREATED, 0);
			result.put(PARAM_LOCATION_ID, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_CREATED, 0);
			result.put(PARAM_LOCATION_ID, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}
		
		boolean locationInserted = false;
		int newId = 0;
		try {
			String name = inputJson.getString(PARAM_LOCATION_NAME);
			int lat = inputJson.getInt(PARAM_LATITUDE);
			int lon = inputJson.getInt(PARAM_LONGITUDE);
			int cityId = inputJson.getInt(PARAM_CITY_ID);
			
			String sql = "insert into `location` (`name`, `latitude`, `longitude`, `owner_id`, `city_id`) values(?, ?, ?, ?, ?);";
			PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, name);
			preparedStatement.setInt(2, lat);
			preparedStatement.setInt(3, lon);
			preparedStatement.setInt(4, userid);
			preparedStatement.setInt(5, cityId);
			
			preparedStatement.execute();
			locationInserted = (preparedStatement.getUpdateCount() == 1);
			
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if(resultSet.next()) {
				newId = resultSet.getInt(1);
			}
			resultSet.close();
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
			result.put(PARAM_CREATED, 0);
			result.put(PARAM_LOCATION_ID, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (!locationInserted) {
			result.put(PARAM_CREATED, 0);
			result.put(PARAM_LOCATION_ID, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (newId <= 0) {
			result.put(PARAM_CREATED, 0);
			result.put(PARAM_LOCATION_ID, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_CREATED, 1);
		result.put(PARAM_LOCATION_ID, newId);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	private String logout(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 218);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_LOGGED_OUT, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_LOGGED_OUT, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}

		try {
			String sql = "delete from `session` " +
						 "where `session`.`user_id` = ?;";
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_LOGGED_OUT, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_LOGGED_OUT, 1);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	private String getCityId(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 217);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_CITY_ID, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_CITY_ID, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}

		int cityId = 0;
		try {
			String sql = "select `user_detail`.`city_id` " +
						 "from `user_detail` " +
						 "where `user_detail`.`user_id` = ?;";
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				cityId = resultSet.getInt("city_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_CITY_ID, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (cityId <= 0) {
			result.put(PARAM_CITY_ID, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_CITY_ID, cityId);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	private String getCityFeed(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 216);
		JSONArray array = new JSONArray();
		JSONObject cityJson = new JSONObject();
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_GROUPS, new JSONArray());
			result.put(PARAM_CITY, new JSONObject());
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_GROUPS, new JSONArray());
			result.put(PARAM_CITY, new JSONObject());
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}

		int cityId = 0;
		try {
			String sql = "select `city`.`id`, `city`.`name`, `city`.`latitude`, `city`.`longitude`" +
						 "from `city`, `user_detail` " +
						 "where `user_detail`.`user_id` = ? and `user_detail`.`city_id` = `city`.`id`";
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				cityId = resultSet.getInt("id");
				
				cityJson.put(PARAM_CITY_ID, cityId);
				cityJson.put(PARAM_CITY_NAME, resultSet.getString("name"));
				cityJson.put(PARAM_LATITUDE, resultSet.getInt("latitude"));
				cityJson.put(PARAM_LONGITUDE, resultSet.getInt("longitude"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_GROUPS, new JSONArray());
			result.put(PARAM_CITY, new JSONObject());
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (cityId <= 0) {
			result.put(PARAM_GROUPS, new JSONArray());
			result.put(PARAM_CITY, new JSONObject());
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		try {
			String sql = "select `group`.`id`, `group`.`title`, `group`.`last_active_time`, `user`.`username`, count(`participation`.`user_id`) as `number_of_members`, `location`.`name`, `user_detail`.`icon_url` " +
						 "from `user`, `user_detail`, `location`, `group` left join `participation` on `participation`.`group_id` = `group`.`id` " +
						 "where `location`.`city_id` = ? and " +
						 	   "`location`.`id` = `group`.`location_id` and " +
						 	   "`group`.`owner_id` = `user`.`id` and " +
						 	   "`user`.`id` = `user_detail`.`user_id` " +
						 "group by `group`.`id` " +
						 "order by `number_of_members` desc;";
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, cityId);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			int i = 0;
			while (resultSet.next() && (i++) < MAX_FEED_COUNT) {
				HashMap<String, Object> itemMap = new HashMap<String, Object>();
				itemMap.put(PARAM_GROUP_ID, resultSet.getInt("id"));
				itemMap.put(PARAM_GROUP_TITLE, resultSet.getString("title"));
				itemMap.put(PARAM_GROUP_LAST_ACTIVE_TIME, resultSet.getLong("last_active_time"));
				itemMap.put(PARAM_GROUP_OWNER_NAME, resultSet.getString("username"));
				itemMap.put(PARAM_GROUP_NUMBER_MEMBERS, resultSet.getInt("number_of_members"));
				itemMap.put(PARAM_LOCATION_NAME, resultSet.getString("name"));
				itemMap.put(PARAM_URL, resultSet.getString("icon_url"));
				array.add(itemMap);
			}

			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_CITY, new JSONObject());
			result.put(RETURN_ERROR, 300);
			result.put(PARAM_GROUPS, new JSONArray());
			return result.toString();
		}

		result.put(PARAM_CITY, cityJson);
		result.put(RETURN_ERROR, 0);
		result.put(PARAM_GROUPS, array);
		return result.toString();
	}

	private String getFavorStatus(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 215);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_HAS_FAVORED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_HAS_FAVORED, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}

		boolean hasFavored = false;
		try {
			int groupId = inputJson.getInt(PARAM_GROUP_ID);
			
			String sql = "select * from `group_favor` where `user_id` = ? and `group_id` = ?;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			preparedStatement.setInt(2, groupId);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			hasFavored = resultSet.next();
			
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_HAS_FAVORED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		if (hasFavored) {
			result.put(PARAM_HAS_FAVORED, 1);
		} else {
			result.put(PARAM_HAS_FAVORED, 0);
		}
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	private String exitGroup(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 214);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_EXITED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_EXITED, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}

		try {
			int groupId = inputJson.getInt(PARAM_GROUP_ID);
			
			String sql = "delete from `participation` where `user_id` = ? and `group_id` = ?;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			preparedStatement.setInt(2, groupId);
			
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_EXITED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_EXITED, 1);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	// group_id, session
	private String enterGroup(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 213);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_ENTERED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_ENTERED, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}

		int groupId = inputJson.getInt(PARAM_GROUP_ID);
		try {
			String sql = "delete from `participation` where `user_id` = ? and `group_id` = ?;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			preparedStatement.setInt(2, groupId);
			
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_ENTERED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		boolean participationInserted = false;
		try {
			long current = Calendar.getInstance().getTimeInMillis();
			String sql = "insert into `participation` (`user_id`, `group_id`, `last_received_time`) values(?, ?, ?);";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			preparedStatement.setInt(2, groupId);
			preparedStatement.setLong(3, current);
			
			preparedStatement.execute();
			participationInserted = (preparedStatement.getUpdateCount() == 1);
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_ENTERED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (!participationInserted) {
			result.put(PARAM_ENTERED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_ENTERED, 1);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	// group_id, session
	private String removeFromFavoriteGroups(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 212);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_REMOVED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_REMOVED, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}
		
		try {
			int groupId = inputJson.getInt(PARAM_GROUP_ID);
			
			String sql = "delete from `group_favor` where `user_id` = ? and `group_id` = ?;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			preparedStatement.setInt(2, groupId);
			
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_REMOVED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_REMOVED, 1);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	// session
	private String getFavoriteGroups(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 211);
		JSONArray groupArray = new JSONArray();
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_GROUPS, groupArray);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_GROUPS, groupArray);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}
		
		try {
			String sql = "select `group`.`id`, `group`.`title`, `group`.`last_active_time`, `user`.`username`, count(`participation`.`user_id`) as `number_of_members`, `location`.`name`, `user_detail`.`icon_url` " +
						 "from `user`, `user_detail`, `location`, `group_favor`, `group` left join `participation` on `participation`.`group_id` = `group`.`id` " +
						 "where `group_favor`.`user_id` = ? and " +
						  	   "`group_favor`.`group_id` = `group`.`id` and " +
						  	   "`user`.`id` = `group`.`owner_id` and " +
						  	   "`user`.`id` = `user_detail`.`user_id` and " +
						  	   "`group`.`location_id` = `location`.`id` " + 
						 "group by `group`.`id`;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				JSONObject group = new JSONObject();
				group.put(PARAM_GROUP_ID, resultSet.getInt("id"));
				group.put(PARAM_GROUP_TITLE, resultSet.getString("title"));
				group.put(PARAM_GROUP_LAST_ACTIVE_TIME, resultSet.getLong("last_active_time"));
				group.put(PARAM_GROUP_OWNER_NAME, resultSet.getString("username"));
				group.put(PARAM_GROUP_NUMBER_MEMBERS, resultSet.getInt("number_of_members"));
				group.put(PARAM_LOCATION_NAME, resultSet.getString("name"));
				group.put(PARAM_URL, resultSet.getString("icon_url"));
				groupArray.add(group);
			}
			
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_GROUPS, new JSONArray());
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_GROUPS, groupArray);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	// title, location_id, session
	private String createGroup(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 210);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_CREATED, 0);
			result.put(PARAM_GROUP_ID, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_CREATED, 0);
			result.put(PARAM_GROUP_ID, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}
		
		boolean groupInserted = false;
		int newId = 0;
		try {
			String title = inputJson.getString(PARAM_GROUP_TITLE);
			long current = Calendar.getInstance().getTimeInMillis();
			int locationId = inputJson.getInt(PARAM_LOCATION_ID);
			
			String sql = "insert into `group` (`create_time`, `owner_id`, `last_active_time`, `location_id`, `title`) values(?, ?, ?, ?, ?);";
			PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setLong(1, current);
			preparedStatement.setInt(2, userid);
			preparedStatement.setLong(3, current);
			preparedStatement.setInt(4, locationId);
			preparedStatement.setString(5, title);
			
			preparedStatement.execute();
			groupInserted = (preparedStatement.getUpdateCount() == 1);
			
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if(resultSet.next()) {
				newId = resultSet.getInt(1);
			}
			resultSet.close();
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
			result.put(PARAM_CREATED, 0);
			result.put(PARAM_GROUP_ID, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (!groupInserted) {
			result.put(PARAM_CREATED, 0);
			result.put(PARAM_GROUP_ID, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (newId <= 0) {
			result.put(PARAM_CREATED, 0);
			result.put(PARAM_GROUP_ID, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_CREATED, 1);
		result.put(PARAM_GROUP_ID, newId);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	// province, city, session
	private String setCity(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 209);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_SET, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_SET, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}
		
		boolean citySet = false;
		try {
			int cityId = inputJson.getInt(PARAM_CITY_ID);
			
			String sql = "update `user_detail` set `city_id` = ? where `user_id` = ?;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, cityId);
			preparedStatement.setInt(2, userid);
			
			preparedStatement.execute();
			citySet = (preparedStatement.getUpdateCount() == 1);
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_SET, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (!citySet) {
			result.put(PARAM_SET, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_SET, 1);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	// usrname, password
	private String register(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 208);
		
		String username = inputJson.getString(PARAM_USRNAME);
		
		boolean usernameExisted = false;
		try {
			String sql = "select * from `user` where `username` = ?;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, username);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			usernameExisted = resultSet.next();
			
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_REGISTERED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (usernameExisted) {
			result.put(PARAM_REGISTERED, 0);
			result.put(RETURN_ERROR, 304);
			return result.toString();
		}
		
		boolean userInserted = true;
		int userid = 0;
		try {
			String password = inputJson.getString(PARAM_PASSWORD);
			
			String sql = "insert into `user` (`username`, `password`) values(?, ?);";
			PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			
			preparedStatement.execute();
			userInserted = (preparedStatement.getUpdateCount() == 1);
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if (resultSet.next()) {
				userid = resultSet.getInt(1);
			}
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_REGISTERED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (!userInserted) {
			result.put(PARAM_REGISTERED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_REGISTERED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		boolean detailInserted = false;
		try {
			String sql = "insert into `user_detail` (`user_id`) values(?);";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			
			preparedStatement.execute();
			detailInserted = (preparedStatement.getUpdateCount() == 1);
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_REGISTERED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (!detailInserted) {
			result.put(PARAM_REGISTERED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}

		result.put(PARAM_REGISTERED, 1);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	// group_id, session
	private String favorGroup(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 207);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_FAVORED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_FAVORED, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}
		
		boolean favorInserted = false;
		try {
			int groupId = inputJson.getInt(PARAM_GROUP_ID);
			
			String sql = "insert into `group_favor` (`user_id`, `group_id`) values(?, ?);";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			preparedStatement.setInt(2, groupId);
			
			preparedStatement.execute();
			favorInserted = (preparedStatement.getUpdateCount() == 1);
			preparedStatement.close();
		} catch (SQLException e) {
			if (e.getMessage().contains("Duplicate")) {
				result.put(PARAM_FAVORED, 1);
				result.put(RETURN_ERROR, 0);
				return result.toString();
			}
			e.printStackTrace();
			result.put(PARAM_FAVORED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (!favorInserted) {
			result.put(PARAM_FAVORED, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_FAVORED, 1);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	// session
	private String getMygroups(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 206);
		JSONArray groupArray = new JSONArray();
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_GROUPS, groupArray);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_GROUPS, groupArray);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}
		
		try {
			String sql = "select `group`.`id`, `group`.`title`, `group`.`last_active_time`, `user`.`username`, count(`participation`.`user_id`) as `number_of_members`, `location`.`name`, `user_detail`.`icon_url` " +
						 "from `user`, `user_detail` `location`, `group` left join `participation`  on `group`.`id` = `participation`.`group_id` " +
						 "where `group`.`owner_id` = ? and " +
						  	   "`user`.`id` = `group`.`owner_id` and " +
						  	   "`user`.`id` = `user_detail`.`user_id` and " +
						  	   "`group`.`location_id` = `location`.`id` " + 
						 "group by `group`.`id` " +
						 "order by `group`.`create_time` desc;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userid);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				JSONObject group = new JSONObject();
				group.put(PARAM_GROUP_ID, resultSet.getInt("id"));
				group.put(PARAM_GROUP_TITLE, resultSet.getString("title"));
				group.put(PARAM_GROUP_LAST_ACTIVE_TIME, resultSet.getLong("last_active_time"));
				group.put(PARAM_GROUP_OWNER_NAME, resultSet.getString("username"));
				group.put(PARAM_GROUP_NUMBER_MEMBERS, resultSet.getInt("number_of_members"));
				group.put(PARAM_LOCATION_NAME, resultSet.getString("name"));
				group.put(PARAM_URL, resultSet.getString("icon_url"));
				groupArray.add(group);
			}
			
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_GROUPS, new JSONArray());
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_GROUPS, groupArray);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	// group_id, session
	private String receiveMessage(int groupId, String session) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 205);
		JSONArray messageArray = new JSONArray();

		int userid = 0;
		try {
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_MESSAGES, new JSONArray());
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_MESSAGES, new JSONArray());
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}
		
		try {
			String sql = "select `message`.`content`, `message`.`time`, `user`.`username`, `user`.`id`, `user_detail`.`icon_url` " +
						 "from `message`, `user`, `user_detail` " +
						 "where `message`.`group_id` = ? and " +
						  	   "`message`.`sender_id` = `user`.`id` and " +
						  	   "`user`.`id` = `user_detail`.`user_id` and " +
						  	   "`message`.`time` > " +
						  	   		"(select `last_received_time` from `participation` where `group_id` = ? and `user_id` = ?);";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, groupId);
			preparedStatement.setInt(2, groupId);
			preparedStatement.setInt(3, userid);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				if (resultSet.getInt("id") == userid) {
					continue;
				}
				JSONObject message = new JSONObject();
				message.put(PARAM_MSG_CONTENT, resultSet.getString("content"));
				message.put(PARAM_MSG_TIME, resultSet.getLong("time"));
				message.put(PARAM_MSG_FROM, resultSet.getString("username"));
				message.put(PARAM_URL, resultSet.getString("icon_url"));
				messageArray.add(message);
			}
			
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_MESSAGES, new JSONArray());
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		boolean timeUpdated = false;
		try {
			long current = Calendar.getInstance().getTimeInMillis();
			String sql = "update `participation` set `last_received_time` = ? where `group_id` = ? and `user_id` = ?;";
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, current);
			preparedStatement.setInt(2, groupId);
			preparedStatement.setInt(3, userid);
			
			preparedStatement.execute();
			timeUpdated = (preparedStatement.getUpdateCount() == 1);
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_MESSAGES, new JSONArray());
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (!timeUpdated) {
			result.put(PARAM_MESSAGES, new JSONArray());
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_MESSAGES, messageArray);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	private String sendMessage(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 204);
		
		int userid = 0;
		try {
			String session = inputJson.getString(PARAM_SESSION);
			userid = readUseridFromSession(session);
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_SENT, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (userid == 0) {
			result.put(PARAM_SENT, 0);
			result.put(RETURN_ERROR, 303);
			return result.toString();
		}

		int groupId = inputJson.getInt(PARAM_GROUP_ID);
		boolean messageInserted = false;
		try {
			String msgContent = inputJson.getString(PARAM_MSG_CONTENT);
			long time = Calendar.getInstance().getTimeInMillis();
			
			String sql = "insert into `message` (`content`, `time`, `group_id`, `sender_id`) values(?, ?, ?, ?);";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, msgContent);
			preparedStatement.setLong(2, time);
			preparedStatement.setInt(3, groupId);
			preparedStatement.setInt(4, userid);
			
			preparedStatement.execute();
			messageInserted = (preparedStatement.getUpdateCount() == 1);
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_SENT, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (!messageInserted) {
			result.put(PARAM_SENT, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		try {
			long current = Calendar.getInstance().getTimeInMillis();
			String sql = "update `group` set `last_active_time` = ? where `id` = ?;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, current);
			preparedStatement.setInt(2, groupId);
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_SENT, 0);
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		
		result.put(PARAM_SENT, 1);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	private String getLocation(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 203);
		JSONObject locationJson = new JSONObject();

		try {
			int lon = inputJson.getInt(PARAM_LONGITUDE);
			int lat = inputJson.getInt(PARAM_LATITUDE);
			
			String sql = "select l.`id`, l.`name`, l.`latitude`, l.`longitude` from " +
							"(select `id`, `name`, `latitude`, `longitude` " +
							 "from `location` " +
							 "where `latitude` >= ? and " +
							 	   "`latitude` <= ? and " +
							 	   "`longitude` >= ? and " +
							 	   "`longitude` <= ?) l " +
						 "order by (pow(l.`latitude` - ?, 2) + pow(l.`longitude` - ?, 2)) asc;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, (int)(lat - 1e4));
			preparedStatement.setInt(2, (int)(lat + 1e4));
			preparedStatement.setInt(3, (int)(lon - 1e4));
			preparedStatement.setInt(4, (int)(lon + 1e4));
			preparedStatement.setInt(5, lat);
			preparedStatement.setInt(6, lon);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				locationJson.put(PARAM_LOCATION_ID, resultSet.getInt("id"));
				locationJson.put(PARAM_LOCATION_NAME, resultSet.getString("name"));
				locationJson.put(PARAM_LATITUDE, resultSet.getInt("latitude"));
				locationJson.put(PARAM_LONGITUDE, resultSet.getInt("longitude"));
			} else {
				result.put(PARAM_LOCATION, new JSONObject());
				result.put(RETURN_ERROR, 302);
				return result.toString();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_LOCATION, new JSONObject());
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}

		result.put(PARAM_LOCATION, locationJson);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	private String getFeed(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 202);
		JSONArray array = new JSONArray();

		try {
			int locationId = inputJson.getInt(PARAM_LOCATION_ID);
			String sql = "select `group`.`id`, `group`.`title`, `group`.`last_active_time`, `user`.`username`, count(`participation`.`user_id`) as `number_of_members`, `location`.`name`, `user_detail`.`icon_url` " +
						 "from `user`, `user_detail`, `location`, `group` left join `participation` on `participation`.`group_id` = `group`.`id` " +
						 "where `group`.`location_id` = ? and " +
						  	   "`user`.`id` = `group`.`owner_id` and " +
						  	   "`user`.`id` = `user_detail`.`user_id` and " +
						  	   "`group`.`location_id` = `location`.`id` " + 
						 "group by `group`.`id` " +
						 "order by `number_of_members` desc;";
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, locationId);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			int i = 0;
			while (resultSet.next() && (i++) < MAX_FEED_COUNT) {
				HashMap<String, Object> itemMap = new HashMap<String, Object>();
				itemMap.put(PARAM_GROUP_ID, resultSet.getInt("id"));
				itemMap.put(PARAM_GROUP_TITLE, resultSet.getString("title"));
				itemMap.put(PARAM_GROUP_LAST_ACTIVE_TIME, resultSet.getLong("last_active_time"));
				itemMap.put(PARAM_GROUP_OWNER_NAME, resultSet.getString("username"));
				itemMap.put(PARAM_GROUP_NUMBER_MEMBERS, resultSet.getInt("number_of_members"));
				itemMap.put(PARAM_LOCATION_NAME, resultSet.getString("name"));
				itemMap.put(PARAM_URL, resultSet.getString("icon_url"));
				array.add(itemMap);
			}

			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(RETURN_ERROR, 300);
			result.put(PARAM_GROUPS, new JSONArray());
			return result.toString();
		}

		result.put(RETURN_ERROR, 0);
		result.put(PARAM_GROUPS, array);
		return result.toString();
	}

	private String login(JSONObject inputJson) {
		JSONObject result = new JSONObject();
		result.put(RETURN_TYPE, 201);
		int session = 0;
		boolean firstLogin = false;
		String iconUrl = "";

		boolean valid = false;
		int id = 0;
		try {
			String username = inputJson.getString(PARAM_USRNAME);
			String password = inputJson.getString(PARAM_PASSWORD);
			
			String sql = "select `user`.`id`, `user_detail`.`city_id`, `user_detail`.`icon_url` " +
						 "from `user`, `user_detail` " +
						 "where `user`.`username` = ? and " +
						 	   "`user`.`password` = ? and " +
						 	   "`user`.`id` = `user_detail`.`user_id`;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				valid = true;
				id = resultSet.getInt("id");
				int city = resultSet.getInt("city_id");
				if (city == 0) {
					firstLogin = true;
				}
				iconUrl = resultSet.getString("icon_url");
			}

			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_SESSION, "");
			result.put(PARAM_FIRST_LOGIN, 0);
			result.put(PARAM_URL, "");
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}

		if (!valid) {
			result.put(PARAM_SESSION, "");
			result.put(PARAM_FIRST_LOGIN, 0);
			result.put(PARAM_URL, "");
			result.put(RETURN_ERROR, "301");
			return result.toString();
		}

		try {
			String sql = "delete from `session` where `user_id` = ?;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_SESSION, "");
			result.put(PARAM_FIRST_LOGIN, 0);
			result.put(PARAM_URL, "");
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}

		boolean sessionInserted = false;
		try {
			String sql = "insert into `session` (`user_id`) values(?);";
			PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, id);
			
			preparedStatement.execute();
			sessionInserted = (preparedStatement.getUpdateCount() == 1);
			
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if (resultSet.next()) {
				session = resultSet.getInt(1);
			}
			
			resultSet.close();
			preparedStatement.close();
			
			if (session <= 0) {
				result.put(PARAM_SESSION, "");
				result.put(PARAM_FIRST_LOGIN, 0);
				result.put(PARAM_URL, "");
				result.put(RETURN_ERROR, 300);
				return result.toString();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result.put(PARAM_SESSION, "");
			result.put(PARAM_FIRST_LOGIN, 0);
			result.put(PARAM_URL, "");
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}
		if (!sessionInserted) {
			result.put(PARAM_SESSION, "");
			result.put(PARAM_FIRST_LOGIN, 0);
			result.put(PARAM_URL, "");
			result.put(RETURN_ERROR, 300);
			return result.toString();
		}

		result.put(PARAM_SESSION, String.valueOf(session));
		if (firstLogin) {
			result.put(PARAM_FIRST_LOGIN, 1);
		} else {
			result.put(PARAM_FIRST_LOGIN, 0);
		}
		result.put(PARAM_URL, iconUrl);
		result.put(RETURN_ERROR, 0);
		return result.toString();
	}

	private int readUseridFromSession(String session) throws SQLException {
		int sessionInt = 0;
		try {
			sessionInt = Integer.parseInt(session);
		} catch (Exception e) {
			return 0;
		}
		int id = 0;
		String sql = "select `user_id` from `session` where `session` = ?;";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, sessionInt);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			id = resultSet.getInt("user_id");
		}
		resultSet.close();
		preparedStatement.close();
		return id;
	}

	public static void main(String[] args) {
		TestServer server = new TestServer();
		server.go();
		
//		JSONObject testJsonObject1 = new JSONObject();
//		testJsonObject.put("usrname", "doubi");
//		testJsonObject.put("password", "iiiiiiiiiiiiiiiii");
//		testJsonObject.put(PARAM_CITY_ID, "1");
//		testJsonObject1.put(PARAM_SESSION, "2A331F770EC6B6F47AB0F0F3B3317D65");
//		System.out.println(server.register(testJsonObject1));
//		System.out.println(server.login(testJsonObject1));
//		System.out.println(server.setCity(testJsonObject1));
//		testJsonObject1.put("latitude", "32026595");
//		testJsonObject1.put("longitude", "118782919");
//		System.out.println(server.getLocation(testJsonObject1));
//		testJsonObject.put("title", "Hey look!");
//		testJsonObject.put("location_id", "1");
//		System.out.println(server.createGroup(testJsonObject1));
//		System.out.println(server.getMygroups(testJsonObject1));
//		testJsonObject1.put("group_id", "1");
//		System.out.println(server.removeFromFavoriteGroups(testJsonObject1));
//		System.out.println(server.getFavoriteGroups(testJsonObject1));
//		System.out.println(server.getFavorStatus(testJsonObject1));
//		System.out.println(server.getFeed(testJsonObject1));
//		System.out.println(server.getCityFeed(testJsonObject1));
//		System.out.println(server.enterGroup(testJsonObject1));
		
//		JSONObject message = new JSONObject();
//		message.put("content", "yo, we made connected~");
//		message.put("time", String.valueOf(Calendar.getInstance().getTimeInMillis()));
//		message.put("group_id", "1");
//		testJsonObject1.put("message", message);
//		System.out.println(server.sendMessage(testJsonObject1));
		
//		System.out.println(server.receiveMessage(testJsonObject1));
		
////////////////////////////////////////////////////////////////////////////		
		
//		JSONObject jsonObject2 = new JSONObject();
//		jsonObject2.put("usrname", "haha");
//		jsonObject2.put("password", "aaaaaaaaaaaaaaaa");
//		jsonObject2.put(PARAM_SESSION, "76019476DCF5575A70EBAA01B0E55DEC");
//		System.out.println(server.register(jsonObject2));
//		System.out.println(server.login(jsonObject2));
//		jsonObject2.put(PARAM_CITY_ID, 1);
//		System.out.println(server.setCity(jsonObject2));
//		jsonObject2.put("group_id", "1");
//		System.out.println(server.enterGroup(jsonObject2));
		
//		JSONObject message = new JSONObject();
//		message.put("content", "haha");
//		message.put("time", String.valueOf(Calendar.getInstance().getTimeInMillis()));
//		message.put("group_id", "1");
//		jsonObject2.put("message", message);
//		System.out.println(server.sendMessage(jsonObject2));
//		
//		JSONObject message2 = new JSONObject();
//		message2.put("content", "let me try more than one message");
//		message2.put("time", String.valueOf(Calendar.getInstance().getTimeInMillis()));
//		message2.put("group_id", "1");
//		jsonObject2.put("message", message2);
//		System.out.println(server.sendMessage(jsonObject2));
		
//		System.out.println(server.receiveMessage(jsonObject2));
	}

//	// MD5 TODO change it to the ai
//	private String generateSession(int userId) {
//		String initial = String.valueOf(userId) + Calendar.getInstance().getTimeInMillis();
//		
//		byte[] btInput = initial.getBytes();
//		char[] md5String = new String("0123456789ABCDEF").toCharArray();
//		try {
//			MessageDigest mdInst;
//			mdInst = MessageDigest.getInstance("MD5");
//			mdInst.update(btInput);
//			byte[] md = mdInst.digest();
//			int j = md.length;
//			char str[] = new char[j * 2];
//			int k = 0;
//			for (int i = 0; i < j; i++) {
//				byte byte0 = md[i];
//				str[k++] = md5String[byte0 >>> 4 & 0xf];
//				str[k++] = md5String[byte0 & 0xf];
//			}
//			return new String(str);
//		} catch (NoSuchAlgorithmException e) {
//			return null;
//		}
//	}
}
