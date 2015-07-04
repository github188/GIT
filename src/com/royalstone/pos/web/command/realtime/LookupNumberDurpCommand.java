package com.royalstone.pos.web.command.realtime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，负责查询抵用卷
 * @author zhouzhou
 */
public class LookupNumberDurpCommand implements ICommand {

	private String errorMsg1;
	private String errorMsg2;

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
			
		String result = "-1";
		String date;
		String Money;
		date = cardLevelID.substring(0,8);
		Money = cardLevelID.substring(8,cardLevelID.length());

		PreparedStatement pstmt =
			connection.prepareStatement(
				"SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED; " +
				"Declare @price Integer " +
                "Select @price =  Max(plyMoneyA)  from availability_Ply  " +
	            "where ? >= moneyStart " +
				"and convert(char(10),getdate(),120) <= convert(char(10),(convert(datetime,? ,120)),120) " + 
                "if @price = ? begin select @price as price " +
				"end else begin set @price = 0 " +
				"select @price as price end ;" );
		
		int money = Integer.parseInt(Money);
		int de = Integer.parseInt(deptID);
		
        pstmt.setInt(1,de);
        pstmt.setString(2,date);
        pstmt.setInt(3,money);

		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			result = rs.getString("price").trim();
		}
		rs.close();
    	return result;
	}

}
