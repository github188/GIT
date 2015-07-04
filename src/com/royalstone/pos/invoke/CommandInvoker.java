package com.royalstone.pos.invoke;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.command.ICommand;

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
public class CommandInvoker {
	public CommandInvoker(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public Response invoke(String command, Object obj) {
		Response result;

		try {
			URL servlet;
			HttpURLConnection conn;

			servlet = new URL("http", host, port, DISPATCHER);
			conn = (HttpURLConnection) servlet.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("ContentType", REQUEST_CONTENT_TYPE);
			conn.setRequestMethod("POST");

			Object[] params = new Object[2];
			params[0] = command;
			params[1] = obj;

			Object[] results = null;

			if (pos.hasServer) {

				MarshalledValue mvI = new MarshalledValue(params);

				ObjectOutputStream oos =
					new ObjectOutputStream(conn.getOutputStream());
				oos.writeObject(mvI);
				oos.flush();

				ObjectInputStream ois =
					new ObjectInputStream(conn.getInputStream());

				MarshalledValue mvO = (MarshalledValue) ois.readObject();
				if (mvO != null) {
					results = mvO.getValues();
				} else {
					result = new Response(-1, "与服务器的联系失败");
				}

				ois.close();
				oos.close();

			} else {

				try {
					results =
						(
							(ICommand)
								(Class.forName(command).newInstance())).excute(
							params);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}

			if (results != null && results.length > 0)
				result = (Response) results[0];
			else
				result = new Response(-1, "原因不明的错误");

		} catch (IOException ex) {
			ex.printStackTrace();
			result = new Response(-1, "与后台服务器联系失败！");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			result = new Response(-1, "与后台服务器联系失败！");
		}

		return result;
	}

	private static final String DISPATCHER = "/pos41/DispatchServlet";
	private static final String REQUEST_CONTENT_TYPE =
		"application/x-java-serialized-object";

	private String host;
	private int port;
}
