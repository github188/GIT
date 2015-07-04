package com.royalstone.pos.web.command;

import java.sql.Connection;

import com.royalstone.pos.common.OperatorList;
import com.royalstone.pos.db.PosMinister;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.util.DBConnection;

/**
 * œ¬‘ÿOperator–≈œ¢
 * @author root
 */
public class OperatorCommand implements ICommand 
{

	public OperatorCommand () 
	{
	}

	public Object[] excute(Object[] values) {

		Object[] results = new Object[1];
		Connection con    = null;
		Response response = null;

		if ( values != null  ) {
			try {

				con = DBConnection.getConnection("java:comp/env/dbpos");
				
				if( con != null ){
					System.out.println("dbpos connected in OperatorCommand!");   
					OperatorList list = PosMinister.getOperatorList( con );
					response = new Response( 0, "OK", list );
				}else {
					response = new Response( -1, "Database connection failed in OperatorCommand." );
				}

				results[0] = response;
				return results;
			} catch (Exception e) {
				e.printStackTrace();
				results[0] = new Response( -1, "Failed." );
			}finally{
				DBConnection.closeAll(null,null,con);
				return results;
			}
		}
		return null;
	}
}

