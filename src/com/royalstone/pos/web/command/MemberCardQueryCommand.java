package com.royalstone.pos.web.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.royalstone.pos.card.MemberCard;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，用来查询挂账卡的信息
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
     * 根据卡号和密码(子卡不需密码)查询挂账卡的信息
     * @param CardNo 挂账卡号
     * @return 挂账卡查询值对象
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
            cardquery.setExceptionInfo("卡号或密码为空");
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
                cardquery.setExceptionInfo("此卡号不存在,请按清除键继续");
                DBConnection.closeAll(rs, null, null);
                return cardquery;
            } else { //主卡

                mode = (rs.getString("Mode")).trim();
                if (!mode.equals("1")) {
                    switch (mode.charAt(0)) {
                        case '2':
                            cardquery.setExceptionInfo("未到帐卡,请按清除键继续");
                            break;
                        case 'm':
                            cardquery.setExceptionInfo("一般挂失卡,请按清除键继续");
                            break;
                        case 'r':
                            cardquery.setExceptionInfo("已回收卡,请按清除键继续");
                            break;
                        case 'l':
                            cardquery.setExceptionInfo("严重挂失卡,请按清除键继续");
                            break;
                        case 'f':
                            cardquery.setExceptionInfo("冻结卡,请按清除键继续");
                            break;
                        case 'e':
                            cardquery.setExceptionInfo("已换卡,请按清除键继续");
                            break;
                        case 'q':
                            cardquery.setExceptionInfo("退卡,请按清除键继续");
                            break;
                        default :
                            cardquery.setExceptionInfo("其他错误,请按清除键继续");
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
            cardquery.setExceptionInfo("数据库操作有误,请按清除键继续");
            return cardquery;
        } finally {
            DBConnection.closeAll(rs, state, conn);
        }
        return cardquery;
    }


}
