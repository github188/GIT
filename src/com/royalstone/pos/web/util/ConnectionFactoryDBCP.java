package com.royalstone.pos.web.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * @author liangxinbiao
 */

public class ConnectionFactoryDBCP implements IConnectionFactory {

	private HashMap connMap = new HashMap();

	public ConnectionFactoryDBCP() {

	}

	public void addConnection(
		String dataSrc,
		String driver,
		String url,
		String user,
		String password,
		int maxActive,
		int maxIdel,
		int maxWaitTime) throws Exception {

		DBConnect dbConnect =
			new DBConnect(
				driver,
				url,
				user,
				password,
				maxActive,
				maxIdel,
				maxWaitTime);

		connMap.put(dataSrc, dbConnect);

	}

	public Connection getConnection(String datasrc) throws SQLException {

		Connection conn = null;

		try {

			DBConnect dbConnect = (DBConnect) connMap.get(datasrc);

			if (dbConnect != null) {
				conn = dbConnect.getConnection();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return conn;
	}

	private class DBConnect {

		private DataSource dataSource = null;
		private String driver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		private String url =
			"jdbc:microsoft:sqlserver://localhost:1433;SelectMethod=Cursor;databasename=MyshopPOS";
		private String user = "sa";
		private String password = "sa";

		private int maxActive = 2;
		private int maxIdel = 2;
		private int maxWaitTime = 10000;

		public DBConnect(
			String driver,
			String url,
			String user,
			String password,
			int maxActive,
			int maxIdel,
			int maxWaitTime)
			throws Exception {

			try {

				System.setProperty("jdbc.drivers", driver);

				Class.forName(driver);

				dataSource =
					setupDataSource(
						driver,
						user,
						password,
						url,
						maxActive,
						maxIdel,
						maxWaitTime);

			} catch (Exception e) {
				System.out.println(
					"DBConnect.java => Unable to load driver."
						+ e.getMessage());
				throw e;
			}

		}

		public DataSource setupDataSource(
			String sDrvName,
			String sUserName,
			String sPwd,
			String connectURI,
			int maxActive,
			int maxIdle,
			int maxWaitTime) {

			BasicDataSource ds = new BasicDataSource();
			ds.setDriverClassName(sDrvName);
			ds.setUsername(sUserName);
			ds.setPassword(sPwd);
			ds.setUrl(connectURI);
			ds.setMaxActive(maxActive);

			ds.setMaxIdle(maxIdle);
			ds.setMaxWait(maxWaitTime);
			
			ds.setValidationQuery("select 1");
			ds.setTestOnBorrow(true);
			
			return ds;
		}

		public Connection getConnection()
			throws SQLException, ClassNotFoundException {

			BasicDataSource bds = (BasicDataSource) dataSource;
			return dataSource.getConnection();

		}

		public String getDriver() {
			return driver;
		}

		public String getPassword() {
			return password;
		}

		public String getUrl() {
			return url;
		}

		public String getUser() {
			return user;
		}

		public void setDriver(String string) {
			driver = string;
		}

		public void setPassword(String string) {
			password = string;
		}

		public void setUrl(String string) {
			url = string;
		}

		public void setUser(String string) {
			user = string;
		}

		public int getMaxActive() {
			return maxActive;
		}

		public int getMaxIdel() {
			return maxIdel;
		}

		public int getMaxWaitTime() {
			return maxWaitTime;
		}

		public void setMaxActive(int i) {
			maxActive = i;
		}

		public void setMaxIdel(int i) {
			maxIdel = i;
		}

		public void setMaxWaitTime(int i) {
			maxWaitTime = i;
		}

	}
	
	public static void main(String[] args)
	{
       try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Connection con = null;

			con = DriverManager.getConnection("jdbc:jtds:sqlserver://172.16.13.88:1433/mySHOPPOS40_jkyy","sa","sa");
	          
            Statement st = con.createStatement();            
            ResultSet rs = st.executeQuery("SELECT * FROM paymode");
            while(rs.next())
            {
                String a = rs.getString("pmcode").trim();
                String b = rs.getString("pmname").trim();
                String c = String.valueOf(rs.getInt("sperate")).trim();

                System.out.println(a +" "+ b + " "+ c);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }    
        
	 }
}
