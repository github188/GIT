package com.royalstone.pos.web.command.realtime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.royalstone.pos.common.GoodsExt;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，负责查询一品多码的商品信息
 * @author liangxinbiao
 */
public class LookupGoodsExtCommand implements ICommand {

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
	 * 根据商品条码从表goodsext6和price_lst里查询商品的扩展信息
	 * @param connection 到数据库的连接
	 * @param code 商品条码
	 * @return 商品的扩展信息
	 */
	public GoodsExt lookup(Connection connection, String code) {

		GoodsExt result = null;
		try {
			result = lookupWithException(connection, code);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMsg1 = "数据库查询错误!";
			errorMsg2 = e.getMessage();
		}

		return result;
	}

	public GoodsExt lookupWithException(Connection connection, String code)
		throws SQLException {

		GoodsExt result = null;

//		PreparedStatement pstmt =
//			connection.prepareStatement(
//				"SELECT price_lst.vgno, goodsext6.goodsno, goodsext6.gname, price_lst.deptno, price_lst.spec, price_lst.uname, "
//					+ "price_lst.v_type, price_lst.p_type, goodsext6.price,price_lst.price as pricex, price_lst.x, pknum "
//					+ "FROM price_lst,goodsext6 where goodsext6.vgno=price_lst.vgno and goodsext6.goodsno=?");
		
		PreparedStatement pstmt =
			connection.prepareStatement(
				"SELECT a.vgno, b.goodsno, b.gname, a.deptno, a.spec, a.uname, "
					+ "a.v_type, a.p_type, b.price,a.price as pricex, a.x, b.pknum, Max(c.BatchNo) as batchno "
					+ "FROM price_lst a,goodsext6 b, myshopshstock..shopbatchno c where b.vgno=a.vgno and b.goodsno=? and a.vgno = c.goodsid "
					+ "group by a.vgno, b.goodsno, b.gname, a.deptno, a.spec, a.uname,a.v_type, a.p_type, b.price,a.price, a.x, b.pknum ");

		pstmt.setString(1, code);

		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String vgno = Formatter.mytrim(rs.getString("vgno"));
			String goodsno = Formatter.mytrim(rs.getString("goodsno"));
			String gname = Formatter.mytrim(rs.getString("gname"));
			String deptno = Formatter.mytrim(rs.getString("deptno"));
			String spec = Formatter.mytrim(rs.getString("spec"));
			String uname = Formatter.mytrim(rs.getString("uname"));
			String v_type = Formatter.mytrim(rs.getString("v_type"));
			String p_type = Formatter.mytrim(rs.getString("p_type"));
			String batchno = Formatter.mytrim(rs.getString("batchno"));
			double price = rs.getDouble("price");
			if (price <= 0.00)
				price = rs.getDouble("pricex");
			int x = rs.getInt("x");
			int goods_type = Integer.parseInt(v_type);
			int i_price = (int) Math.rint(price * 100);
			if (spec == null)
				spec = "";
			if (uname == null)
				uname = "";
			int pknum = rs.getInt("pknum");
			if (batchno == null)
				batchno = "20070101";

			result =
				new GoodsExt(
					vgno,
					goodsno,
					gname,
					deptno,
					spec,
					uname,
					i_price,
					goods_type,
					x,
					p_type,
					pknum,
					batchno);
		}
		rs.close();

		return result;

	}

	public ArrayList getGoodsExtListWithException(
		Connection connection,
		String code)
		throws SQLException {

		ArrayList result = new ArrayList();

//		PreparedStatement pstmt =
//			connection.prepareStatement(
//				"SELECT price_lst.vgno, goodsext6.goodsno, price_lst.gname, price_lst.deptno, price_lst.spec, price_lst.uname, "
//					+ "price_lst.v_type, price_lst.p_type, price_lst.price, price_lst.x, pknum "
//					+ "FROM price_lst,goodsext6 where goodsext6.vgno=price_lst.vgno and price_lst.vgno=?");
		
		PreparedStatement pstmt =
			connection.prepareStatement(
				"SELECT a.vgno, b.goodsno, a.gname, a.deptno, a.spec, a.uname, "
					+ "a.v_type, a.p_type, a.price, a.x, b.pknum, Max(c.BatchNo) as batchno "
					+ "FROM price_lst a ,goodsext6 b, myshopshstock..shopbatchno c  where b.vgno=a.vgno and a.vgno=? and a.vgno = c.goodsid"
					+ "group by a.vgno, b.goodsno, a.gname, a.deptno, a.spec, a.uname,a.v_type, a.p_type, b.price,a.price, a.x, b.pknum ");

		pstmt.setString(1, code);

		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String vgno = Formatter.mytrim(rs.getString("vgno"));
			String goodsno = Formatter.mytrim(rs.getString("goodsno"));
			String gname = Formatter.mytrim(rs.getString("gname"));
			String deptno = Formatter.mytrim(rs.getString("deptno"));
			String spec = Formatter.mytrim(rs.getString("spec"));
			String uname = Formatter.mytrim(rs.getString("uname"));
			String v_type = Formatter.mytrim(rs.getString("v_type"));
			String p_type = Formatter.mytrim(rs.getString("p_type"));
			double price = rs.getDouble("price");
			String batchno = Formatter.mytrim(rs.getString("batchno"));
			int x = rs.getInt("x");
			int goods_type = Integer.parseInt(v_type);
			int i_price = (int) Math.rint(price * 100);
			if (spec == null)
				spec = "";
			if (uname == null)
				uname = "";
			int pknum = rs.getInt("pknum");
			if (batchno == null)
				batchno = "20070101";

			result.add(
				new GoodsExt(
					vgno,
					goodsno,
					gname,
					deptno,
					spec,
					uname,
					i_price,
					goods_type,
					x,
					p_type,
					pknum,
					batchno));
		}
		rs.close();
		return result;

	}

	public ArrayList getGoodsExtList(Connection connection, String code) {

		ArrayList result = null;

		try {
			result = getGoodsExtListWithException(connection, code);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMsg1 = "数据库查询错误!";
			errorMsg2 = e.getMessage();
		}

		return result;
	}
}
