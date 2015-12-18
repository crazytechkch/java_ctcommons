package com.crazytech.db;

import java.sql.SQLException;

public class MySqlUtil extends DBUtil {

	private static final String DRIVER = "com.mysql.jdbc.Driver"; 
	private static final String PROTOCOL = "jdbc:mysql";
	
	public MySqlUtil(String server, String dbName, String user, String pass) {
		super(DRIVER,PROTOCOL, server, dbName, user, pass);
		// TODO Auto-generated constructor stub
	}
	
	/* connect with SSH*/
	public MySqlUtil(String protocol,String server, String dbName, String user, String pass) {
		super(DRIVER,protocol, server, dbName, user, pass);
		// TODO Auto-generated constructor stub
	}
}
