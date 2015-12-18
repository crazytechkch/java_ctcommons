package com.crazytech.db;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBUtil implements Serializable {
	public static final String CLS_JTDS = "net.sourceforge.jtds.jdbc.Driver";
	//private String dbURL = "jdbc:jtds:sqlserver://soserver02/";
	private String driver;
	private String protocol;
	private String server;
	private String dbName;
	private String user;
	private String pass;
	private Connection conn;
	
	
	/**
	 * Constructor for DbMan Class
	 * @param dbName Database Name
	 * @param user Username of Database
	 * @param pass Password of Database
	 */
	public DBUtil(String driver, String protocol, String server, String dbName,String user, String pass){
		super();
		this.driver = driver;
		this.protocol = protocol;
		this.server = server;
		this.dbName = dbName;
		this.user = user;
		this.pass = pass;
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initData(String table, String sql) throws SQLException {
		execute("drop table if exist "+table);
		execute(sql);
	}
	
	public String getDatabaseUrl() {
		return protocol+"://"+server+"/"+dbName;
	}
	
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(getDatabaseUrl(), user, pass);
	}
	
	public ResultSet getResultSet(String sql) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		conn = getConnection();
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public String execute(String sql) throws SQLException {
		conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
		stmt.close();
		conn.close();
		return "success";
	}
	
	
	public String insertRecord (String tableName, NameValuePair cvp, 
			Boolean hasId, String idColName)throws SQLException{
		String columnsStr = "";
		String valuesStr = "'";
		for (String key : cvp.keySet()) {
			columnsStr+=key+",";
			valuesStr+=cvp.get(key).toString()+"','";
		}
		columnsStr = columnsStr.substring(0, columnsStr.lastIndexOf(","));
		valuesStr = valuesStr.substring(0, valuesStr.lastIndexOf("'")-1);
		if (hasId) {
			columnsStr = idColName+","+columnsStr;
			valuesStr = autoincId(tableName, idColName)+","+valuesStr;
		}
		String sql = "insert into "+tableName
				+"("+columnsStr+")"
				+"values("+valuesStr+")";
		execute(sql);
		return "success";
	}
	
	public String updateRecord (String tableName, NameValuePair valueNvp, String idColName, Long id) throws SQLException {
		String values = "";
		for (String key : valueNvp.keySet()) {
			values+=key+" = '"+valueNvp.get(key)+"',";
		}
		values = values.substring(0, values.lastIndexOf(","));
		String sql = "update "+tableName+" set "
				+values
				+" where "+idColName+" = "+id;
		execute(sql);
		return "success";
	}
	
	public String deleteRecord (String tableName, String idColName, Long id) throws SQLException {
		String sql = "delete from "+tableName+" where "+idColName+" = "+id;
		execute(sql);
		return "success";
	}
	
	public Boolean hasRecord(String tableName, String column, String value) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		Integer rowcount = 0;
		String sql = "select count("+column+") as nrows from "+tableName+" where "+column+"='"+value+"'";
		conn = getConnection();
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			rowcount = rs.getInt("nrows");
		}
		rs.close();
		stmt.close();
		conn.close();
		return rowcount>0;
	}
	
	public String getXmlResult(String sql) throws SQLException{
		ResultSet rs = getResultSet(sql.contains("for xml")?sql:(sql+" for xml auto"));
		String xml = "";
		ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			xml+=rs.getString(rsmd.getColumnLabel(1));
		}
		return xml;
	}
	
	private Integer autoincId(String tableName, String idColName) throws SQLException{
		 String sql = "select max("+idColName+") as maxid from "+tableName;
		 int maxId = 0;
		 Statement stmt = null;
		 ResultSet rs = null;
		 conn = getConnection();
		 stmt = conn.createStatement(); 
		 rs = stmt.executeQuery(sql);
		 if (rs.next()) {
			 maxId = rs.getInt("maxid");
		 }
		 rs.close();
		 stmt.close();
		 conn.close();
		 return maxId+1;
	}
	
	
	public String closeConnection() throws SQLException{
		conn.close();
		return "success";
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
}
