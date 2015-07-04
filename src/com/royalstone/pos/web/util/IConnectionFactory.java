
package com.royalstone.pos.web.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 连接工厂的接口
 * @author liangxinbiao
 */
public interface IConnectionFactory {
	
	/**
	 * @param datasrc 数据库在应用服务器里的JNDI名字
	 * @return 返回根数据库的连接
	 */
	public Connection getConnection(String datasrc) throws SQLException ; 
	
}
