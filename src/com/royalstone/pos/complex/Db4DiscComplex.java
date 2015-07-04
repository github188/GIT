/*
 * Created on 2004-6-8
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.complex;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DecimalFormat;

import com.royalstone.pos.journal.DataSource;
import com.royalstone.pos.util.Day;
import com.royalstone.pos.util.PosTime;

/**
 * @author root
 *
 */
public class Db4DiscComplex {

	public static void main(String[] args) {
		DataSource datasrc = new DataSource( "172.16.16.108", 1433, "myShopPos" );
		Connection con = datasrc.open( "sa", "sa" );
		DiscComplexList disc_lst = Db4DiscComplex.getComplexList( con );
		disc_lst.print();
	}
	
	/**
	 * @param connection
	 * @return
	 */
	public static DiscComplexList getComplexList ( Connection connection )
	{
		String sql = " SELECT schemaNo, Name, Price, StartDate, EndDate, StartTime, EndTime,Get_Type"
					+ " FROM CombineProm; ";

		DiscComplexList disc_list = new DiscComplexList();

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			while( rs.next() ){
				int schemaNo   = rs.getInt( "schemaNo" );
				String Name    = rs.getString( "Name" );
				double Price   = rs.getDouble( "Price" );
				Date startDate = rs.getDate( "startDate" );
				Date EndDate   = rs.getDate( "EndDate" );
				Time StartTime = rs.getTime( "StartTime" );
				Time EndTime   = rs.getTime( "EndTime" );
				int level=Integer.parseInt(rs.getString("Get_Type"));
				
				DiscComplex disc = new DiscComplex( "" + schemaNo, Name, (int)Math.rint( 100*Price), 
					new Day( startDate ), new Day( EndDate ), 
					new PosTime( StartTime ), new PosTime( EndTime),level);

				String sql_item = " SELECT groupno, vgno, Qty, PromValue "
							+ " FROM CombinePromItem WHERE schemaNo = ? ; ";

				PreparedStatement pstmt = connection.prepareStatement( sql_item );
				pstmt.setInt(1, schemaNo);
				ResultSet rs_item = pstmt.executeQuery();
				
				while( rs_item.next() ){
					int Link_Goodsid = rs_item.getInt( "groupno" );
					int Goodsid      = rs_item.getInt( "vgno" );
					double Qty       = rs_item.getDouble( "Qty" );
					double PromValue = rs_item.getDouble( "PromValue" );
					
					DecimalFormat df = new DecimalFormat( "000000" );
					DiscPool pool    = new DiscPool( df.format(Link_Goodsid), df.format(Goodsid), 
						(int)Math.rint(Qty), (int)Math.rint(100*PromValue) );

					disc.addPool( pool );
				}
				rs_item.close();
				disc_list.add( disc );
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 

		return disc_list;
	}
}
