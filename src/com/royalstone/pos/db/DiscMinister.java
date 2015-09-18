/*
 * Created on 2004-6-7
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.GregorianCalendar;

import com.royalstone.pos.favor.*;
import com.royalstone.pos.journal.DataSource;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DiscMinister 
{

	public static void main(String[] args) 
	{
		DataSource datasrc = new DataSource( "172.16.7.197", 1433, "ApplePos" );
		Connection con = datasrc.open( "sa", "sa" );
		DiscMinister m = new DiscMinister( con );
		DiscountList disc_lst = m.getDiscountList( con );
	}
	
	public DiscMinister ( Connection connection )
	{
		this.connection = connection;
	}
	
	public DiscountList getDiscountList( Connection connection )
	{
		if( lst == null ){
			lst = new DiscountList();
			getDisc4Goods( connection );
			getDisc4Dept( connection );
			getDisc4Member( connection );
			getPromotion( connection );
			getProm4Member( connection );
            getDisc4MemberDept(connection);
		}
		return lst;
	}
	
	
	public BulkPriceList getBulkList( Connection connection )
	{
		if( bulk_lst == null ){
			bulk_lst = new BulkPriceList();
			getBulkPrice( connection );
		}
		return bulk_lst;
	}
	
	public void getDisc4Goods( Connection connection )
	{
		String sql = " SELECT vgno, distrate1, distrate2, distrate3, "
					+ " min_amount, med_amount, max_amount, "
					+ " starttime, endtime "
					+ " FROM distitem_vgno; ";
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			while( rs.next() ){
				String 	vgno 	   = rs.getString( "vgno" );
				int		distrate1  = rs.getInt( "distrate1" );
				int		distrate2  = rs.getInt( "distrate2" );
				int		distrate3  = rs.getInt( "distrate3" );
				int		min_amount = rs.getInt( "min_amount" );
				int		med_amount = rs.getInt( "med_amount" );
				int		max_amount = rs.getInt( "max_amount" );
				
				Date	starttime  = rs.getDate( "starttime" );	
				Date	endtime    = rs.getDate( "endtime" );	


				GregorianCalendar g_start = new GregorianCalendar();
				g_start.setTime(starttime);

				GregorianCalendar g_end = new GregorianCalendar();
				g_end.setTime(endtime);

				lst.add( new Disc4Goods( vgno, distrate1, min_amount, distrate2, med_amount, distrate3, max_amount,
					g_start, g_end ) );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 

	}
	
	
	public void getBulkPrice( Connection connection )
	{
		String sql = " SELECT vgno, promprice1*100 promprice1, promprice2*100 promprice2, promprice3*100 promprice3, " 
					+ " promprice4*100 promprice4, promprice5*100 promprice5, promprice6*100 promprice6, "
					+ " qty1, qty2, qty3, qty4, qty5, qty6, "
					+ " startdate, enddate "
					+ " FROM discPrice_vgno; ";
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			while( rs.next() ){
				String 	vgno 	   = rs.getString( "vgno" );
				int		promprice1  = (int) (rs.getDouble( "promprice1" ) );
				int		promprice2  = (int) (rs.getDouble( "promprice2" ) );
				int		promprice3  = (int) (rs.getDouble( "promprice3" ) );
				int		promprice4  = (int) (rs.getDouble( "promprice4" ) );
				int		promprice5  = (int) (rs.getDouble( "promprice5" ) );
				int		promprice6  = (int) (rs.getDouble( "promprice6" ) );
				int		qty1 = rs.getInt( "qty1" );
				int		qty2 = rs.getInt( "qty2" );
				int		qty3 = rs.getInt( "qty3" );
				int		qty4 = rs.getInt( "qty4" );
				int		qty5 = rs.getInt( "qty5" );
				int		qty6 = rs.getInt( "qty6" );
				
				Date	starttime  = rs.getDate( "startdate" );	
				Date	endtime    = rs.getDate( "enddate" );	


				GregorianCalendar g_start = new GregorianCalendar();
				g_start.setTime(starttime);

				GregorianCalendar g_end = new GregorianCalendar();
				g_end.setTime(endtime);

				bulk_lst.add( new BulkPrice( vgno, promprice1, qty1, promprice2, qty2, promprice3, qty3, 
					promprice4, qty4, promprice5, qty5, promprice6, qty6,
					g_start, g_end ) );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	public void getDisc4Dept( Connection connection )
	{
		String sql = " SELECT deptno, distrate1, distrate2, distrate3, " 
					+ " min_amount, med_amount, max_amount, "
					+ " starttime, endtime "
					+ " FROM distitem_dept; ";
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			while( rs.next() ){
				String 	deptno 	   = rs.getString( "deptno" );
				int		distrate1  = rs.getInt( "distrate1" );
				int		distrate2  = rs.getInt( "distrate2" );
				int		distrate3  = rs.getInt( "distrate3" );
				int		min_amount = rs.getInt( "min_amount" );
				int		med_amount = rs.getInt( "med_amount" );
				int		max_amount = rs.getInt( "max_amount" );

				Date	starttime  = rs.getDate( "starttime" );	
				Date	endtime    = rs.getDate( "endtime" );	

				GregorianCalendar g_start = new GregorianCalendar();
				g_start.setTime(starttime);

				GregorianCalendar g_end = new GregorianCalendar();
				g_end.setTime(endtime);

				lst.add( new Disc4Dept( deptno, distrate1, min_amount, distrate2, med_amount, distrate3, max_amount,
					g_start, g_end ) );
//				System.out.println( "Disc4Dept: " + deptno + " " + distrate1 + " " + min_amount + " " + starttime + " " + endtime );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
    public void getDisc4MemberDept( Connection connection )
	{
		String sql = " SELECT DeptID, DiscLevel, DiscRate,"
					+ " StartTime, EndTime "
					+ " FROM discDeptMember; ";
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			while( rs.next() ){
				int 	deptID 	   = rs.getInt( "DeptID" );
				int		discLevel  = rs.getInt( "DiscLevel" );
				int		discRate  = rs.getInt( "DiscRate" );
				Date	starttime  = rs.getDate( "StartTime" );
				Date	endtime    = rs.getDate( "EndTime" );

				GregorianCalendar g_start = new GregorianCalendar();
				g_start.setTime(starttime);

				GregorianCalendar g_end = new GregorianCalendar();
				g_end.setTime(endtime);

				lst.add( new Disc4MemberDept( deptID, discLevel, discRate,g_start, g_end ) );
//				System.out.println( "Disc4Dept: " + deptno + " " + distrate1 + " " + min_amount + " " + starttime + " " + endtime );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void getDisc4Member( Connection connection )
	{
		String sql = " SELECT vgno, promlevel, promdisc, "
					+ " startdate, enddate, starttime, endtime "
					+ " FROM promdisc_vip; ";

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			while( rs.next() ){
				String 	vgno 	   = rs.getString( "vgno" );
				int 	promlevel  = rs.getInt( "promlevel" );
				int 	promdisc   = rs.getInt( "promdisc" );

				Date	startdate  = rs.getDate( "startdate" );	
				Date	enddate    = rs.getDate( "enddate" );	
				Date	starttime  = rs.getDate( "starttime" );	
				Date	endtime    = rs.getDate( "endtime" );	
				
				GregorianCalendar g_start = new GregorianCalendar();
				g_start.setTime(starttime);

				GregorianCalendar g_end = new GregorianCalendar();
				g_end.setTime(endtime);

//				System.out.println( "Disc4Member: " + vgno + " " + promlevel + " " + promdisc + " " + startdate + " " + enddate );
				lst.add( new Disc4Member( vgno, promdisc, promlevel, g_start, g_end ) );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	public void getPromotion( Connection connection )
	{
		String sql = " SELECT vgno, promtype, promprice, "
					+ " startdate, enddate, starttime, endtime,qflag, qty  "
					+ "FROM promotion; ";

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			while( rs.next() ){
				String 	vgno 	   = rs.getString( "vgno" );
				String 	promtype   = rs.getString( "promtype" );
				double 	promprice  = rs.getDouble( "promprice" );
				String flag = rs.getString("qflag");
				int qty = rs.getInt("qty");

				Date	startdate  = rs.getDate( "startdate" );	
				Date	enddate    = rs.getDate( "enddate" );	
				Date	starttime  = rs.getDate( "starttime" );	
				Date	endtime    = rs.getDate( "endtime" );	

				GregorianCalendar g_start = new GregorianCalendar();
				g_start.setTime(starttime);

				GregorianCalendar g_end = new GregorianCalendar();
				g_end.setTime(endtime);

				lst.add( new Promotion( vgno, (int) (100*promprice), g_start, g_end,flag,qty) );
//				System.out.println( "PROMOTION: " + vgno + " " + promtype + " " + promprice + " " + startdate + " " + enddate );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	public void getProm4Member( Connection connection )
	{
		String sql = " SELECT vgno, promlevel, promprice, "
					+ " startdate, enddate, starttime, endtime "
					+ " FROM promotion_vip; ";

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			while( rs.next() ){
				String 	vgno 	   = rs.getString( "vgno" );
				int 	promlevel   = rs.getInt( "promlevel" );
				double 	promprice  = Math.rint(rs.getDouble( "promprice" ));

				Date	startdate  = rs.getDate( "startdate" );	
				Date	enddate    = rs.getDate( "enddate" );	
				Date	starttime  = rs.getDate( "starttime" );	
				Date	endtime    = rs.getDate( "endtime" );	

				GregorianCalendar g_start = new GregorianCalendar();
				g_start.setTime(starttime);

				GregorianCalendar g_end = new GregorianCalendar();
				g_end.setTime(endtime);

				lst.add( new Prom4Member( vgno, (int) (100*promprice), promlevel, g_start, g_end ) );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	public void getDiscComplex( Connection connection )
	{
		String sql = "";
	}
	
	private Connection connection;
	private DiscountList lst = null;
	private BulkPriceList bulk_lst = null;
}
