package com.royalstone.pos.web.command;

import com.royalstone.pos.card.SHCardPayVO;
import com.royalstone.pos.web.util.DBConnection;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

/**
 * 服务端代码，调用相应的Ejb完成储值卡的支付
 * @author liangxinbiao
 */
public class SHCardAutoReverServletCommand implements ICommand {
    private static int BIGDECIMAL_SCALE = 5;
	public Object[] excute(Object[] values) {

		if (values.length == 2
			&& (values[1] instanceof SHCardPayVO)) {
			try {
				SHCardPayVO payVO = (SHCardPayVO) values[1];

				String result = null;

				result = autoRever(payVO);

				Object[] results = new Object[1];

				results[0] = result;

				return results;

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return null;
	}

    private String autoRever(SHCardPayVO cardpay){
       String result = null;
	   Connection conn = null;
        Statement state = null;
        ResultSet rs = null;


		String cardno = cardpay.getCardno();
		String shopID = cardpay.getShopid();
		String cashierID = cardpay.getCashierid();
		String posID = cardpay.getPosid();
		String time = cardpay.getTime();
		String pay_value = cardpay.getPayvalue();
		BigDecimal _pay_value = new BigDecimal(pay_value);
		_pay_value.setScale(BIGDECIMAL_SCALE, BigDecimal.ROUND_HALF_UP);
		String passwd = cardpay.getPassword();
		String cdseq = cardpay.getCdseq();

		String sql = "";
		String sub_sql =
			"CardNo='"
				+ cardno
				+ "' and ShopID='"
				+ shopID
				+ "' and BranchNo='"
				+ posID
				+ "' and "
				+ "CashierNo='"
				+ cashierID
				+ "' and ReqTime='"
				+ time
				+ "' and CDSeq="
				+ cdseq;
//				+ " and "
//				+ "Convert(char(8),PurchDateTime,112)=Convert(char(8),getdate(),112) and stat=0";
		int updateCount = 0;

		try {

			conn = DBConnection.getConnection("java:comp/env/dbcards");

			state = conn.createStatement();
			rs =
				state.executeQuery(
					"select count(*) from GuestPurch0 where " + sub_sql);
			rs.next();

			if (rs.getInt(1) == 0) {
				return "1";
			}

			if (rs != null)
				rs.close();
			rs =
				state.executeQuery(
					"select Detail,PayMoney from GuestPurch0 where " + sub_sql);
			rs.next();

			String detail = mytrim(rs.getString(1));
			BigDecimal _detail = rs.getBigDecimal(1);
			String paymoney = mytrim(rs.getString(2));
			BigDecimal _paymoney = rs.getBigDecimal(2);

			sql =
				"update Guest set Detail=Detail+"
					+ paymoney
					+ ",PayMoney=PayMoney+"
					+ Double.parseDouble(paymoney)*-1
					+ ",times=times-1 where CardNo='"
					+ cardno
					+ "'";

			conn.setAutoCommit(false);

			updateCount = state.executeUpdate(sql);

			sql = "update GuestPurch0 set Stat='1' where " + sub_sql;

			updateCount = state.executeUpdate(sql);

			sql =
				"insert into GuestPurch0(CardNo,PayMoney,Detail,ShopID,ReqTime,CDSeq,BranchNo,CashierNo,Stat,Point) values('"
					+ cardno
					+ "',"
					+ paymoney
					+ ","
					+ detail
					+ ",'"
					+ shopID
					+ "','"
					+ time
					+ "','"
					+ cdseq
					+ "','"
					+ posID
					+ "','"
					+ cashierID
					+ "','3',0)";

			updateCount = state.executeUpdate(sql);

			conn.commit();

			return "1";

		} catch (Exception ex) {
			try {
				if (!conn.getAutoCommit())
					conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "储值卡交易冲正错误! ";
		} finally {
			DBConnection.closeAll(rs, state, conn);
		}
    }

	/**
	 * 清除字符串的前后空格
	 * @param str
	 * @return
	 */
	private String mytrim(String str) {
		if (str == null) {
			return "";
		} else {
			return str.trim();
		}
	}
}
