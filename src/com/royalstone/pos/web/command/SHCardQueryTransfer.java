package com.royalstone.pos.web.command;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;

import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.card.SHCardQueryVO;

/**
 * ����˴��룬������ѯ���˿�����Ϣ
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
	 * ���ݿ��ź�����(�ӿ���������)��ѯ���˿�����Ϣ
	 * @param cardNo ���˿���
	 * @return ���˿���ѯֵ����
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
					cardquery.setExceptioninfo("���ӿ�����������,�����������");
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
			cardquery.setExceptioninfo("��ѯ������������,�����������");
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
					result = "���ӿ�����������,�����������";
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
			result = "��ѯ������������,�����������";
			return result;
		}
		return result;

	}
}
