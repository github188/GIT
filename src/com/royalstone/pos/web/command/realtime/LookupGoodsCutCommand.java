package com.royalstone.pos.web.command.realtime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.royalstone.pos.common.GoodsCut;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，负责查询商品信息
 * @author liangxinbiao
 */
public class LookupGoodsCutCommand implements ICommand {

	private String errorMsg1;
	private String errorMsg2;

	/**
	 * @see ICommand#excute(Object[])
	 */
	public Object[] excute(Object[] values) {

		if (values != null
			&& values.length == 2
			&& (values[1] instanceof String)) {

			Connection con = null;

			try {
				con = DBConnection.getConnection("java:comp/env/dbpos");

				Object[] result = new Object[3];
				result[0] = lookup(con, (String) values[1]);
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
	 * 根据商品编码或条码从表price_lst里查询出商品信息
	 * @param connection 到数据库的连接
	 * @param code 商品编码或条码
	 * @return 商品信息
	 */
	public GoodsCut lookup(Connection connection, String code) {

		GoodsCut result = null;

		try {
			result = lookupWithException(connection, code);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMsg1 = "数据库查询错误!";
			errorMsg2 = e.getMessage();
		}

		return result;
	}

	public GoodsCut lookupWithException(Connection connection, String code)
		throws SQLException {
			
		GoodsCut result = null;

		PreparedStatement pstmt =
			connection.prepareStatement(
					"SELECT  a.vgno, a.goodsno, a.gname, a.uname, a.spec, a.price, a.x, a.qty, a.curqty,b.deptno,Max(c.BatchNo) as batchno   FROM goodscut a, price_lst b, MySHOPSHStock..Shopbatchno c " +
					 "where a.goodsno=? and a.vgno = b.vgno and c.GoodsID = b.vgno  group by a.vgno, a.goodsno, a.gname, a.uname, a.spec, a.price, a.x, a.qty, a.curqty,b.deptno;" );	
		
		
					
//				" SELECT vgno, goodsno, gname, uname, spec, price, x, qty, curqty FROM goodscut where goodsno=?; ");

		pstmt.setString(1, code);

		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			String vgno = Formatter.mytrim(rs.getString("vgno"));
			String goodsno = Formatter.mytrim(rs.getString("goodsno"));
			String gname = Formatter.mytrim(rs.getString("gname"));
			String spec = Formatter.mytrim(rs.getString("spec"));
			String uname = Formatter.mytrim(rs.getString("uname"));
			String deptno = Formatter.mytrim(rs.getString("deptno"));
			String batchno = Formatter.mytrim(rs.getString("BatchNo")); // zhouzhou add 批次号
			int qty = rs.getInt("qty");
			int  curqty = rs.getInt("curqty");
			double price = rs.getDouble("price");
			int x = rs.getInt("x");
			int i_price = (int) Math.rint(price * 100);
			if (spec == null)
				spec = "";
			if (uname == null)
				uname = "";
			if (batchno == null)
				batchno = "20070101";

			result =
				new GoodsCut(
					vgno,
					goodsno,
					gname,
                    uname,
					spec,
                    i_price,
					x,
					qty,
					curqty,
					deptno,
					batchno);

		}
		rs.close();
		
		return result;
	}

}
