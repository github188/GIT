package com.royalstone.pos.web.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 封装了对数据库的操作，如取连接、关闭连接等
 * @author liangxinbiao
 */
public class DBConnection {

	private static IConnectionFactory factory =
		new ConnectionFactoryContainer();

	/**
	 * @param datasrc 数据库在应用服务器里的JNDI名字
	 * @return 返回根数据库的连接
	 */
	public static Connection getConnection(String datasrc) throws SQLException{
		return factory.getConnection(datasrc);
	}

	/**
	 * 设置连接工厂，所有连接透过连接工厂取得
	 * @param ifactory
	 */
	public static void setConnectionFactory(IConnectionFactory ifactory) {
		factory = ifactory;
	}
	
	public static IConnectionFactory getConnectionFactory(){
		return factory;
	}

	/**
	 * 关闭连接相关的对象
	 * @param rs
	 * @param stmt
	 * @param con
	 */
	public static void closeAll(ResultSet rs, Statement stmt, Connection con) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

}
