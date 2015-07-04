/*
 * Created on 2004-6-3
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test;
import java.sql.*;
//import java.util.GregorianCalendar;
// import java.util.*;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TouchDataBase 
{
	public static void main(String[] args) 
	{
		
		String dbUrl	= "jdbc:microsoft:sqlserver://172.16.12.36:1433;DatabaseName=test;SelectMethod=Cursor;";
		String user		= "sa";
		String passwd	= "sa";
		Connection con	= null;

		try{
			System.out.println("init ... ");
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			con = DriverManager.getConnection(dbUrl,user,passwd);


			PreparedStatement pstmt;
			/*
			 * 

						PreparedStatement pstmt = con.prepareStatement(
							"UPDATE meng SET age = ? WHERE id = ?");

						pstmt.setLong(1, 30);
						pstmt.setLong(2, 1970);
						pstmt.executeUpdate();
			 */
			con.setAutoCommit(false);
			System.out.println( "First" );
			pstmt = con.prepareStatement( "INSERT INTO meng(id, age, name) VALUES (?, ?, ?)" );
			pstmt.setLong(1, 20);
			pstmt.setLong(2, 1980);
			pstmt.setString(3, "√œ¬ﬁ“„");
			pstmt.addBatch();

			pstmt.setLong(1, 10);
			pstmt.setLong(2, 1990);
			pstmt.setString(3, "new!");
			pstmt.addBatch();
			System.out.println( "execute First" );
			pstmt.executeBatch();

			
			System.out.println( "Prepare Second" );
			
			
			PreparedStatement qstmt = con.prepareStatement( "INSERT INTO meng(id, age, name) VALUES (?, ?, ?)" );
			qstmt.setLong(1, 20);
			qstmt.setLong(2, 1980);
			qstmt.setString(3, "12345");
			qstmt.addBatch();

			qstmt.setLong(1, 10);
			qstmt.setLong(2, 1990);
			qstmt.setString(3, "67890");
			qstmt.addBatch();
			qstmt.executeBatch();
			
			con.commit();
			
			/*
			 * 
//			GregorianCalendar today = new GregorianCalendar( 2004, 3, 22);
			PreparedStatement qstmt = con.prepareStatement( 
				" SELECT name FROM meng; " 
				+ " WHERE listno = ? AND pos_id = ? AND dt = ? ; " );
				
			qstmt.setInt( 1, 1 );
			qstmt.setString( 2, "P001" );
			qstmt.setDate( 3, new Date(today.getTimeInMillis()) );
			ResultSet rs = qstmt.executeQuery();
			if( !rs.next() ) System.out.println( "ResultSet is NULL!" );
			else System.out.println( "Not NULL!" );
 */

			if(con!=null){try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}

		} 
		catch(Exception e){
			e.printStackTrace();
//			throw e;
			
		} 
	} 
}
