package com.royalstone.pos.web.command.realtime;

import java.sql.*;
import java.util.GregorianCalendar;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.data.PosPriceData;
import com.royalstone.pos.favor.*;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * 服务端代码，负责查询商品的普通促销信息 
 * @author liangxinbiao
 */
public class GetDiscCriteriaCommand implements ICommand {

	private String errorMsg1;
	private String errorMsg2;

	/**
	 * @see com.royalstone.pos.web.command.ICommand#excute(java.lang.Object[])
	 */
	public Object[] excute(Object[] values) {

		if (values != null
			&& values.length == 4
			&& (values[1] instanceof String)) {

			Connection con = null;

			try {
				con = DBConnection.getConnection("java:comp/env/dbpos");

				Object[] result = new Object[4];
				result[0] =
					getDiscCriteria(
						con,
						(String) values[1],
						(String) values[2],
						(String) values[3]);
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
	 * 根据商品编码和商品的促销类型查询出商品的普通促销信息
	 * @param connection 到数据库的连接
	 * @param code 商品编码
	 * @param ptype 促销类型
	 * @return 普通促销信息
	 */
	public DiscCriteria getDiscCriteria(
		Connection connection,
		String code,
		String ptype,
		String level) {

		DiscCriteria result = null;

		try {
			result = getDiscCriteriaWithException(connection, code, ptype, level);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMsg1 = "数据库查询错误!";
			errorMsg2 = e.getMessage();
		}

		return result;
	}

	/**
	 * 根据商品编码在表distitem_vgno里查询商品的单品折扣
	 * @param connection 到数据库的连接
	 * @param code 商品编码
	 * @return 普通促销信息
	 * @throws SQLException
	 */
	public DiscCriteria getDisc4Goods(Connection connection, String code)
		throws SQLException {

		DiscCriteria result = null;

		String sql =
			" SELECT vgno, distrate1, distrate2, distrate3, "
				+ " min_amount, med_amount, max_amount, "
				+ " starttime, endtime "
				+ " FROM distitem_vgno where vgno=?; ";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, code);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			String vgno = Formatter.mytrim(rs.getString("vgno"));
			int distrate1 = rs.getInt("distrate1");
			int distrate2 = rs.getInt("distrate2");
			int distrate3 = rs.getInt("distrate3");
			int min_amount = rs.getInt("min_amount");
			int med_amount = rs.getInt("med_amount");
			int max_amount = rs.getInt("max_amount");

			Date starttime = rs.getDate("starttime");
			Date endtime = rs.getDate("endtime");

			GregorianCalendar g_start = new GregorianCalendar();
			g_start.setTime(starttime);

			GregorianCalendar g_end = new GregorianCalendar();
			g_end.setTime(endtime);

			result =
				new Disc4Goods(
					vgno,
					distrate1,
					min_amount,
					distrate2,
					med_amount,
					distrate3,
					max_amount,
					g_start,
					g_end);
		}

		return result;

	}

	/**
	 * 根据商品编码在表distitem_dept和price_lst里查询商品的整类折扣
	 * @param connection 到数据库的连接
	 * @param code 商品编码
	 * @return 普通促销信息
	 * @throws SQLException
	 */
	public DiscCriteria getDisc4Dept(Connection connection, String code)
		throws SQLException {

		DiscCriteria result = null;

		String sql =
			" SELECT distitem_dept.deptno, distrate1, distrate2, distrate3, "
				+ " min_amount, med_amount, max_amount, "
				+ " starttime, endtime "
				+ " FROM distitem_dept,price_lst where price_lst.vgno=? and price_lst.deptno=distitem_dept.deptno; ";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, code);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			String deptno = Formatter.mytrim(rs.getString("deptno"));
			int distrate1 = rs.getInt("distrate1");
			int distrate2 = rs.getInt("distrate2");
			int distrate3 = rs.getInt("distrate3");
			int min_amount = rs.getInt("min_amount");
			int med_amount = rs.getInt("med_amount");
			int max_amount = rs.getInt("max_amount");

			Date starttime = rs.getDate("starttime");
			Date endtime = rs.getDate("endtime");

			GregorianCalendar g_start = new GregorianCalendar();
			g_start.setTime(starttime);

			GregorianCalendar g_end = new GregorianCalendar();
			g_end.setTime(endtime);

			result =
				new Disc4Dept(
					deptno,
					distrate1,
					min_amount,
					distrate2,
					med_amount,
					distrate3,
					max_amount,
					g_start,
					g_end);
		}

