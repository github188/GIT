package com.royalstone.pos.web.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 连接工厂的实现，它的连接是直接透过JDBC取得的，
 * 只要用来在应用服务器外测试一些需要操作数据库的模块
 * @author liangxinbiao
 */
public class ConnectionFactoryDirect implements IConnectionFactory {

	private DataSource[] datasources =
		{
			new DataSource(
				"java:comp/env/dbcard",
				"com.microsoft.jdbc.sqlserver.SQLServerDriver",
				"jdbc:microsoft:sqlserver://172.16.16.108:1433;DatabaseName=MyshopSHCard",
				"sa",
				"sa"),
			new DataSource(
				"java:comp/env/dbpos",
				"com.microsoft.jdbc.sqlserver.SQLServerDriver",
				"jdbc:microsoft:sqlserver://172.16.7.197:1433;SelectMethod=Cursor;databasename=ApplePOS",
				"sa",
				"sa")};

	/**
	 * @see com.royalstone.pos.web.util.IConnectionFactory#getConnection(java.lang.String)
	 */
	public Connection getConnection(String datasrc) {

		for (int i = 0; i < datasources.length; i++) {
			if (datasources[i].getDatasourceName() != null
				&& datasources[i].getDatasourceName().equals(datasrc)) {
				try {
					Class.forName(datasources[i].getDriverName());
					Connection conn =
						DriverManager.getConnection(
							datasources[i].url,
							datasources[i].userName,
							datasources[i].password);
					return conn;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 数据源，保存有关要连接的数据库的信息，包括地址、JNDI名、URL等
	 * @author liangxinbiao
	 */
	private class DataSource {

		private String datasourceName;
		private String driverName;
		private String url;
		private String userName;
		private String password;

		public DataSource(
			String datasourceName,
			String driverName,
			String url,
			String userName,
			String password) {
			this.datasourceName = datasourceName;
			this.driverName = driverName;
			this.url = url;
			this.userName = userName;
			this.password = password;
		}

		/**
		 * @return JNDI名
		 */
		public String getDatasourceName() {
			return datasourceName;
		}

		/**
		 * @return 驱动程序类名
		 */
		public String getDriverName() {
			return driverName;
		}

		/**
		 * @return 连接数据库密码
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @return 连接数据库的JDBC URL
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * @return 连接数据库的用户名
		 */
		public String getUserName() {
			return userName;
		}

	}

}
