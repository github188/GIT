package com.royalstone.pos.invoke;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.util.Response;

/**
 * @author Mengluoyi
 *
 */
public class WebShell 
{
	public WebShell( String host, int port )
	{
		this.host = host;
		this.port = port;
	}

	public Response invoke( String command, Element elm ) 
	{
		Response result = null;
		Document request, reply;

		try {
			URL servlet;
			HttpURLConnection conn;

			servlet = new URL ( "http", host, port, DISPATCHER );
			conn = (HttpURLConnection) servlet.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("ContentType", REQUEST_CONTENT_TYPE );
			conn.setRequestMethod("POST");
			OutputStream out = conn.getOutputStream();
			
			Element root = new Element( "request" );
			Element elm_cmd = new Element( "command" ).addContent( command );
			root.addContent( elm_cmd );
			XMLOutputter outputter = new XMLOutputter( "  ", true, "GB2312" );
			request = new Document( root );
			outputter.output( request, out );

			InputStream in = conn.getInputStream();

			
		} catch (IOException ex) {
			ex.printStackTrace();
			result = new Response( -1, "与后台服务器联系失败！" );
		} 

		return result;
	}
	
	private static final String DISPATCHER = "/pos41/WebShellServer";
	private static final String REQUEST_CONTENT_TYPE = "application/x-java-serialized-object";

	private String host;
	private int port;
}
