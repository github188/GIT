/*
 * Created on 2004-6-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.web.command;

import java.sql.Connection;

import com.royalstone.pos.db.DiscMinister;
import com.royalstone.pos.favor.DiscountList;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.util.DBConnection;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DiscountCommand implements ICommand {

	public DiscountCommand() {
	}

	public Object[] excute(Object[] values) {
		Object[] results = new Object[1];
		Connection con = null;
		Response response = null;

		if (values != null) {
			try {

				con = DBConnection.getConnection("java:comp/env/dbpos");

				if (con != null) {
					System.out.println("dbpos connected!");
					DiscMinister minister = new DiscMinister(con);
					DiscountList list = minister.getDiscountList(con);
					response = new Response(0, "OK", list);
				} else {
					response = new Response(-1, "Database connection failed.");
				}

				results[0] = response;
				return results;
			} catch (Exception e) {
				e.printStackTrace();
				results[0] = new Response(-1, "Failed.");
			} finally {
				DBConnection.closeAll(null, null, con);
				return results;
			}
		}

		return null;
	}
}
