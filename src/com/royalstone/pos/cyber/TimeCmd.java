/*
 * Created on 2004-6-27
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.cyber;

import java.io.IOException;
import java.sql.Connection;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.util.Day;
import com.royalstone.pos.util.PosTime;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TimeCmd implements CyberCmd
{

	public static void main(String[] args) throws IOException 
	{
		TimeCmd cmd = new TimeCmd();
		Element reply = cmd.excute( null );
		
		XMLOutputter outputter = new XMLOutputter( "  ", true, "GB2312" );
		outputter.setTextTrim(true);
		outputter.output( new Document( reply ), System.out );

	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.cyber.CyberCmd#excute(org.jdom.Element)
	 */
	public Element excute(Element xinput) 
	{
		Element reply = new Element( "reply" );
		Element xout  = new Element( "xoutput" );
		xout.addContent( new Day().toElement() );
		xout.addContent( new PosTime().toElement() );
		Element xerr  = new XErrorElement( 0, "" );
		reply.addContent( xout );
		reply.addContent( xerr );
		return reply;
	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.cyber.CyberCmd#setDBConnection(java.sql.Connection)
	 */
	public void setDBConnection(Connection con) {
		// do nothing.
	}
}
