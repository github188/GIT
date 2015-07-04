package com.royalstone.pos.web.command.realtime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.royalstone.pos.common.GoodsExt;
import com.royalstone.pos.data.BatchnoData;
import com.royalstone.pos.data.PosPriceData;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，负责查询一品多码的商品信息
 * @author liangxinbiao
 */
public class GetBatchnoinfoException implements ICommand {

	private String errorMsg1;
	private String errorMsg2;

	/**
	 * @see com.royalstone.pos.web.command.ICommand#excute(java.lang.Object[])
	 */
	public Object[] excute(Object[] values) {

		if (values != null
			&& values.length == 2
			&& (values[1] instanceof PosPriceData)) {

			Connection con = null;

			try {
				con = DBConnection.getConnection("java:comp/env/dbpos");

				Object[] result = new Object[3];
				result[0] = getBatchnolistWithException(con, (PosPriceData) values[1]);
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

	public ArrayList getBatchnolistWithException(
		Connection connection,
		PosPriceData code)
		throws SQLException {

		ArrayList result = new ArrayList();
		
		PreparedStatement pstmt =
			connection.prepareStatement(
				"SELECT ShopID, Vgno, Batchno, toeffectivedate, Flag, SaleFlag "
					+ "FROM shopbatchno where vgno=?  order  by batchno desc ");

		pstmt.setString(1, code.getSaleCode());

		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) 
		{
			BatchnoData data = new BatchnoData();
			data.setBatchon(rs.getString("Batchno"));
			data.setBatchdate(rs.getString("toeffectivedate"));
			data.setFlag(rs.getInt("Flag"));//是否近效期 1=是 0=未到近效期
			data.setSaleflag(rs.getInt("SaleFlag"));//是否可售1=可售0=禁止销售
			result.add(data);
		}
		rs.close();
		return result;

	}

	public ArrayList getBatchExtList(Connection connection, PosPriceData code) {

		ArrayList result = null;

		try {
			result = getBatchnolistWithException(connection, code);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMsg1 = "数据库查询错误!";
			errorMsg2 = e.getMessage();
		}

		return result;
	}
}
