/*
 * Created on 2004-6-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.card.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.util.Response;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TimeQuery 
{
	public static void main(String[] args) 
	{
		TimeQuery q = new TimeQuery( "localhost", 9090 );
		try {
			q.query();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println( "Failed!" );
		} 
	}
	

	public TimeQuery( String host, int port ) 
	{
		this.host = host;
		this.port = port;
	}

	public void query() throws IOException
	{
		Response result = null;
		try {
			String url = "http://" + host + ":" + port + "/pos41/DispatchServlet";
			servlet = new URL ( url );
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] = "com.royalstone.pos.web.command.TimeCommand";
			params[1] = "Pls tell me current time.";

			MarshalledValue mvI = new MarshalledValue(params);
System.out.println( "TimeCommand Invoked! " );			
			MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				result = (Response) results[0];
				System.out.println( result );
			}
		}
		catch( java.io.EOFException e )
		{
			System.out.println( "EOF!" );
			e.printStackTrace();
		} 
		catch ( IOException e ) {
			e.printStackTrace();
		}

	}

	private URL servlet;
	private HttpURLConnection conn;
	private String host;
	private int port;
}


