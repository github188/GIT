/*
 * Created on 2004-6-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.web.command;

import java.sql.Connection;
import java.sql.SQLException;

import com.royalstone.pos.common.PosRequest;
import com.royalstone.pos.db.WorkTurnMinister;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.util.DBConnection;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LogoffCommand implements ICommand {

	public LogoffCommand() {
	}

	public Object[] excute(Object[] values) {
		System.err.println("LogoffCommand executed!");

		Connection con = null;
		Response response = null;
		PosRequest req = null;

		Object[] results = new Object[1];
		if (values != null && values.length > 1) {
			req = (PosRequest) values[1];

			try {
				con = DBConnection.getConnection("java:comp/env/dbpos");

				if (con != null) {
					System.err.println("LogoffCommand: ds has connected");
					WorkTurnMinister minister = new WorkTurnMinister(con);
					response = minister.logoff(req);
				} else {
					response = new Response(-1, "退出操作不成功。");
				}

			} catch (SQLException e) {
				e.printStackTrace();
				response = new Response(-1, "退出操作不成功。");
			} finally {
				DBConnection.closeAll(null, null, con);
			}
		}
		results[0] = response;
		return results;
	}
}
