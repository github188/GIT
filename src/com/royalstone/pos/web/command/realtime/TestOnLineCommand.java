package com.royalstone.pos.web.command.realtime;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，测试应用服务器是否跟数据库服务器连接正常，返回POS机是否在联机状态的依据
 * @author liangxinbiao
 */
public class TestOnLineCommand implements ICommand {

	/**
	 * @see com.royalstone.pos.web.command.ICommand#excute(java.lang.Object[])
	 */
	public Object[] excute(Object[] values) {
		if (values != null
			&& values.length == 1) {
				Connection con = null;
				try {
					con = DBConnection.getConnection("java:comp/env/dbpos");
					Statement stmt=con.createStatement();
					stmt.execute("select 1");

					Object[] result = new Object[1];
					result[0] = "1";
					return result;
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					DBConnection.closeAll(null, null, con);
				}
		}
		return null;
	}
}