		return result;
	}
    public DiscCriteria getDisc4MemberDept(Connection connection, String code,String level)
        throws SQLException {

        DiscCriteria result = null;
        int		discRate;
        
        int disc = 0;
        String sql_new = "select promdisc from datediscrate_vip where vipdate = convert(char(2),getdate(),103) and promlevel = ? ;";
        PreparedStatement stmt_new = connection.prepareStatement(sql_new);
        stmt_new.setString(1,level);
        ResultSet rs_new = stmt_new.executeQuery();
        if (rs_new.next()){
        	disc = rs_new.getInt("promdisc");
        	}
        rs_new.close();

        String sql =
            " SELECT DeptID, DiscLevel, DiscRate,"
					+ " StartTime, EndTime "
					+ " FROM discDeptMember,price_lst where price_lst.vgno=? and price_lst.deptno=CONVERT(char(15),discDeptMember.DeptID) and  discDeptMember.Disclevel=?; ";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, code);
        stmt.setString(2,level);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
				int 	deptID 	   = rs.getInt( "DeptID" );
				int		discLevel  = rs.getInt( "DiscLevel" );
				if ( disc >0 ){
						discRate = 100 - disc;
					}else{
						discRate = rs.getInt( "DiscRate" );
					}
				Date	starttime  = rs.getDate( "StartTime" );
				Date	endtime    = rs.getDate( "EndTime" );

				GregorianCalendar g_start = new GregorianCalendar();
				g_start.setTime(starttime);

				GregorianCalendar g_end = new GregorianCalendar();
				g_end.setTime(endtime);

            result =
                new Disc4MemberDept( deptID, discLevel, discRate,g_start, g_end );
        }

        return result;

    }

	/**
	 * 根据商品编码从表promdisc_vip里查询出会员折扣
	 * @param connection 到数据库的连接
	 * @param code 商品编码
	 * @return 普通促销信息
	 * @throws SQLException
	 */
	public DiscCriteria getDisc4Member(Connection connection, String code,String level)
		throws SQLException {

		DiscCriteria result = null;

		int promdisc;
        int disc = 0;
        String sql_new = "select promdisc from datediscrate_vip where vipdate = convert(char(2),getdate(),103) and promlevel = ? ;";
        PreparedStatement stmt_new = connection.prepareStatement(sql_new);
        stmt_new.setString(1,level);
        ResultSet rs_new = stmt_new.executeQuery();
        if (rs_new.next()){
        	disc = rs_new.getInt("promdisc");
        	}
        rs_new.close();

		String sql =
			" SELECT vgno, promlevel, promdisc, "
				+ " startdate, enddate, starttime, endtime "
				+ " FROM promdisc_vip where vgno=? and promlevel=?; ";

		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, code);
		stmt.setString(2,level);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			String vgno = Formatter.mytrim(rs.getString("vgno"));
			int promlevel = rs.getInt("promlevel");
			if (disc > 0){
				 	promdisc = 100 - disc;
				} else {
					promdisc = rs.getInt("promdisc");
			}

			Date startdate = rs.getDate("startdate");
			Date enddate = rs.getDate("enddate");
			Date starttime = rs.getDate("starttime");
			Date endtime = rs.getDate("endtime");

			GregorianCalendar g_start = new GregorianCalendar();
			g_start.setTime(starttime);

			GregorianCalendar g_end = new GregorianCalendar();
			g_end.setTime(endtime);

			result = new Disc4Member(vgno, promdisc, promlevel, g_start, g_end);
		}

		return result;
	}


	/**
	 * 根据商品编码从表promotion里查询出商品的单品促销信息
	 * @param connection 到数据库的连接
	 * @param code 商品编码
	 * @return 普通促销信息
	 * @throws SQLException
	 */
	public DiscCriteria getPromotion(Connection connection, String code)
		throws SQLException {

		DiscCriteria result = null;

		String sql =
			" SELECT vgno, promtype, promprice, "
				+ " startdate, enddate, starttime, endtime "
				+ "FROM promotion where vgno=?; ";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, code);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			String vgno = Formatter.mytrim(rs.getString("vgno"));
			String promtype = Formatter.mytrim(rs.getString("promtype"));
			double promprice = rs.getDouble("promprice");

			Date startdate = rs.getDate("startdate");
			Date enddate = rs.getDate("enddate");
			Date starttime = rs.getDate("starttime");
			Date endtime = rs.getDate("endtime");

			GregorianCalendar g_start = new GregorianCalendar();
			g_start.setTime(starttime);

			GregorianCalendar g_end = new GregorianCalendar();
			g_end.setTime(endtime);

			result =
				new Promotion(vgno, (int) (100 * promprice), g_start, g_end);
		}

		return result;
	}

	/**
	 * 根据商品编码从表promotion_vip里查询出商品的会员价
	 * @param connection 到数据库的连接
	 * @param code 商品编码
	 * @return 普通促销信息
	 * @throws SQLException
	 */
	public DiscCriteria getProm4Member(Connection connection, String code,String level)
		throws SQLException {

		DiscCriteria result = null;

		String sql =
			" SELECT vgno, promlevel, promprice, "
				+ " startdate, enddate, starttime, endtime "
				+ " FROM promotion_vip where vgno=? and promlevel=?; ";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, code);
		stmt.setString(2,level);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			String vgno = Formatter.mytrim(rs.getString("vgno"));
			int promlevel = rs.getInt("promlevel");
			double promprice = rs.getDouble("promprice");

			Date startdate = rs.getDate("startdate");
			Date enddate = rs.getDate("enddate");
			Date starttime = rs.getDate("starttime");
			Date endtime = rs.getDate("endtime");

			GregorianCalendar g_start = new GregorianCalendar();
			g_start.setTime(starttime);

			GregorianCalendar g_end = new GregorianCalendar();
			g_end.setTime(endtime);

			result =
				new Prom4Member(
					vgno,
					(int)(Math.rint(100 * promprice)),
					promlevel,
					g_start,
					g_end);
		}

		return result;
	}

	
