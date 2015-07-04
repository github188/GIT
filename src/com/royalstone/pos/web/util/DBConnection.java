package com.royalstone.pos.web.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ��װ�˶����ݿ�Ĳ�������ȡ���ӡ��ر����ӵ�
 * @author liangxinbiao
 */
public class DBConnection {

	private static IConnectionFactory factory =
		new ConnectionFactoryContainer();

	/**
	 * @param datasrc ���ݿ���Ӧ�÷��������JNDI����
	 * @return ���ظ����ݿ������
	 */
	public static Connection getConnection(String datasrc) throws SQLException{
		return factory.getConnection(datasrc);
	}

	/**
	 * �������ӹ�������������͸�����ӹ���ȡ��
	 * @param ifactory
	 */
	public static void setConnectionFactory(IConnectionFactory ifactory) {
		factory = ifactory;
	}
	
	public static IConnectionFactory getConnectionFactory(){
		return factory;
	}

	/**
	 * �ر�������صĶ���
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
