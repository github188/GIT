package com.royalstone.pos.card;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.shell.pos;

/**
 * 挂账卡实现，它负责和后台服务器通讯，实现挂账卡的查询、支付、自动冲正等功能
 * @author liangxinbiao
 */
public class LoanCard implements ILoanCard {

	private URL servlet;
	private HttpURLConnection conn;

	public LoanCard() {
		try {
			servlet =
				new URL(
					"http://"
						+ pos.core.getPosContext().getServerip()
						+ ":"
						+ pos.core.getPosContext().getPort()
						+ "/pos41/DispatchServlet");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public LoanCard(URL servlet) {
		this.servlet = servlet;
	}

	/**
	 * @see com.royalstone.pos.card.ILoanCard#query(java.lang.String, java.lang.String)
	 */
	public LoanCardQueryVO query(String cardNo, String secrety) {

		LoanCardQueryVO result = null;
		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[3];

			params[0] = "com.royalstone.pos.web.command.LoanCardQueryCommand";
			params[1] = cardNo;
			params[2] = secrety;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				result = (LoanCardQueryVO) results[0];
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	/**
	 * @see com.royalstone.pos.card.ILoanCard#pay(com.royalstone.pos.card.LoanCardPayVO)
	 */
	public String pay(LoanCardPayVO cp) {

		String result = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] = "com.royalstone.pos.web.command.LoanCardPayCommand";
			params[1] = cp;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				result = (String) results[0];
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	/**
	 * @see com.royalstone.pos.card.ILoanCard#autoRever(com.royalstone.pos.card.LoanCardPayVO)
	 */
	public String autoRever(LoanCardPayVO cp) {
		String result = null;

		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] = "com.royalstone.pos.web.command.LoanCardAutoReverCommand";
			params[1] = cp;

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
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
