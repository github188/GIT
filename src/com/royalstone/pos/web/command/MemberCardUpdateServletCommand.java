package com.royalstone.pos.web.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.royalstone.pos.card.MemberCardUpdate;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，调用相应的Ejb完成储值卡的支付
 * @author liangxinbiao
 */
public class MemberCardUpdateServletCommand implements ICommand {

	public Object[] excute(Object[] values) {

		if (values.length == 2
			&& (values[1] instanceof MemberCardUpdate)) {
			try {
				MemberCardUpdate updateVO = (MemberCardUpdate) values[1];

				String result = null;

				result = pay(updateVO);

				Object[] results = new Object[1];

				results[0] = result;

				return results;

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return null;
	}

    private String pay(MemberCardUpdate cardUpdate){
       String result = null;
	   Connection conn = null;
        Statement state = null;
        ResultSet rs = null;
        Statement statePoint = null;
        ResultSet rsPoint = null;
        int isRealtimePoint=0; //是否实时积分 ，0否  1 是
        String sqlPoint="select value from config "
						+ "where name ='是否实时积分'";


		if (cardUpdate.getCardno() == null
			|| cardUpdate.getCardno().trim().equals("")) {
			result = "卡号或密码为空";
			return result;
		}
		try {
			
			conn = DBConnection.getConnection("java:comp/env/dbcard");
			
        //------查询是否实时积分参数－－－

           statePoint = conn.createStatement();
           rsPoint= statePoint.executeQuery(sqlPoint);
            if(rsPoint.next()){
                if("是".equals(rsPoint.getString("value")))
                   isRealtimePoint=1;
                else
                   isRealtimePoint=0;
            }else
                   isRealtimePoint=0;

        //---------------------



			state = conn.createStatement();
			rs =
				state.executeQuery(
					"select mode,detail from guest "
						+ "where cardno = '"
						+ cardUpdate.getCardno().trim()
						+ "'");
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
					return "该卡" + result;
				}
				try {
					conn.setAutoCommit(false);
					DBConnection.closeAll(rs, state, null);
					state = conn.createStatement();
					String sql = null;
					sql =
						"insert into guestpurch0(cardno,paymoney,detail,shopid,reqtime,cdseq,branchno,cashierno,stat,point,ListNo) values('"
							+ cardUpdate.getCardno()
							+ "',"
							+ cardUpdate.getPayvalue()
							+ ",'0','"
							+ cardUpdate.getShopid()
							+ "','"
							+ cardUpdate.getTime()
							+ "','"
							+ cardUpdate.getCdseq()
							+ "','"
							+ cardUpdate.getPosid()
							+ "','"
							+ cardUpdate.getCashierid()
							+ "','0','"
                            +cardUpdate.getCourrentPoint()
							+"',"
							+cardUpdate.getListno() + ")";
					state.executeUpdate(sql);
                    if(isRealtimePoint==1)
					  sql =
						"update guest set paymoney=paymoney+"
							+ cardUpdate.getPayvalue()
							+ ",times=times+1,lastusedate=getdate(),"
							+ "lastshopid='"
							+ cardUpdate.getShopid()
							+ "',lastposid='"
							+ cardUpdate.getPosid()
							+ "', LastCashierID='"
							+ cardUpdate.getCashierid()
                            + "',Point='"
							+ cardUpdate.getPoint()
							+ "' where cardNo='"
							+ cardUpdate.getCardno()
							+ "'";
                    else
                         sql =
						"update guest set paymoney=paymoney+"
							+ cardUpdate.getPayvalue()
							+ ",times=times+1,lastusedate=getdate(),"
							+ "lastshopid='"
							+ cardUpdate.getShopid()
							+ "',lastposid='"
							+ cardUpdate.getPosid()
							+ "', LastCashierID='"
							+ cardUpdate.getCashierid()
                            + "' where cardNo='"
							+ cardUpdate.getCardno()
							+ "'";
					state.executeUpdate(sql);
					
//会员卡消费不写cardacc0					
//					sql =
//						"exec InsertCardAcc '"
//							+ cardUpdate.getCardno()
//							+ "','"
//							+ cardUpdate.getShopid()
//							+ "',-1,"
//							+ cardUpdate.getPayvalue()
//							+ ","+cardUpdate.getPoint()+",'',559001,'会员卡消费'";
//					state.executeUpdate(sql);


					conn.commit();
					result = "1";
					conn.setAutoCommit(true);
				} catch (SQLException ex) {
					ex.printStackTrace();
					conn.rollback();
					DBConnection.closeAll(rs, state, conn);
					return "出现错误,事务回滚";
				}
			} else {
				result = "无此卡号";
				DBConnection.closeAll(rs, null, null);
				return result;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
            if(rsPoint!=null)
                try {
                    rsPoint.close();
                } catch (SQLException e) {}
            if(statePoint!=null)
                try {
                    statePoint.close();
                } catch (SQLException e) {}
			DBConnection.closeAll(rs, state, conn);
		}
		return result;
    }

}
