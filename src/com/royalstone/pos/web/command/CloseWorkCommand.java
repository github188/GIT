/*
 * Created on 2004-6-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.web.command;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

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
public class CloseWorkCommand implements ICommand {

	public Object[] excute(Object[] values) {
		System.out.println("CloseWorkCommand executed!");

		Connection con = null;
		Response response = null;
		Context ctx = null;
		DataSource ds = null;
		PosRequest req = null;

		Object[] results = new Object[1];
		if (values != null && values.length > 1) {
			req = (PosRequest) values[1];
			System.out.println(req);
			String posid = req.getPosid();

			try {
				ctx = new InitialContext();
				if (ctx != null)
					ds = (DataSource) ctx.lookup("java:comp/env/dbpos");
				if (ds != null)
					con = ds.getConnection();

				if (con != null) {
					System.out.println("CloseWorkCommand: ds has connected");
					WorkTurnMinister minister = new WorkTurnMinister(con);
					response = minister.closeWork(req);
				} else {
					response = new Response(-1, "未成功.");
				}

			} catch (NamingException e) {
				e.printStackTrace();
				response = new Response(-1, "数据库连接失败.");
			} catch (SQLException e) {
				e.printStackTrace();
				response = new Response(-1, "数据库连接失败.");
			} finally {
				DBConnection.closeAll(null, null, con);
			}
		}
		results[0] = response;
		return results;

	}
}
