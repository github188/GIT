/*
 * Created on 2004-8-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.pos.journal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Date;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.command.PosLogCommand;

/**
 * @author Administrator
 */

public class LogWriter {
	
	private PosLogCommand posLogCommand=new PosLogCommand();
	
	public static void main(String[] args) {
		LogWriter w = new LogWriter("localhost", 9090);
		w.writeLog("001.xml");
	}

	public LogWriter(String server, int port) {
		try {
			servlet =
				new URL(
					"http://" + server + ":" + port + "/pos41/DispatchServlet");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void writeLog(final String file) {
		Element root = getRootElement("poslog" + File.separator + file);
		PosLog log = new PosLog(root);

		Response result = null;
		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] = "com.royalstone.pos.web.command.PosLogCommand";
			params[1] = log;


			Object[] results = null;
			
			if(pos.hasServer){

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}
				
			}else{
				
				results=posLogCommand.excute(params);
			}
			

			if (results != null && results.length > 0) {
				result = (Response) results[0];
				System.out.println(result);
				if (result != null && result.succeed()) {
					
					PosContext context = PosContext.getInstance();
					 /*
                    //TODO 修改 沧州富达 by fire  2005_5_11
					WorkTurn workTurn = context.getWorkTurn();

					File dir = new File("poslog/" + workTurn.toString());
					*/
                    File dir = new File("poslog/" + Formatter.getDate(new Date()));
                    if (!dir.exists()) {
						dir.mkdir();
					}

					String srcfile = "poslog" + File.separator + file;
					String destfile =
						"poslog"
							+ File.separator
							//TODO 沧州富达 by fire  2005_5_11
							+ Formatter.getDate(new Date())
							+ File.separator
							+ file;

					FileChannel in = new FileInputStream(srcfile).getChannel();
					FileChannel out = new FileOutputStream(destfile).getChannel();
					in.transferTo(0, in.size(), out);

					in.close();
					out.close();

					(new File(srcfile)).delete();


				}
			}
		} catch (java.io.EOFException e) {
			System.out.println("EOF!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static Element getRootElement(String file) {
		try {
			Document doc = (new SAXBuilder()).build(new FileInputStream(file));
			Element root = doc.getRootElement();
			return root;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private URL servlet;
	private HttpURLConnection conn;
}
