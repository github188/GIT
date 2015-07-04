package com.royalstone.pos.loader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.common.OperatorList;
import com.royalstone.pos.common.PayModeList;
import com.royalstone.pos.db.PosMinister;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

public class PayModeLoader implements ICommand {
	
	public void download2Xml(OutputStream out) throws IOException {

		Connection con = null;

		try {
			
			con = DBConnection.getConnection("java:comp/env/dbpos");

			if (con != null) {
				PayModeList list = PosMinister.getPayModeList(con);
				XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
				outputter.setTextTrim(true);
				outputter.output(new Document(list.toElement()), out);
			}

			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} finally {
			DBConnection.closeAll(null, null, con);
		}

	}

	public Object[] excute(Object[] values) {
		Object[] results = new Object[1];
		Connection con    = null;
		Response response = null;

		if ( values != null  ) {
			try {

				con = DBConnection.getConnection("java:comp/env/dbpos");
				
				if( con != null ){
					System.out.println("dbpos connected in OperatorCommand!");   
					PayModeList list = PosMinister.getPayModeList(con);
					response = new Response( 0, "OK", list );
				}else {
					response = new Response( -1, "Database connection failed in OperatorCommand." );
				}

				results[0] = response;
				return results;
			} catch (Exception e) {
				e.printStackTrace();
				results[0] = new Response( -1, "Failed." );
			}finally{
				DBConnection.closeAll(null,null,con);
				return results;
			}
		}		return null;
	}
	
	
	public static void main(String[] args) {
		PayModeLoader pd = new PayModeLoader("172.16.7.163", 9090);
		try {
			new PayModeLoader().download2Xml(new FileOutputStream("paymode.NEW.xml"));
		} catch (IOException e) {
			System.out.println("Failed!");
			// e.printStackTrace();
		}
	}

	public PayModeLoader() {
	}

	public PayModeLoader(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void download(String file) throws IOException {
		Response result = null;

		servlet = new URL("http", host, port, "/pos41/DispatchServlet");
		conn = (HttpURLConnection) servlet.openConnection();

		Object[] params = new Object[1];

		params[0] = "com.royalstone.pos.web.command.PayModeCommand";

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
			results = excute(params);
		}

		if (results != null && results.length > 0) {
			result = (Response) results[0];
			PayModeList lst = (PayModeList) result.getObject();
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
