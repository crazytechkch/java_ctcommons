package com.crazytech.db;

public class SqlUtil extends DBUtil {

	private static final String DRIVER = "net.sourceforge.jtds.jdbc.Driver";
	private static final String PROTOCOL = "jdbc:jtds:sqlserver";
	
	public SqlUtil(String server,
			String dbName, String user, String pass) {
		super(DRIVER, PROTOCOL, server, dbName, user, pass);
		// TODO Auto-generated constructor stub
	}

}
