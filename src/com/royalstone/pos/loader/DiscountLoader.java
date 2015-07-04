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

import com.royalstone.pos.favor.DiscountList;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.command.DiscountCommand;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DiscountLoader {

	private DiscountCommand discountCommand = new DiscountCommand();

	public static void main(String[] args) {
		DiscountLoader w = new DiscountLoader("localhost", 9090);
		try {
			w.download("discount.NEW.lst");
		} catch (IOException e) {
			System.out.println("Failed!");
			// e.printStackTrace();
		}
	}

	public DiscountLoader(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void download(String file) throws IOException {
		Response result = null;

		servlet = new URL("http", host, port, "/pos41/DispatchServlet");
		conn = (HttpURLConnection) servlet.openConnection();

		Object[] params = new Object[1];

		params[0] = "com.royalstone.pos.web.command.DiscountCommand";

		Object[] results = null;

		if (pos.hasServer) {

			MarshalledValue mvI = new MarshalledValue(params);
			System.out.println("Invoke DiscountCommand! ");
			MarshalledValue mvO = null;
			try {
				mvO = HttpInvoker.invoke(conn, mvI);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (mvO != null) {
				results = mvO.getValues();
			}

		} else {

			results = discountCommand.excute(params);

		}

		if (results != null && results.length > 0) {
			result = (Response) results[0];
			DiscountList lst = (DiscountList) result.getObject();
			if (lst != null)
				lst.dump(file);
			System.out.println(result);
		}

	}

	private URL servlet;
	private HttpURLConnection conn;
	private String host;
	private int port;
}
