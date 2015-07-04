/*
 * Created on 2004-6-15
 */
package com.royalstone.pos.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.royalstone.pos.common.Operator;
import com.royalstone.pos.journal.DataSource;

/**
 * @author Mengluoyi
 */
public class OperatorMinister {

	public static void main(String[] args) {
		DataSource datasrc = new DataSource( "172.16.7.197", 1433, "ApplePos" );
		Connection con = datasrc.open( "sa", "sa" );
		OperatorMinister m = new OperatorMinister( con );
		try {
			Operator op = m.getOperator( "0001" );
			System.out.println( "Operator: " + op );
			op.setPlainPin( "0002" );
			m.alterOperator( op );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param connection	Database connection.
	 */
	public OperatorMinister( Connection connection )
	{
		this.connection = connection;
	}

	/**
	 * @param operatorid:
	 * @param connection:
	 * @return	null if not found; a Operator obj whose id is operatorid.
	 */
	public Operator getOperator( String operatorid ) throws SQLException
	{
		Operator op;
		PreparedStatement pstmt = connection.prepareStatement(
			" SELECT clerk_id, name, passwd, levelid, max_disc FROM clerk_lst WHERE clerk_id = ?; "  );
                 //   " SELECT clerk_id, name, passwd, levelid FROM clerk_lst WHERE clerk_id = ? "  );
		pstmt.setString( 1, operatorid );

		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) return null;
		else {
			String clerk_id = rs.getString( "clerk_id" );
			String name 	= rs.getString( "name" );
			String passwd 	= rs.getString( "passwd" );
			int levelid 	= rs.getInt( "levelid" );
			int max_disc    =rs.getInt("max_disc");
			PreparedStatement stmt_power = connection.prepareStatement(
				" SELECT power_id FROM power_lst WHERE levelid = " + levelid  );

			ResultSet rs_power = stmt_power.executeQuery();

			Vector v = new Vector();
			while( rs_power.next() ) v.add( new Integer( rs_power.getInt("power_id") ) );
			int[] powers = new int[ v.size() ];
			for ( int i=0; i<v.size(); i++ ) powers[i] = ((Integer) v.get(i)).intValue();

			rs_power.close();

			//op = new Operator( clerk_id, passwd, name, powers );
                        op = new Operator( clerk_id, passwd, name,max_disc ,powers );

		}
		rs.close();
		return op;
	}

	public void alterOperator( Operator op ) throws SQLException
	{
		String sql = " UPDATE clerk_lst SET passwd = ? WHERE clerk_id = ?; ";
		PreparedStatement pstmt = connection.prepareStatement( sql );

		pstmt.setString( 1, op.getPinEncrypted() );
		pstmt.setString( 2, op.getId() );

		pstmt.executeUpdate();
	}

    public int getListNO(String posID)throws SQLException{
       Operator op;
        ResultSet rs = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                " SELECT listno FROM pos_lst WHERE pos_id = ?; "  );
            pstmt.setString( 1, posID );

            rs = pstmt.executeQuery();
            if( ! rs.next() ) return -1;
            else {
                return rs.getInt("listno");

            }
        } catch (SQLException e) {
           throw(e);
        } finally {
            if(rs!=null)
                try {
                    rs.close();
                } catch (SQLException e) {}
        }


    }

	private Connection connection;
}
