
package com.royalstone.pos.web.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ���ӹ����Ľӿ�
 * @author liangxinbiao
 */
public interface IConnectionFactory {
	
	/**
	 * @param datasrc ���ݿ���Ӧ�÷��������JNDI����
	 * @return ���ظ����ݿ������
	 */
	public Connection getConnection(String datasrc) throws SQLException ; 
	
}