public DiscCriteria getMemberDiscount(Connection connection, String code,String level)
    throws SQLException {

    DiscCriteria result = null;
    Prom4Member pro4Member=null;
      double pro4MemberPromprice=0;
    Disc4Member disc4Member=null;
      int promdisc =0;
    Disc4MemberDept disc4MemberDept=null;
      int discRate=0;

    LookupGoodsCommand lgc=new LookupGoodsCommand();
    /*TODO 药品限购*/
    PosPriceData codenew = new PosPriceData();
    codenew.setSaleCode(code);
    Goods gs=lgc.lookupWithException(connection,codenew);
    if(gs==null)
            return null;
    
    // zhouzhou add 
    int disc = 0;
    String sql_new = "select promdisc from datediscrate_vip where vipdate = convert(char(2),getdate(),103) and promlevel = ? ;";
    PreparedStatement stmt_new = connection.prepareStatement(sql_new);
    stmt_new.setString(1,level);
    ResultSet rs_new = stmt_new.executeQuery();
    if (rs_new.next()){
    	disc = rs_new.getInt("promdisc");
    	}
    rs_new.close();

    String sql =
        " SELECT vgno, promlevel, promprice, "
            + " startdate, enddate, starttime, endtime "
            + " FROM promotion_vip where vgno=? and promlevel = ?; ";
    PreparedStatement stmt1 = connection.prepareStatement(sql);
    stmt1.setString(1, code);
    stmt1.setString(2,level);
    ResultSet rs1 = stmt1.executeQuery();
    if (rs1.next()) {
        String vgno = Formatter.mytrim(rs1.getString("vgno"));
        int promlevel = rs1.getInt("promlevel");
        pro4MemberPromprice = rs1.getDouble("promprice");

        Date startdate = rs1.getDate("startdate");
        Date enddate = rs1.getDate("enddate");
        Date starttime = rs1.getDate("starttime");
        Date endtime = rs1.getDate("endtime");

        GregorianCalendar g_start = new GregorianCalendar();
        g_start.setTime(starttime);

        GregorianCalendar g_end = new GregorianCalendar();
        g_end.setTime(endtime);
        pro4Member =new Prom4Member( vgno,(int)Math.rint(100 * pro4MemberPromprice), promlevel,g_start, g_end);
    }
    
     
     sql =
		" SELECT vgno, promlevel, promdisc, "
			+ " startdate, enddate, starttime, endtime "
			+ " FROM promdisc_vip where vgno=? and promlevel=? ; ";

	PreparedStatement stmt2 = connection.prepareStatement(sql);
	stmt2.setString(1, code);
	stmt2.setString(2,level);
	ResultSet rs2 = stmt2.executeQuery();
	if (rs2.next()) {
		String vgno = Formatter.mytrim(rs2.getString("vgno"));
		int promlevel = rs2.getInt("promlevel");
 
		if (disc > 0){
			 	promdisc = 100 - disc;
			} else {
				 promdisc = rs2.getInt("promdisc");
		}

		Date startdate = rs2.getDate("startdate");
		Date enddate = rs2.getDate("enddate");
		Date starttime = rs2.getDate("starttime");
		Date endtime = rs2.getDate("endtime");

		GregorianCalendar g_start = new GregorianCalendar();
		g_start.setTime(starttime);

		GregorianCalendar g_end = new GregorianCalendar();
		g_end.setTime(endtime);

		disc4Member = new Disc4Member(vgno, promdisc, promlevel, g_start, g_end);
	}
    sql =
               " SELECT DeptID, DiscLevel, DiscRate,"
                       + " StartTime, EndTime "
                       + " FROM discDeptMember,price_lst where price_lst.vgno=? and price_lst.deptno=CONVERT(char(15),discDeptMember.DeptID) and discDeptMember.Disclevel=?; ";
       PreparedStatement stmt3 = connection.prepareStatement(sql);
           stmt3.setString(1, code);
           stmt3.setString(2,level);
           ResultSet rs3 = stmt3.executeQuery();
           if (rs3.next()) {
                   int 	deptID 	   = rs3.getInt( "DeptID" );
                   int		discLevel  = rs3.getInt( "DiscLevel" );
            		if (disc > 0){
            			discRate = 100 - disc;
        			} else {
        				discRate  = rs3.getInt( "DiscRate" );
        				}
                   Date	starttime  = rs3.getDate( "StartTime" );
                   Date	endtime    = rs3.getDate( "EndTime" );

                   GregorianCalendar g_start = new GregorianCalendar();
                   g_start.setTime(starttime);

                   GregorianCalendar g_end = new GregorianCalendar();
                   g_end.setTime(endtime);

               disc4MemberDept =
                   new Disc4MemberDept( deptID, discLevel, discRate,g_start, g_end );
           }

    if(pro4MemberPromprice==0&&promdisc==0&&discRate==0)
         return null;
    int price=gs.getPrice();
    int pro4MemberPromPrice=0;
    if(pro4MemberPromprice!=0)
      pro4MemberPromPrice=price-(int)(pro4MemberPromprice*100);
    int disc4MemberPrice=price*promdisc/100;
    int disc4MemberDeptPrice=price*discRate/100;

    if(pro4MemberPromPrice!=0&&pro4MemberPromPrice>=disc4MemberPrice&&pro4MemberPromPrice>=disc4MemberDeptPrice)
        result= pro4Member;
    if(disc4MemberPrice!=0&&disc4MemberPrice>=pro4MemberPromPrice&&disc4MemberPrice>=disc4MemberDeptPrice)
        result= disc4Member;
    if(disc4MemberDeptPrice!=0&&disc4MemberDeptPrice>=pro4MemberPromPrice&&disc4MemberDeptPrice>=disc4MemberPrice)
        result= disc4MemberDept;
    return result;
}


	
	public DiscCriteria getDiscCriteriaWithException(
		Connection connection,
		String code,
		String ptype,
		String level)
		throws SQLException {
		DiscCriteria result = null;
		if (ptype.equals(DiscCriteria.DISC4GOODS)) {
			result = getDisc4Goods(connection, code);
		} else if (ptype.equals(DiscCriteria.DISC4DEPT)) {
			result = getDisc4Dept(connection, code);
		} else if (ptype.equals(DiscCriteria.DISC4MEMBER)) {
			result = getDisc4Member(connection, code,level);
		} else if (ptype.equals(DiscCriteria.PROMOTION)) {
			result = getPromotion(connection, code);
		} else if (ptype.equals(DiscCriteria.PROM4MEMBER)) {
			result = getProm4Member(connection, code,level);
        } else if (ptype.equals(DiscCriteria.DISC4MEMBERDEPT)){
			result = getDisc4MemberDept(connection, code,level);
		} else if (ptype.equals(DiscCriteria.NORMAL))
            result=getMemberDiscount(connection, code,level);
		return result;
	}
}
