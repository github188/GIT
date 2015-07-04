package com.royalstone.pos.web.command;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.royalstone.pos.card.MemberCard;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;

/**
 * ����˴��룬������ѯ���˿�����Ϣ
 * @author liangxinbiao
 */
public class MemberCardQueryTransfer implements ICommand {

	private MemberCardQueryCommand memberCardQueryCommand =
		new MemberCardQueryCommand();

	public Object[] excute(Object[] values) {
		if (values.length == 3
			&& (values[1] instanceof String)
			&& ((values[2] instanceof String) || (values[2] == null))) {
			String host = (String) values[2];
			Object[] results = new Object[1];
			results[0] = query((String) values[1], host);
			return results;
		}

		return null;
	}

	/**
	 * ���ݿ��ź�����(�ӿ���������)��ѯ���˿�����Ϣ
	 * @param CardNo ���˿���
	 * @return ���˿���ѯֵ����
	 */
	private MemberCard query(String CardNo, String host) {

		MemberCard cardquery = new MemberCard();

		Object[] params = new Object[2];

		params[0] = "com.royalstone.pos.web.command.MemberCardQueryCommand";
		params[1] = CardNo;

		Object[] results = null;

		if (host != null && !host.equals("NoCardServer")) {

			URL servlet;
			HttpURLConnection conn;
			try {
				servlet = new URL("http://" + host + "/pos41/DispatchServlet");
			} catch (Exception ex) {
				ex.printStackTrace();
				cardquery.setExceptionInfo("���ӿ�����������,�������������");
				return cardquery;
			}

			try {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} catch (IOException ex) {
				ex.printStackTrace();
				cardquery.setExceptionInfo("��ѯ������������,�������������");
				return cardquery;
			}

		} else if (host.equals("NoCardServer")) {

			results = memberCardQueryCommand.excute(params);

		}

		if (results != null && results.length > 0) {
			cardquery = (MemberCard) results[0];
		}

		return cardquery;
	}

}
