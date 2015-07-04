/*
 * Created on 2005-5-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.pos.card;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.PosConfig;
import com.royalstone.pos.web.command.MemberCardQueryTransfer;
import com.royalstone.pos.web.command.MemberCardUpdateTransfer;

/**
 * @author yaopoqing
 */
public class MemberCardMgrImpl implements MemberCardMgr {
	private URL servlet;
	private HttpURLConnection conn;

	private MemberCardQueryTransfer memberCardQueryTransfer =
		new MemberCardQueryTransfer();
	private MemberCardUpdateTransfer memberCardUpdateTransfer =
		new MemberCardUpdateTransfer();

	public MemberCardMgrImpl() throws Exception {
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
			throw (ex);
		}
	}

	public MemberCardMgrImpl(URL servlet) {
		this.servlet = servlet;
	}

	public MemberCard query(String cardNo) throws IOException {
		MemberCard result = null;
		try {

			Object[] params = new Object[3];

			params[0] =
				"com.royalstone.pos.web.command.MemberCardQueryTransfer";
			params[1] = cardNo;
			params[2] = PosConfig.getInstance().getString("CARDHOST_IP");

			Object[] results = null;

			if (pos.hasServerCard) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = memberCardQueryTransfer.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (MemberCard) results[0];
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			throw (ex);
		}

		return result;
	}

	public String updateCardInfo(MemberCardUpdate memberCard)
		throws IOException {
		String result = null;

		try {

			Object[] params = new Object[3];

			params[0] =
				"com.royalstone.pos.web.command.MemberCardUpdateTransfer";
			params[1] = memberCard;
			params[2] = PosConfig.getInstance().getString("CARDHOST_IP");

			Object[] results = null;

			if (pos.hasServerCard) {

				conn = (HttpURLConnection) servlet.openConnection();

				MarshalledValue mvI = new MarshalledValue(params);
				MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

				if (mvO != null) {
					results = mvO.getValues();
				}

			} else {

				results = memberCardUpdateTransfer.excute(params);

			}

			if (results != null && results.length > 0) {
				result = (String) results[0];
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return result;

	}

	/* £¨·Ç Javadoc£©
	 * @see com.royalstone.pos.card.MemberCardMgr#activation(java.lang.String, java.lang.String)
	 */
	public MemberCard activation(String cardNo, String secrety) throws IOException {
		
		return null;
	}

}
