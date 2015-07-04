package com.royalstone.xshell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/*
 * 创建日期 2004-5-24
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */

/**
 * @author Mengluoyi
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class CyberShell 
{
	public static void main( String[] args ) throws IOException
	{
		CyberShell sh = new CyberShell( "localhost", 9090 );
		Element elm = sh.invoke( "TimdCmd", new Element("xinput" ) );
		XMLOutputter outputter = new XMLOutputter( "  ", true, "GB2312" );
		outputter.setTextTrim(true);
		outputter.output(new Document(elm), System.out );

	}
	
	public CyberShell( String host, int port )
	{
		this.host = host;
		this.port = port;
	}

	public Element invoke( String command, Element xinput ) 
	{

		try {
			URL servlet;
			HttpURLConnection conn;

			servlet = new URL ( "http", host, port, DISPATCHER );
			conn = (HttpURLConnection) servlet.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("ContentType", "text/plain" );
			conn.setRequestMethod("POST");

			OutputStream out = conn.getOutputStream();
			
			Element elm_request = new Element( "request" );
			Element elm_cmd 	= new Element( "command" ).addContent( "com.royalstone.pos.cyber.TimeCmd" );
			elm_request.addContent( elm_cmd );
			elm_request.addContent( xinput );
			XMLOutputter outputter = new XMLOutputter( "  ", true, "GB2312" );
			outputter.setTextTrim(true);
			outputter.output(new Document(elm_request), out );
			out.flush();
			out.close();

			InputStream  in  = conn.getInputStream();
			Document doc = (new SAXBuilder()).build( in );
			in.close();

			return doc.getRootElement();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	private static final String DISPATCHER = "/pos41/CyberShellServer";

	private String host;
	private int port;
}
