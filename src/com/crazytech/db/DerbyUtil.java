package com.crazytech.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DerbyUtil extends DBUtil {

	private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver"; 
	private static final String PROTOCOL = "jdbc:derby:";
	private String dbName;
	
	public DerbyUtil(String server, String dbName, String user, String pass) {
		super(DRIVER, PROTOCOL, server, dbName, user, pass);
		this.dbName = dbName;
	}

	@Override
	public String getDatabaseUrl() {
		// TODO Auto-generated method stub
		return PROTOCOL+dbName+";created=true";
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return DriverManager.getConnection(getDatabaseUrl());
	}
}
