package com.royalstone.pos.web.command.realtime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.data.PosPriceData;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，负责查询商品信息
 * @author liangxinbiao
 */
public class LookupGoodsCommand implements ICommand {

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
				long start = System.currentTimeMillis();
				con = DBConnection.getConnection("java:comp/env/dbpos");
				System.out.println(
					"GetConnection = " + (System.currentTimeMillis() - start));

				Object[] result = new Object[3];
				result[0] = lookup(con, (PosPriceData) values[1]);
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
	public Goods lookup(Connection connection, PosPriceData code) {

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

	public Goods lookupWithException(Connection connection, PosPriceData code)
		throws SQLException {

		long start = System.currentTimeMillis();

		Goods result = null;

		String sql = null;
    if(code.getFlag()==0){
		if (code.getSaleCode().length() == 6) {
			// zhouzhou add 20070405
			sql =
				"SELECT a.vgno, a.goodsno, a.gname, deptno, a.spec, uname,  v_type, p_type, price, x,cflag,b.manufacturer  FROM price_lst a,myshopshstock..goods b where a.vgno*=b.goodsid and vgno=?; ";
				
//				"SELECT vgno, goodsno, gname, deptno, spec, uname,  v_type, p_type, price, x, Max(b.BatchNo) as batchno  FROM price_lst a , myshopshstock..shopbatchno b   where vgno=? and a.vgno*= b.GoodsID " + 
//				"GROUP BY vgno, goodsno, gname, deptno, spec, uname,  v_type, p_type, price, x; ";

		} else {

			sql =
				"SELECT a.vgno, a.goodsno, a.gname, deptno, a.spec, uname,  v_type, p_type, price, x,cflag,b.manufacturer  FROM price_lst a,myshopshstock..goods b where a.vgno*=b.goodsid and goodsno=?; ";
				
//				"SELECT vgno, goodsno, gname, deptno, spec, uname,  v_type, p_type, price, x, Max(b.BatchNo) as batchno  FROM price_lst a , myshopshstock..shopbatchno b   where goodsno=? and a.vgno*= b.GoodsID " + 
//				"GROUP BY vgno, goodsno, gname, deptno, spec, uname,  v_type, p_type, price, x; ";

		}

//		sql = " SELECT vgno, goodsno, gname, deptno, spec, uname,  v_type, p_type, price, x  FROM price_lst where vgno=?";

		PreparedStatement pstmt = connection.prepareStatement(sql);

		pstmt.setString(1, code.getSaleCode());
		//pstmt.setString(2, code);

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
			int cflag = rs.getInt("cflag");
			String manufact=Formatter.mytrim(rs.getString("manufacturer"));
		//	String batchno = Formatter.mytrim(rs.getString("batchno"));

			String batchno = Formatter.mytrim("20070101");
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
			if (manufact == null)
				manufact = "";
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
					batchno,
					cflag,
					manufact);

		}
		rs.close();
    }else{
		/*控制*/
		sql="select shopid ,goodsid,limitnum from goodsselllimit where ShopID=? and goodsid=? and " +
				"Convert(char(8),EndDate,112)>=Convert(char(8),getdate(),112) and " +
				"Convert(char(8),BeginDate,112)<=Convert(char(8),getdate(),112)";
		PreparedStatement pstmtnew = connection.prepareStatement(sql);

		pstmtnew.setString(1, code.getShopid());
		pstmtnew.setString(2, code.getSaleCode());
		//pstmtnew.setLong(3, code.getSaleAmount());
		
		ResultSet rsnew = pstmtnew.executeQuery();

		if(rsnew.next()){
			//判断数量
			String limitnumnew = Formatter.mytrim(rsnew.getString("limitnum"));
		    if(Integer.valueOf(limitnumnew)>=code.getSaleAmount())
		    {
		    	result = new Goods("1");
		    }else{
		    	result = new Goods("9");;  /*有限购传NULL对象*/
		    }
			
		}else{
			result = new Goods("0");
		}
		rsnew.close();
    }
		System.out.println("DBinner = " + (System.currentTimeMillis() - start));

		return result;
	}

}
