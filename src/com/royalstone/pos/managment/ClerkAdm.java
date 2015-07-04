/*
 * Created on 2004-6-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.managment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.royalstone.pos.common.RequestAlterPin;
import com.royalstone.pos.common.RequestLogon;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.command.AlterPinCommand;
import com.royalstone.pos.web.command.ClerkCommand;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ClerkAdm {
	
	private ClerkCommand clerkCommand = new ClerkCommand();
	private AlterPinCommand alterPinCommand = new AlterPinCommand();

	public ClerkAdm(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public Response getClerk(String posid, String cashierid, String plainpin) {
		RequestLogon req = new RequestLogon(posid, cashierid, plainpin);
		Response result = null;
		try {
			servlet = new URL("http", host, port, "/pos41/DispatchServlet");
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] = "com.royalstone.pos.web.command.ClerkCommand";
			params[1] = req;

			Object[] results = null;

			if (pos.hasServer) {
				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {
				results = clerkCommand.excute(params);
			}

			if (results != null && results.length > 0)
				result = (Response) results[0];
		} catch (IOException e) {
			e.printStackTrace();
			result = new Response(-1, "ERROR: 与后台服务器连接失败。");
		}

		return result;
	}

	public Response alterPin(
		String posid,
		String cashierid,
		String pin_old,
		String pin_new) {
		RequestAlterPin req =
			new RequestAlterPin(posid, cashierid, pin_old, pin_new);
		Response result = null;
		try {

			Object[] params = new Object[2];

			params[0] = "com.royalstone.pos.web.command.AlterPinCommand";
			params[1] = req;

			Object[] results = null;

			if (pos.hasServer) {

				servlet = new URL("http", host, port, "/pos41/DispatchServlet");
				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO =
					HttpInvoker.invokeWithException(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = alterPinCommand.excute(params);

			}

			if (results != null && results.length > 0)
				result = (Response) results[0];
		} catch (IOException e) {
			e.printStackTrace();
			result = new Response(-1, "ERROR: 与后台连接失败。");
		}

		return result;
	}

	private URL servlet;
	private HttpURLConnection conn;
	private String host;
	private int port;
}
