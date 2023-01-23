//$Id$
package com.management.camp.vaccination.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static Connection conn;
	private static String dbDriver = "org.postgresql.Driver";
	private static String dbURL = "jdbc:postgresql://localhost:5432/vaccination_camp_management";
	private static String userName = "vennila-16250";
	private static String password = "";
	
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		if(conn!=null)
			return conn;
		
		Class.forName(dbDriver);
		conn = DriverManager.getConnection(dbURL, userName, password);
		
		return conn;
		
	}
	
	public static void closeConnection() throws SQLException {
		if(conn != null)
			conn.close();
	}

}
