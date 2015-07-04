package com.royalstone.pos.web.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.royalstone.pos.card.MemberCardUpdate;
import com.royalstone.pos.web.util.DBConnection;

/**
 * ����˴��룬������Ӧ��Ejb��ɴ�ֵ����֧��
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
        int isRealtimePoint=0; //�Ƿ�ʵʱ���� ��0��  1 ��
        String sqlPoint="select value from config "
						+ "where name ='�Ƿ�ʵʱ����'";


		if (cardUpdate.getCardno() == null
			|| cardUpdate.getCardno().trim().equals("")) {
			result = "���Ż�����Ϊ��";
			return result;
		}
		try {
			
			conn = DBConnection.getConnection("java:comp/env/dbcard");
			
        //------��ѯ�Ƿ�ʵʱ���ֲ���������

           statePoint = conn.createStatement();
           rsPoint= statePoint.executeQuery(sqlPoint);
            if(rsPoint.next()){
                if("��".equals(rsPoint.getString("value")))
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
							result = "δ���ʿ�";
							break;
						case 'r' :
							result = "�ѻ��տ�";
							break;
						case 'm' :
							result = "һ���ʧ��";
							break;
						case 'l' :
							result = "���ع�ʧ��";
							break;
						case 'f' :
							result = "����";
							break;
						case 'e' :
							result = "�ѻ���";
							break;
						case 'q' :
							result = "�˿�";
							break;
						default :
							result = "��������";
							break;
					}
					DBConnection.closeAll(rs, state, conn);
					return "�ÿ�" + result;
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
					
//��Ա�����Ѳ�дcardacc0					
//					sql =
//						"exec InsertCardAcc '"
//							+ cardUpdate.getCardno()
//							+ "','"
//							+ cardUpdate.getShopid()
//							+ "',-1,"
//							+ cardUpdate.getPayvalue()
//							+ ","+cardUpdate.getPoint()+",'',559001,'��Ա������'";
//					state.executeUpdate(sql);


					conn.commit();
					result = "1";
					conn.setAutoCommit(true);
				} catch (SQLException ex) {
					ex.printStackTrace();
					conn.rollback();
					DBConnection.closeAll(rs, state, conn);
					return "���ִ���,����ع�";
				}
			} else {
				result = "�޴˿���";
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
