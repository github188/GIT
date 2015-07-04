package com.royalstone.pos.loader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.royalstone.pos.common.OperatorList;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.command.OperatorCommand;

/**
 * @author root
 *
 */
public class OperatorLoader {

	private OperatorCommand operatorCommand = new OperatorCommand();

	public static void main(String[] args) {
		OperatorLoader w = new OperatorLoader("172.16.7.163", 9090);
		try {
			w.download("operator.NEW.lst");
		} catch (IOException e) {
			System.out.println("Failed!");
			// e.printStackTrace();
		}
	}

	public OperatorLoader(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void download(String file) throws IOException {
		Response result = null;

		servlet = new URL("http", host, port, "/pos41/DispatchServlet");
		conn = (HttpURLConnection) servlet.openConnection();

		Object[] params = new Object[1];

		params[0] = "com.royalstone.pos.web.command.OperatorCommand";

		Object[] results = null;

		if (pos.hasServer) {
			MarshalledValue mvI = new MarshalledValue(params);
			System.out.println("Invoke OperatorCommand! ");
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
			results = operatorCommand.excute(params);
		}

		if (results != null && results.length > 0) {
			result = (Response) results[0];
			OperatorList lst = (OperatorList) result.getObject();
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
