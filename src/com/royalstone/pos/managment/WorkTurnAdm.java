/*
 * Created on 2004-6-15
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.managment;

import java.net.HttpURLConnection;
import java.net.URL;

import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.common.PosRequest;
import com.royalstone.pos.common.RequestLogon;
import com.royalstone.pos.invoke.CommandInvoker;
import com.royalstone.pos.util.Response;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WorkTurnAdm {

	public static void main(String[] args) 
	{
		try {
			WorkTurnAdm w = new WorkTurnAdm( "localhost", 9090 );
			w.closeTurn();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println( "Failed!" );
		} 
	}

	public WorkTurnAdm( String host, int port ) 
	{
		this.host = host;
		this.port = port;
	}
	
	public WorkTurnAdm( PosContext context )
	{
		this.host = context.getServerip();
		this.port = context.getPort();
	}

	public Response closeTurn()
	{
		PosContext ctx = PosContext.getInstance();
		PosRequest req = new PosRequest( ctx );
		Response result = null;

		CommandInvoker invoker = new CommandInvoker ( host, port );
		result = invoker.invoke( "com.royalstone.pos.web.command.CloseWorkCommand", req );

		return result;
	}
	
	public Response logoff()
	{
		PosContext ctx = PosContext.getInstance();
		PosRequest req = new PosRequest( ctx );
		Response result = null;

		CommandInvoker invoker = new CommandInvoker ( host, port );
		result = invoker.invoke( "com.royalstone.pos.web.command.LogoffCommand", req );

		return result;
	}
/**
 * updated by huangxuean on 25 Jun 2004
 * @param posid
 * @param cashierid
 * @param plainpin
 * @param shiftid
 * @param ip
 * @return
 * */
	public Response logon( String posid, String cashierid, String plainpin, int shiftid, String ip)
	{
		RequestLogon req = new RequestLogon( posid, cashierid, plainpin, shiftid,ip );
		Response result = null;
		
		CommandInvoker invoker = new CommandInvoker ( host, port );
		result = invoker.invoke( "com.royalstone.pos.web.command.LogonCommand", req );

		return result;
	}
	
	public Response logon( String posid, String cashierid, String plainpin, int shiftid)
		{
			RequestLogon req = new RequestLogon( posid, cashierid, plainpin, shiftid);
			Response result = null;
		
			CommandInvoker invoker = new CommandInvoker ( host, port );
			result = invoker.invoke( "com.royalstone.pos.web.command.LogonCommand", req );

			return result;
		}
	
	private URL servlet;
	private HttpURLConnection conn;
	private String host;
	private int port;
}
