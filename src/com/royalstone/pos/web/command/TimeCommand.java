/*
 * Created on 2004-6-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.web.command;

import com.royalstone.pos.util.Response;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TimeCommand implements ICommand 
{

	public TimeCommand () 
	{
	}

	public Object[] excute(Object[] values) {

		if ( values != null ) {
			try {
				Object[] results = new Object[1];
				results[0] = new Response( 0, "Please set your system time." );
				return results;

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return null;
	}
}



