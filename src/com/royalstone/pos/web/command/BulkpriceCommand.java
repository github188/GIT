/*
 * Created on 2004-6-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.web.command;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.royalstone.pos.db.DiscMinister;
import com.royalstone.pos.favor.BulkPriceList;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.util.DBConnection;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BulkpriceCommand implements ICommand 
{

	public BulkpriceCommand () 
	{
	}

	public Object[] excute(Object[] values) {
		Object[] results = new Object[1];
		Connection con    = null;
		Response response = null;
		Context ctx       = null;
		DataSource ds     = null;

		if ( values != null  ) {
			try {
				ctx = new InitialContext();
				if( ctx != null ) ds = (DataSource)ctx.lookup( "java:comp/env/dbpos" );
				if( ds  != null ) con = ds.getConnection();  
				
				if( con != null ){
					System.out.println("BulkpriceCommand: dbpos connected!");   
					DiscMinister minister = new DiscMinister( con );
					BulkPriceList list = minister.getBulkList( con );
					response = new Response( 0, "OK", list );
				}else {
					response = new Response( -1, "Database connection failed." );
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

