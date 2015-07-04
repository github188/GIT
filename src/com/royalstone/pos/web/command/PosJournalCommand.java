/*
 * Created on 2004-6-4
 */
package com.royalstone.pos.web.command;

import java.sql.Connection;
import java.sql.SQLException;

import com.royalstone.pos.journal.PosJournal;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.util.DBConnection;

/**
 * @author Mengluoyi
 *
 */
public class PosJournalCommand implements ICommand 
{

	public PosJournalCommand () 
	{
	}

	public Object[] excute(Object[] values) {
		Object[] results  = null;
		Response response = null;

		Connection con    = null;

		results = new Object[1];

		if ( values != null
			&& values[1] instanceof PosJournal ) {
			
			try {
				PosJournal journal = (PosJournal) values[1];
				
				con = DBConnection.getConnection("java:comp/env/dbpos");
				
				
				if( con != null ){
					System.out.println("ds has connected");   
					if( journal.isDuplicated( con ) ){ 
						journal.save( con );
						response = new Response( 0, "Duplicated journal saved." );
					} else {
						journal.save( con );
						response = new Response( 0, "Journal saved." );
					}
				}else {
					response = new Response( -1, "Database connection failed." );
				}

				results[0] = response;
				return results;

			} catch ( SQLException e ) {
				e.printStackTrace();
				System.err.println( "Journal writing failed." );
				results[0] = new Response( -1, "Journal writing failed." );
				
			}  finally {
				DBConnection.closeAll(null,null,con);
			}
		}else {
			results[0] = new Response( -1, "Data from Client is invalid." );
		}
		
		return results;
	}
}
