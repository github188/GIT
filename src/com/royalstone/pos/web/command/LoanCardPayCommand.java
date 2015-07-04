package com.royalstone.pos.web.command;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.royalstone.pos.card.LoanCardPayVO;
import com.royalstone.pos.util.Value;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，完成挂账卡的支付
 * @author liangxinbiao
 */
public class LoanCardPayCommand implements ICommand {

	/**
	 * @see com.royalstone.pos.web.command.ICommand#excute(java.lang.Object[])
	 */
	public Object[] excute(Object[] values) {
		if (values.length == 2 && (values[1] instanceof LoanCardPayVO)) {

			Object[] results = new Object[1];
			results[0] = pay((LoanCardPayVO) values[1]);
			return results;

		}

		return null;
	}

	/**
	 * 根据挂帐卡支付的值对象完成挂账卡的支付
	 * @param cardpay 挂帐卡支付的值对象
	 * @return 错误信息,"1"代表成功
	 */
	private String pay(LoanCardPayVO cardpay) {

		String result = null;
		Statement state = null;
		ResultSet rs = null;

		if ((cardpay.getCardno() == null
			|| cardpay.getCardno().trim().equals("")
			&& (cardpay.getSubcardno() == null
				|| cardpay.getSubcardno().trim().equals("")))
			|| cardpay.getPassword() == null
			|| cardpay.getPassword().trim().equals("")) {
			result = "卡号或密码为空";
			return result;
		}
		
		Connection conn=null;
		
		try {
			
			conn = DBConnection.getConnection("java:comp/env/dbcard");
				
			if (cardpay.getSubcardno() == null
				|| cardpay.getSubcardno().trim().equals("")) { //主卡
				state = conn.createStatement();
				rs =
					state.executeQuery(
						"select count(*) as counts from guest where cardno = '"
							+ cardpay.getCardno().trim()
							+ "'");
				rs.next();
				if (rs.getInt("counts") == 0) {
					result = "无此卡号";
					DBConnection.closeAll(rs, null, null);
					return result;
				}
				DBConnection.closeAll(rs, null, null);
				rs =
					state.executeQuery(
						"select mode,detail,credit from guest "
							+ "where cardno = '"
							+ cardpay.getCardno().trim()
							+ "' and secrety = '"
							+ cardpay.getPassword().trim()
							+ "'");
				if (rs.next()) {
					java.math.BigDecimal rsDetail = rs.getBigDecimal("detail");
					rsDetail.add(rs.getBigDecimal("credit"));
					rsDetail.setScale(5, BigDecimal.ROUND_HALF_UP);
					java.math.BigDecimal payvalue =
						new java.math.BigDecimal(
							(new Value(Integer
								.parseInt(cardpay.getPayvalue())))
								.toString());
					payvalue.setScale(5, BigDecimal.ROUND_HALF_UP);
					if (rsDetail.compareTo(payvalue) < 0) {
						result = "支付额不够";
						DBConnection.closeAll(rs, state, conn);
						return result;
					}
					String mode = (rs.getString("mode")).trim();
					if (!mode.equals("1")) {
						switch (mode.charAt(0)) {
							case '2' :
								result = "未到帐卡";
								break;
							case 'r' :
								result = "已回收卡";
								break;
							case 'm' :
								result = "一般挂失卡";
								break;
							case 'l' :
								result = "严重挂失卡";
								break;
							case 'f' :
								result = "冻结";
								break;
							case 'e' :
								result = "已换卡";
								break;
							case 'q' :
								result = "退卡";
								break;
							default :
								result = "其他错误";
								break;
						}
						DBConnection.closeAll(rs, state, conn);
						return "该卡" + result;
					}
					try {
						String sql = null;
						ResultSet rs1 =
							state.executeQuery(
								"select shopid from creditclient where cardno='"
									+ cardpay.getCardno()
									+ "'");
						if (rs1.next()) {
							if (mytrim(rs1.getString("shopid"))
								.equals(cardpay.getShopid())) {
								conn.setAutoCommit(false);
								DBConnection.closeAll(rs, state, null);
								state = conn.createStatement();
								sql = null;

								sql =
									"update guest set paymoney=paymoney+"
										+ (new Value(Integer
											.parseInt(cardpay.getPayvalue())))
											.toString()
										+ ",detail=detail-"
										+ (new Value(Integer
											.parseInt(cardpay.getPayvalue())))
											.toString()
										+ ",times=times+1,lastusedate=getdate(),"
										+ "lastshopid='"
										+ cardpay.getShopid()
										+ "',lastposid='"
										+ cardpay.getPosid()
										+ "', LastCashierID='"
										+ cardpay.getCashierid()
										+ "' where cardNo='"
										+ cardpay.getCardno()
										+ "'";
								state.executeUpdate(sql);

								rsDetail = rsDetail.subtract(payvalue);
								sql =
									"insert into guestpurch0(cardno,paymoney,detail,shopid,reqtime,cdseq,branchno,cashierno,listno,stat,point) values('"
										+ cardpay.getCardno()
										+ "',"
										+ (new Value(Integer
											.parseInt(cardpay.getPayvalue())))
											.toString()
										+ ","
										+ rsDetail.toString().trim()
										+ ",'"
										+ cardpay.getShopid()
										+ "','"
										+ cardpay.getTime()
										+ "','"
										+ cardpay.getCdseq()
										+ "','"
										+ cardpay.getPosid()
										+ "','"
										+ cardpay.getCashierid()
										+ "','"
										+ cardpay.getSheetid()
										+ "','0',0)";
								state.executeUpdate(sql);

								sql =
									"exec InsertCardAcc '"
										+ cardpay.getCardno()
										+ "','"
										+ cardpay.getShopid()
										+ "',-1,"
										+ (new Value(Integer
											.parseInt(cardpay.getPayvalue())))
											.toString()
										+ ",0,'',559001,'储值消费余额减少'";
								state.executeUpdate(sql);
								conn.commit();
								result = "1";
								conn.setAutoCommit(true);
							}
						} else {
							conn.rollback();
							DBConnection.closeAll(rs, state, conn);
							return "出现错误,事务回滚";
						}
					} catch (SQLException ex) {
						ex.printStackTrace();
						conn.rollback();
						DBConnection.closeAll(rs, state, conn);
						return "出现错误,事务回滚";
					}
				} else {
					result = "密码有误";
					DBConnection.closeAll(rs, state, conn);
					return result;
				}
				result = "1";
			} else { //子卡
				state = conn.createStatement();
				rs =
					state.executeQuery(
						"select cardno,creditsubcardno,balance,credit,status as mode,MaxOilQtyPerTrans,OilTypeID,carID from creditsubcard "
							+ "where (creditsubcardno = '"
							+ cardpay.getSubcardno().trim()
							+ "' and cardverifyCode = '"
							+ cardpay.getPassword().trim()
							+ "' and IsNeededVerify=1) or (creditsubcardno ='"
							+ cardpay.getSubcardno().trim()
							+ "' and IsNeededVerify=0) ");

				if (rs.next()) {
					String mode = (rs.getString("mode")).trim();
					if (!mode.equals("1")) {
						switch (mode.charAt(0)) {
							case '2' :
								result = "未到帐卡";
								break;
							case 'r' :
								result = "已回收卡";
								break;
							case 'm' :
								result = "一般挂失卡";
								break;
							case 'l' :
								result = "严重挂失卡";
								break;
							case 'f' :
								result = "冻结";
								break;
							case 'e' :
								result = "已换卡";
								break;
							case 'q' :
								result = "退卡";
								break;
							default :
								result = "其他错误";
								break;
						}
						DBConnection.closeAll(rs, state, conn);
						return result;
					}

					String mainCardNo = mytrim(rs.getString("cardno"));
					double subBalance =
						rs.getDouble("balance") + rs.getDouble("credit");
					
					ResultSet rs6 =
						state.executeQuery(
							"select mode from guest "
								+ "where cardno = '"
								+ mainCardNo.trim()
								+ "' ");

					if (rs6.next()) {
						mode = (rs6.getString("mode")).trim();
						if (!mode.equals("1")) {
							switch (mode.charAt(0)) {
								case '2' :
									result = "未到帐卡[主账户]";
									break;
								case 'r' :
									result = "已回收卡[主账户]";
									break;
								case 'm' :
									result = "一般挂失卡[主账户]";
									break;
								case 'l' :
									result = "严重挂失卡[主账户]";
									break;
								case 'f' :
									result = "冻结[主账户]";
									break;
								case 'e' :
									result = "已换卡[主账户]";
									break;
								case 'q' :
									result = "退卡[主账户]";
									break;
								default :
									result = "其他错误[主账户]";
									break;
							}
							DBConnection.closeAll(rs6, state, conn);
							return result;
						}
					}
					
					ResultSet rs3 =
						state.executeQuery(
							"select detail,credit from guest where cardno='"
								+ mainCardNo
								+ "'");

					if (rs3.next()) {
						double mainBalance =
							rs3.getDouble("detail") + rs3.getDouble("credit");

						if (subBalance > mainBalance)
							subBalance = mainBalance;

					}
					if(subBalance<0)subBalance=0;
					
					rs3.close();

					double payValue =
						Double.parseDouble(
							(new Value(Integer
								.parseInt(cardpay.getPayvalue())))
								.toValStr());

					if (subBalance < payValue) {
						result = "支付额不够";
						DBConnection.closeAll(rs, state, conn);
						return result;
					}

					String sql = null;
//					ResultSet rs1 =
//						state.executeQuery(
//							"select shopid from creditclient where cardno='"
//								+ cardpay.getCardno()
//								+ "'");
//					if (rs1.next()) {
//						if (mytrim(rs1.getString("shopid"))
//							.equals(cardpay.getShopid())) {

							conn.setAutoCommit(false);
							DBConnection.closeAll(rs, state, null);
							state = conn.createStatement();
							sql = null;
							sql =
								"insert into guestpurch0(cardno,paymoney,detail,shopid,reqtime,cdseq,branchno,cashierno,listno,stat,point,creditsubcardno) values('"
									+ cardpay.getCardno()
									+ "',"
									+ (new Value(Integer
										.parseInt(cardpay.getPayvalue())))
										.toValStr()
									+ ","
									+ Double.toString(subBalance - payValue)
									+ ",'"
									+ cardpay.getShopid()
									+ "','"
									+ cardpay.getTime()
									+ "','"
									+ cardpay.getCdseq()
									+ "','"
									+ cardpay.getPosid()
									+ "','"
									+ cardpay.getCashierid()
									+ "','"
									+ cardpay.getSheetid()
									+ "','0',0,'"
									+ cardpay.getSubcardno()
									+ "')";
							state.executeUpdate(sql);

							sql =
								"update guest set paymoney=paymoney+"
									+ (new Value(Integer
										.parseInt(cardpay.getPayvalue())))
										.toValStr()
									+ ",detail=detail-"
									+ (new Value(Integer
										.parseInt(cardpay.getPayvalue())))
										.toValStr()
									+ ",times=times+1,lastusedate=getdate(),"
									+ "lastshopid='"
									+ cardpay.getShopid()
									+ "',lastposid='"
									+ cardpay.getPosid()
									+ "', LastCashierID='"
									+ cardpay.getCashierid()
									+ "' where cardNo='"
									+ mainCardNo
									+ "'";
							state.executeUpdate(sql);

							sql =
								"update creditsubcard set paymoney=paymoney+"
									+ (new Value(Integer
										.parseInt(cardpay.getPayvalue())))
										.toValStr()
									+ ",balance=balance-"
									+ (new Value(Integer
										.parseInt(cardpay.getPayvalue())))
										.toValStr()
									+ ",lastmodiTime=getdate(),"
									+ "lastmodiOPID='"
									+ cardpay.getCashierid()
									+ "' where creditsubcardNo='"
									+ cardpay.getSubcardno()
									+ "'";
							state.executeUpdate(sql);

							ResultSet rs2 =
								state.executeQuery(
									"select memberID,detail,point from guest where cardno='"
										+ cardpay.getCardno()
										+ "'");

							String memberID = null;
							double detail = 0;
							double point = 0;
							if (rs2.next()) {
								memberID = mytrim(rs2.getString("memberID"));
								if (memberID != null) {
									memberID = "'" + memberID + "'";
								} else {
									memberID = "NULL";
								}
								detail = rs2.getDouble("detail");
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
									+ "',-1,"
									+ (new Value(Integer
										.parseInt(cardpay.getPayvalue())))
										.toValStr()
									+ ","
									+ Double.toString(detail)
									+ ",0,"
									+ Double.toString(point)
									+ ",'',559001,'储值消费余额减少',getdate())";

							state.executeUpdate(sql);
							conn.commit();
							result = "1";
//						}
//					} else {
//						conn.rollback();
//						DBConnection.closeAll(rs, state, conn);
//						return "出现错误,事务回滚";
//					}
//
//					result = "1";
//					DBConnection.closeAll(rs, state, conn);
//					return result;
				}
				 else {
					result = "密码有误";
					DBConnection.closeAll(rs, state, conn);
					return result;
				}
			}

		} catch (SQLException se) {
			se.printStackTrace();
			result="数据库操作有误";
		} finally {
			DBConnection.closeAll(rs, state, conn);
		}
		return result;
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
