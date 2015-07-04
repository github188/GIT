package com.royalstone.pos.web.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.royalstone.pos.card.MemberCard;
import com.royalstone.pos.web.util.DBConnection;

/**
 * ����˴��룬������ѯ���˿�����Ϣ
 * @author liangxinbiao
 */
public class MemberCardQueryCommand implements ICommand {

    /**
     * @see com.royalstone.pos.web.command.ICommand#excute(java.lang.Object[])
     */
    public Object[] excute(Object[] values) {
        if (values.length == 2
                && (values[1] instanceof String)) {
            Object[] results = new Object[1];
            results[0] = query((String) values[1]);
            return results;
        }

        return null;
    }

    /**
     * ���ݿ��ź�����(�ӿ���������)��ѯ���˿�����Ϣ
     * @param CardNo ���˿���
     * @return ���˿���ѯֵ����
     */
    private MemberCard query(String CardNo) {

        Connection conn = null;
        Statement state = null;
        ResultSet rs = null;

        MemberCard cardquery = new MemberCard();
        String mode = null;
        String realcard = null;
        if (CardNo == null
                || CardNo.trim().equals("")) {
            cardquery.setExceptionInfo("���Ż�����Ϊ��");
            return cardquery;
        }
        
        try {
			conn = DBConnection.getConnection("java:comp/env/dbcard");

            conn.setAutoCommit(true);
            state = conn.createStatement();

//            rs =
//                    state.executeQuery(
//                            "select CardNO,Mode,MemberID,memberlevel,Point from guest where cardno = '"
//                    + CardNo.trim()
//                    + "'");
             rs =
                    state.executeQuery(
                            "select guest.CardNO,guest.Mode,guest.MemberID,guest.memberlevel,guest.Point from guest,CardType  where " +
                    " guest.CardType=CardType.cardtype and ( CardType.flag='0' or CardType.flag='2' )  and (cardno = '"
                    + CardNo.trim()+ "' or mobile = '"+CardNo.trim()+"')");
            //rs.next();
            if (!rs.next()) {
                cardquery.setExceptionInfo("�˿��Ų�����,�밴���������");
                DBConnection.closeAll(rs, null, null);
                return cardquery;
            } else { //����

                mode = (rs.getString("Mode")).trim();
                if (!mode.equals("1")) {
                    switch (mode.charAt(0)) {
                        case '2':
                            cardquery.setExceptionInfo("δ���ʿ�,�밴���������");
                            break;
                        case 'm':
                            cardquery.setExceptionInfo("һ���ʧ��,�밴���������");
                            break;
                        case 'r':
                            cardquery.setExceptionInfo("�ѻ��տ�,�밴���������");
                            break;
                        case 'l':
                            cardquery.setExceptionInfo("���ع�ʧ��,�밴���������");
                            break;
                        case 'f':
                            cardquery.setExceptionInfo("���Ῠ,�밴���������");
                            break;
                        case 'e':
                            cardquery.setExceptionInfo("�ѻ���,�밴���������");
                            break;
                        case 'q':
                            cardquery.setExceptionInfo("�˿�,�밴���������");
                            break;
                        default :
                            cardquery.setExceptionInfo("��������,�밴���������");
                            break;
                    }
                    DBConnection.closeAll(rs, state, conn);
                    return cardquery;
                }
                //cardquery.setCardNo(CardNo);
                realcard = (rs.getString("CardNO")).trim();
                cardquery.setCardNo(realcard);
                cardquery.setMemberLevel(rs.getInt("memberlevel"));
                cardquery.setTotalPoint(rs.getBigDecimal("Point"));
            }
        } catch (SQLException se) {
            se.printStackTrace();
            cardquery = new MemberCard();
            cardquery.setExceptionInfo("���ݿ��������,�밴���������");
            return cardquery;
        } finally {
            DBConnection.closeAll(rs, state, conn);
        }
        return cardquery;
    }


}
