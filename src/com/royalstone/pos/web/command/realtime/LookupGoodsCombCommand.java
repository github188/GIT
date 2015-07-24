package com.royalstone.pos.web.command.realtime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * ����˴��룬�����ѯ��Ʒ��Ϣ
 * @author liangxinbiao
 */
public class LookupGoodsCombCommand implements ICommand {

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
				errorMsg1 = "���ݿ����Ӵ���!";
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
	 * ������Ʒ���������ӱ�price_lst���ѯ����Ʒ��Ϣ
	 * @param connection �����ݿ������
	 * @param code ��Ʒ���������
	 * @return ��Ʒ��Ϣ
	 */
	public Goods lookup(Connection connection, String code) {

		Goods result = null;

		try {
			result = lookupWithException(connection, code);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMsg1 = "���ݿ��ѯ����!";
			errorMsg2 = e.getMessage();
		}

		return result;
	}

	public Goods lookupWithException(Connection connection, String code)
		throws SQLException {
			
		Goods result = null;

//		PreparedStatement pstmt =
//			connection.prepareStatement(
//				"  SELECT vgno, goodsno, gname, uname, spec, price, x FROM goodscombine where goodsno=?; ");
		// ��ϱ�����Ʒ����Ч�ڽ����ж� 20080116 zhouzhou Add 
		PreparedStatement pstmt =
		connection.prepareStatement(
			"  SELECT vgno, goodsno, gname, uname, spec, price, x FROM goodscombine where goodsno=? and convert(char(8),getDate(),112) between convert(int,(convert(char(8),begindate,112))) and convert(int,(convert(char(8),enddate,112))); ");

		pstmt.setString(1, code);

		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			String vgno = rs.getString("vgno");
				String goodsno = rs.getString("goodsno");
				String gname = rs.getString("gname");
				String deptno = "";
				String spec = rs.getString("spec");
				String uname = rs.getString("uname");
				String v_type = "";
				String p_type = "n";
				double price = rs.getDouble("price");
				int x = rs.getInt("x");
				int goods_type = 0;
				int i_price = (int) Math.rint(price * 100);
				if (spec == null)
					spec = "";
				if (uname == null)
					uname = "";

			result =
				new Goods(vgno,
						goodsno,
						gname,
						deptno,
						spec,
						uname,
						i_price,
						goods_type,
						x,
						p_type,
						"20070101"); 	// �����Ʒ

		}
		rs.close();
		
		return result;
	}

}