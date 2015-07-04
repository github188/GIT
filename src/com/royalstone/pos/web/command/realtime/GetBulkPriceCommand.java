package com.royalstone.pos.web.command.realtime;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import com.royalstone.pos.favor.BulkPrice;
import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，负责查询商品的量贩促销信息
 * @author liangxinbiao
 */
public class GetBulkPriceCommand implements ICommand {

	private String errorMsg1;
	private String errorMsg2;

	/**
	 * @see com.royalstone.pos.web.command.ICommand#excute(java.lang.Object[])
	 */
	public Object[] excute(Object[] values) {
		if (values != null
			&& values.length == 2
			&& (values[1] instanceof String)) {

			Connection con = null;

			try {
				con = DBConnection.getConnection("java:comp/env/dbpos");

				Object[] result = new Object[3];
				result[0] = getBulkPrice(con, (String) values[1]);
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
	 * 根据商品编码从表discPrice_vgno里查询出它所参与的量贩促销信息
	 * @param connection 到数据库的连接
	 * @param code 商品编码
	 * @return 量贩促销信息
	 */
	public BulkPrice getBulkPrice(Connection connection, String code) {

		BulkPrice result = null;

		try {
			result = getBulkPriceWithException(connection, code);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMsg1 = "数据库查询错误!";
			errorMsg2 = e.getMessage();
		}

		return result;
	}

	public BulkPrice getBulkPriceWithException(
		Connection connection,
		String code)
		throws SQLException {

		BulkPrice result = null;

		String sql =
			" SELECT vgno, promprice1*100 promprice1, promprice2*100 promprice2, promprice3*100 promprice3, "
				+ " promprice4*100 promprice4, promprice5*100 promprice5, promprice6*100 promprice6, "
				+ " qty1, qty2, qty3, qty4, qty5, qty6, "
				+ " startdate, enddate "
				+ " FROM discPrice_vgno where vgno=?; ";

		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, code);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			String vgno = rs.getString("vgno");
			int promprice1 = (int) (rs.getDouble("promprice1"));
			int promprice2 = (int) (rs.getDouble("promprice2"));
			int promprice3 = (int) (rs.getDouble("promprice3"));
			int promprice4 = (int) (rs.getDouble("promprice4"));
			int promprice5 = (int) (rs.getDouble("promprice5"));
			int promprice6 = (int) (rs.getDouble("promprice6"));
			int qty1 = rs.getInt("qty1");
			int qty2 = rs.getInt("qty2");
			int qty3 = rs.getInt("qty3");
			int qty4 = rs.getInt("qty4");
			int qty5 = rs.getInt("qty5");
			int qty6 = rs.getInt("qty6");

			Date starttime = rs.getDate("startdate");
			Date endtime = rs.getDate("enddate");

			GregorianCalendar g_start = new GregorianCalendar();
			g_start.setTime(starttime);

			GregorianCalendar g_end = new GregorianCalendar();
			g_end.setTime(endtime);

			result =
				new BulkPrice(
					vgno,
					promprice1,
					qty1,
					promprice2,
					qty2,
					promprice3,
					qty3,
					promprice4,
					qty4,
					promprice5,
					qty5,
					promprice6,
					qty6,
					g_start,
					g_end);
		}

		return result;

	}

}
