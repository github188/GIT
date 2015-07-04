package com.royalstone.pos.web.command;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;

import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.card.SHCardQueryVO;

/**
 * 服务端代码，用来查询挂账卡的信息
 * @author liangxinbiao
 */
public class SHCardQueryTransfer implements ICommand {

	private SHCardQueryServletCommand shCardQueryServletCommand =
		new SHCardQueryServletCommand();

	public Object[] excute(Object[] values) {
		if (values.length == 4
			&& (values[1] instanceof String)
			&& (values[2] instanceof String)
			&& ((values[3] instanceof String) || (values[3] == null))) {
			String cardNo = (String) values[1];
			String secrety = (String) values[2];
			String host = (String) values[3];

			Object[] results = new Object[1];
			if ("-1".equals(secrety))
				results[0] = isNeedPsaa(cardNo, secrety, host);
			else
				results[0] = query(cardNo, secrety, host);
			return results;
		}

		return null;
	}

	/**
	 * 根据卡号和密码(子卡不需密码)查询挂账卡的信息
	 * @param cardNo 挂账卡号
	 * @return 挂账卡查询值对象
	 */
	private SHCardQueryVO query(String cardNo, String secrety, String host) {

		SHCardQueryVO cardquery = new SHCardQueryVO();

		try {

			Object[] params = new Object[3];

			params[0] =
				"com.royalstone.pos.web.command.SHCardQueryServletCommand";
			params[1] = cardNo;
			params[2] = secrety;

			Object[] results = null;

			if (host != null && !host.equals("NoCardServer")) {

				URL servlet;
				HttpURLConnection conn;
				try {
					servlet =
						new URL("http://" + host + "/pos41/DispatchServlet");
				} catch (Exception ex) {
					ex.printStackTrace();
					cardquery.setExceptioninfo("连接卡服务器出错,按清除键继续");
					return cardquery;
				}

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else if (host.equals("NoCardServer")) {

				results = shCardQueryServletCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				cardquery = (SHCardQueryVO) results[0];
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			cardquery.setExceptioninfo("查询卡服务器出错,按清除键继续");
			return cardquery;
		}
		return cardquery;
	}

	private String isNeedPsaa(String cardNo, String secrety, String host) {
		String result = "0";

		try {

			Object[] params = new Object[3];

			params[0] =
				"com.royalstone.pos.web.command.SHCardQueryServletCommand";
			params[1] = cardNo;
			params[2] = secrety;

			Object[] results = null;

			if (host != null && !host.equals("NoCardServer")) {

				URL servlet;
				HttpURLConnection conn;
				try {
					servlet =
						new URL("http://" + host + "/pos41/DispatchServlet");
				} catch (Exception ex) {
					ex.printStackTrace();
					result = "连接卡服务器出错,按清除键继续";
					return result;
				}

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else if (host.equals("NoCardServer")) {

				results = shCardQueryServletCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (String) results[0];
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			result = "查询卡服务器出错,按清除键继续";
			return result;
		}
		return result;

	}
}
