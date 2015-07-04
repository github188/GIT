package com.royalstone.pos.web.command.realtime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，负责查询商品信息
 */
public class LookupGoodsCommandTwo implements ICommand {

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
				long start = System.currentTimeMillis();
				con = DBConnection.getConnection("java:comp/env/dbpos");
				System.out.println(
					"GetConnection = " + (System.currentTimeMillis() - start));

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
	public Goods lookup(Connection connection, String code) {

		Goods result = null;

		try {
			result = lookupWithException(connection, code);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMsg1 = "数据库查询错误!";
			errorMsg2 = e.getMessage();
		}

		return result;
	}

	public Goods lookupWithException(Connection connection, String code)
		throws SQLException {

		long start = System.currentTimeMillis();

		Goods result = null;

		String sql = null;

		if (code.length() == 6) {
			sql =				
				"SELECT vgno, goodsno, gname, deptno, spec, uname,  v_type, p_type, price, x, Max(b.BatchNo) as batchno  FROM price_lst a , myshopshstock..shopbatchno b ,vipprice_lstview c  where vgno=? and a.vgno*= b.GoodsID and  a.vgno=c.goodsid " + 
				"GROUP BY vgno, goodsno, gname, deptno, spec, uname,  v_type, p_type, price, x; ";
		} else {
			sql =				
				"SELECT vgno, goodsno, gname, deptno, spec, uname,  v_type, p_type, price, x, Max(b.BatchNo) as batchno  FROM price_lst a , myshopshstock..shopbatchno b,vipprice_lstview c    where goodsno=? and a.vgno*= b.GoodsID and a.vgno=c.goodsid " + 
				"GROUP BY vgno, goodsno, gname, deptno, spec, uname,  v_type, p_type, price, x; ";

		}

		PreparedStatement pstmt = connection.prepareStatement(sql);

		pstmt.setString(1, code);

		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
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
			int x = rs.getInt("x");
			int goods_type = Integer.parseInt(v_type);
			int i_price = (int) Math.rint(price * 100);
			if (spec == null)
				spec = "";
			if (uname == null)
				uname = "";
			if (batchno == null)
				batchno = "20070101";

			result =
				new Goods(
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
					batchno);

		}
		rs.close();

		System.out.println("DBinner = " + (System.currentTimeMillis() - start));

		return result;
	}

}
