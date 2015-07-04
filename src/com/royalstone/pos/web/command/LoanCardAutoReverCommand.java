package com.royalstone.pos.web.command;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.royalstone.pos.card.LoanCardPayVO;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，完成挂账卡的自动冲正
 * @author liangxinbiao
 */
public class LoanCardAutoReverCommand implements ICommand {

	private static int BIGDECIMAL_SCALE = 5;

	/**
	 * @see com.royalstone.pos.web.command.ICommand#excute(java.lang.Object[])
	 */
	public Object[] excute(Object[] values) {

		if (values.length == 2 && (values[1] instanceof LoanCardPayVO)) {

			Object[] results = new Object[1];
			results[0] = autoRever((LoanCardPayVO) values[1]);
			return results;

		}

		return null;
	}

	/**
	 * 根据挂帐卡支付的值对象完成自动冲正
	 * @param cardpay  挂帐卡支付的值对象
	 * @return 错误信息,"1"代表成功
	 */
	public String autoRever(LoanCardPayVO cardpay) {

		Connection conn = null;
		Statement state = null;
		ResultSet rs = null;

		String cardno = cardpay.getCardno();
		String shopID = cardpay.getShopid();
		String cashierID = cardpay.getCashierid();
		String posID = cardpay.getPosid();
		String sheetID = cardpay.getSheetid();
		String time = cardpay.getTime();
		String pay_value = cardpay.getPayvalue();
		BigDecimal _pay_value = new BigDecimal(pay_value);
		_pay_value.setScale(BIGDECIMAL_SCALE, BigDecimal.ROUND_HALF_UP);
		String passwd = cardpay.getPassword();
		String cdseq = cardpay.getCdseq();

		try {
			
			conn = DBConnection.getConnection("java:comp/env/dbcard");

			if (cardpay.getSubcardno() == null
				|| cardpay.getSubcardno().trim().equals("")) { //主卡

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
						+ cdseq
						+ " and "
						+ "Convert(char(8),PurchDateTime,112)=Convert(char(8),getdate(),112) and stat=0";
				int updateCount = 0;

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
						"select Detail,PayMoney from GuestPurch0 where "
							+ sub_sql);
				rs.next();

				String detail = mytrim(rs.getString(1));
				BigDecimal _detail = rs.getBigDecimal(1);
				String paymoney = mytrim(rs.getString(2));
				BigDecimal _paymoney = rs.getBigDecimal(2);

				sql =
					"update Guest set Detail=Detail+"
						+ paymoney
						+ ",PayMoney=PayMoney-"
						+ paymoney
						+ ",times=times-1 where CardNo='"
						+ cardno
						+ "'";

				conn.setAutoCommit(false);

				updateCount = state.executeUpdate(sql);

				sql = "update GuestPurch0 set Stat='1' where " + sub_sql;

				updateCount = state.executeUpdate(sql);

				sql =
					"insert into GuestPurch0(CardNo,PayMoney,Detail,ShopID,ReqTime,CDSeq,BranchNo,CashierNo,ListNO,Stat,Point) values('"
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
						+ "','"
						+ sheetID
						+ "','3',0)";

				updateCount = state.executeUpdate(sql);

				conn.commit();

				return "1";
			} else { //子卡
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
						+ cdseq
						+ " and creditsubcardno='"
						+ cardpay.getSubcardno()
						+ "' and "
						+ "Convert(char(8),PurchDateTime,112)=Convert(char(8),getdate(),112) and stat=0";
				int updateCount = 0;

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
						"select Detail,PayMoney from GuestPurch0 where "
							+ sub_sql);
				rs.next();

				String detail = mytrim(rs.getString(1));
				BigDecimal _detail = rs.getBigDecimal(1);
				String paymoney = mytrim(rs.getString(2));
				BigDecimal _paymoney = rs.getBigDecimal(2);

				sql =
					"update Guest set Detail=Detail+"
						+ paymoney
						+ ",PayMoney=PayMoney-"
						+ paymoney
						+ ",times=times-1 where CardNo='"
						+ cardno
						+ "'";

				conn.setAutoCommit(false);

				updateCount = state.executeUpdate(sql);

				sql =
					"update creditsubcard set balance=balance+"
						+ paymoney
						+ ",PayMoney=PayMoney-"
						+ paymoney
						+ " where creditsubcardno='"
						+ cardpay.getSubcardno()
						+ "'";

				updateCount = state.executeUpdate(sql);

				sql = "update GuestPurch0 set Stat='1' where " + sub_sql;

				updateCount = state.executeUpdate(sql);

				sql =
					"insert into GuestPurch0(CardNo,PayMoney,Detail,ShopID,ReqTime,CDSeq,BranchNo,CashierNo,ListNO,Stat,Point,creditsubcardno) values('"
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
						+ "','"
						+ sheetID
						+ "','3',0,'"
						+ cardpay.getSubcardno()
						+ "')";

				updateCount = state.executeUpdate(sql);

				ResultSet rs2 =
					state.executeQuery(
						"select memberID,detail,point from guest where cardno='"
							+ cardpay.getCardno()
							+ "'");

				String memberID = null;
				double detail_d = 0;
				double point = 0;
				if (rs2.next()) {
					memberID = mytrim(rs2.getString("memberID"));
					if (memberID != null) {
						memberID = "'" + memberID + "'";
					} else {
						memberID = "NULL";
					}
					detail_d = rs2.getDouble("detail");
					point = rs2.getDouble("point");
				} else {
					conn.rollback();
					DBConnection.closeAll(rs, state, conn);
					return "出现错误,事务回滚";
				}

				sql =
					"insert into CardAcc0(CardNO,creditsubcardno,MemberID,ShopID,DirectFlag,Value,ResultValue,Point,ResultPoint,SheetID,SheetType,Note,OccurDate) values('"
						+ cardpay.getCardno()
						+ "','"
						+ cardpay.getSubcardno()
						+ "',"
						+ memberID
						+ ",'"
						+ cardpay.getShopid()
						+ "',1,"
						+ paymoney
						+ ","
						+ Double.toString(detail_d)
						+ ",0,"
						+ Double.toString(point)
						+ ",'',559002,'挂账卡冲正',getdate())";

				state.executeUpdate(sql);

				conn.commit();

				return "1";
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				if (!conn.getAutoCommit())
					conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "挂账卡交易冲正错误! ";
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
