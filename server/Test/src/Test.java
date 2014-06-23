import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import net.sf.json.JSONObject;
import net.sf.json.util.NewBeanInstanceStrategy;

public class Test {
	
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost:3306/compath";
	private static final String USER = "root";
	private static final String PASSWORD = "haozi521";
	
	public static void main(String[] args) {
//		String string = Test.getSendingMsg(101, new String[] {"a", "b"}, new String[] {"1'1", "22"});
//		System.out.println(string);
		
//		JSONObject jsonObject = JSONObject.fromObject("{\"key\":\"value\"}");
//		System.out.println(jsonObject.getString("key"));
		
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("a", 2);
//		jsonObject.put("b", "2");
//		System.out.println(jsonObject.toString());
		
//		System.out.println(Integer.parseInt("5"));
//		try {
//			Class.forName(DRIVER);
//			Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
//			String sql = "insert into `session` (`user_id`) values(2);";
//			PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//			
//			preparedStatement.execute();
//			ResultSet resultSet = preparedStatement.getGeneratedKeys();
//			resultSet.next();
//			System.out.println(resultSet.getInt(1));
//			
//			preparedStatement.close();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		System.out.println(new String("a/b/c").split("/")[2]);
	}
	
	public static String getSendingMsg(int opCode, String[] keys, String[] params) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("opcode", String.valueOf(opCode));
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], params[i]);
		}
		
		JSONObject json = JSONObject.fromObject(map);
		return json.toString();
	}

}
