/*
 * Created on 2004-6-6
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.xshell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.cyber.CyberCmd;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public abstract class XShellServer extends HttpServlet {
	public abstract void execute();

	public void doPost( HttpServletRequest request, HttpServletResponse response )
		throws ServletException, IOException 
	{
		response.setContentType("text/plain");
		InputStream  in  = request.getInputStream();
		String command = "com.royalstone.pos.cyber.TimeCmd";
		Element elm_input = null;
		Element reply = null;
		
		try {
			Document doc = (new SAXBuilder()).build( in );
			Element root = doc.getRootElement();
			command = root.getChildText( "command" );
			elm_input = root.getChild( "xinput" );
			
			XMLOutputter outputter = new XMLOutputter( "  ", true, "GB2312" );
			outputter.setTextTrim(true);
			outputter.output( doc, System.out );

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

System.err.println ( command );		

		try {
			connectDatabase();
			
			Class stove = Class.forName( command );
			CyberCmd cmd = (CyberCmd) stove.newInstance();
			
			cmd.setDBConnection( dbconnection );
			reply = cmd.excute( elm_input );

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				dbconnection.close();
			} catch (SQLException ex) {
				System.err.println(ex);
			}
		}

		XMLOutputter outputter = new XMLOutputter( "  ", true, "GB2312" );
		outputter.setTextTrim(true);
		OutputStream out =  response.getOutputStream();
		outputter.output( new Document( reply ), out );
		out.flush();
		out.close();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException 
	{
		doPost( request, response);
	}

	private void connectDatabase() throws NamingException, SQLException
	{
		Context   ctx = null;
		DataSource ds = null;
		ctx = new InitialContext();
		if (ctx != null) ds = (DataSource) ctx.lookup("java:comp/env/dbpos");
		if (ds != null)  dbconnection = ds.getConnection();
	}
	
	Connection dbconnection    = null;
}

