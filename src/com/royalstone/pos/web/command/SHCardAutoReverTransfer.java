package com.royalstone.pos.web.command;

import com.royalstone.pos.card.SHCardPayVO;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.invoke.HttpInvoker;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

/**
 * 服务端代码，调用相应的Ejb完成储值卡的支付
 * @author liangxinbiao
 */
public class SHCardAutoReverTransfer implements ICommand {

	private SHCardAutoReverServletCommand shCardAutoReverServletCommand =
		new SHCardAutoReverServletCommand();

	public Object[] excute(Object[] values) {

		if (values.length == 3
			&& (values[1] instanceof SHCardPayVO)
			&& ((values[2] instanceof String) || (values[2] == null))) {
			try {

				SHCardPayVO payVO = (SHCardPayVO) values[1];
				String host = (String) values[2];
				String result = null;

				result = pay(payVO, host);

				Object[] results = new Object[1];

				results[0] = result;

				return results;

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return null;
	}
	private String pay(SHCardPayVO payVo, String host) {

		String result = null;

		try {

			Object[] results = null;

			Object[] params = new Object[2];

			params[0] =
				"com.royalstone.pos.web.command.SHCardAutoReverServletCommand";
			params[1] = payVo;

			if (host != null && !host.equals("NoCardServer")) {

				URL servlet;
				HttpURLConnection conn;
				try {
					servlet =
						new URL("http://" + host + "/pos41/DispatchServlet");
				} catch (Exception ex) {
					ex.printStackTrace();
					result = "连接卡服务器出错,按清除键继续！";
					return result;
				}

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else if (host.equals("NoCardServer")) {

				results = shCardAutoReverServletCommand.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (String) results[0];
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return result;
	}
}
