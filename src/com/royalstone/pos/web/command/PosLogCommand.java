/*
 * Created on 2004-8-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.pos.web.command;

import java.sql.Connection;

import com.royalstone.pos.journal.PosLog;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.util.DBConnection;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PosLogCommand implements ICommand {
	
	public PosLogCommand(){
		}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.web.command.ICommand#excute(java.lang.Object[])
	 */
	public Object[] excute(Object[] values) {
		
		Object[] results = new Object[1];
		Connection con = null;
		Response response = null;

		if(values != null && values[1] instanceof PosLog ){
			try {
				PosLog log = (PosLog) values[1];
				
				con = DBConnection.getConnection("java:comp/env/dbpos");				
				
				if( con != null ){
					System.out.println("ds has connected");   
					if( log.isDuplicated( con ) ){ 
						log.save( con );
						response = new Response( 0, "Duplicated log saved." );
					} else {
						log.save( con );
						response = new Response( 0, "Log saved." );
					}
				}else {
					response = new Response( -1, "Database connection failed." );
				}

				results[0] = response;
				return results;

			} catch (Exception e) {
				e.printStackTrace();
				results[0] = new Response( -1, "Log writing failed." );
				
			}finally{
				DBConnection.closeAll(null,null,con);
				return results;
			}
		}
		// TODO Auto-generated method stub
		return null;
	    }
	}


