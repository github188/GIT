/*
 * Created on 2004-6-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.loader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.royalstone.pos.favor.BulkPriceList;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.util.Response;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BulkpriceLoader {

	public static void main(String[] args) 
	{
		BulkpriceLoader w = new BulkpriceLoader( "localhost", 9090 );
		try {
			w.download( "bulkprice.NEW.lst" );
		} catch (IOException e) {
			System.out.println( "Failed!" );
			// e.printStackTrace();
		}
	}

	public BulkpriceLoader( String host, int port )
	{
		this.host = host;
		this.port = port;
	}
	
	public void download( String file ) throws IOException
	{
		Response result = null;
		
		servlet = new URL( "http", host, port, "/pos41/DispatchServlet" );
		conn = (HttpURLConnection) servlet.openConnection();

		Object[] params = new Object[1];

		params[0] = "com.royalstone.pos.web.command.BulkpriceCommand";

		MarshalledValue mvI = new MarshalledValue(params);
		System.out.println( "Invoke DiscountCommand! " );			
		MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

		Object[] results = null;

		if (mvO != null) {
			results = mvO.getValues();
		}

		if (results != null && results.length > 0) {
			result = (Response) results[0];
			BulkPriceList lst = (BulkPriceList) result.getObject();
			if(lst!=null)lst.dump( file );
			System.out.println( result );
		}

	}

	private URL servlet;
	private HttpURLConnection conn;
	private String host;
	private int port;
}
