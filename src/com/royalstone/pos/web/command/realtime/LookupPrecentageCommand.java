package com.royalstone.pos.web.command.realtime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，负责查询商品信息
 * @author liangxinbiao
 */
public class LookupPrecentageCommand implements ICommand {

	private String errorMsg1 = null;
	private String errorMsg2 = null;

	/**
	 * @see ICommand#excute(Object[])
	 */
	public Object[] excute(Object[] values) {

		if (values != null
			&& values.length == 3
			&& (values[1] instanceof String)&& (values[2] instanceof String)) {

			Connection con = null;

			try {
				con = DBConnection.getConnection("java:comp/env/dbpos");

				Object[] result = new Object[3];
				result[0] = lookup(con, (String) values[1],(String) values[2]);
				result[1] = errorMsg1;
				result[2] = errorMsg2;
				return result;

			} catch (SQLException e) {
				e.printStackTrace();
				errorMsg1 = "数据库连接错误!";
				errorMsg2 = e.getMessage();
				
				Object[] result = new Object[3];
				result[0] = null;
				result[1] = errorMsg1;
				result[2] = errorMsg2;
				
				return result;


			} finally {
				DBConnection.closeAll(null, null, con);
			}

		}
		return null;
	}

/**
 *
 * @param connection
 * @param cardLevelID
 * @param deptID
 * @return
 */
	public String lookup(Connection connection, String cardLevelID,String deptID) {

		String result ="0";

		try {
			result = lookupWithException(connection, cardLevelID,deptID);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMsg1 = "数据库查询错误!";
			errorMsg2 = e.getMessage();
		}

		return result;
	}

	public String lookupWithException(Connection connection,String cardLevelID,String deptID)
		throws SQLException {
			
		String result = "0";

		PreparedStatement pstmt =
			connection.prepareStatement(
				"SELECT accurate FROM accurate where cardlevelid=? and deptid=? ; ");
        int level=-1;
        try {
            level=Integer.parseInt(cardLevelID);
        } catch (NumberFormatException e) {}
        int dept=-1;
         try {
            dept=Integer.parseInt(deptID);
        } catch (NumberFormatException e) {}
        pstmt.setInt(1,level);
        pstmt.setInt(2,dept);

		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			result = rs.getString("accurate");
		}
		rs.close();
    	return result;
	}

}
