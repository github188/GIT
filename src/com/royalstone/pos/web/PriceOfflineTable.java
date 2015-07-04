/*
 * Created on 2004-6-6
 *
 */

package com.royalstone.pos.web;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.common.GoodsExtList;
import com.royalstone.pos.common.GoodsList;
import com.royalstone.pos.db.PosMinister;
import com.royalstone.pos.web.util.DBConnection;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class PriceOfflineTable extends HttpServlet {

	private static String RESPONSE_CONTENT_TYPE = "text/html";

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		OutputStream out = response.getOutputStream();

		String selectType = "";
		int selectLength = 0;
		String selectMaxCode = "9999999";
		try {
			selectType = request.getParameter("selectType");
			selectLength =
				Integer.parseInt(request.getParameter("selectLength"));
			selectMaxCode = request.getParameter("selectMaxCode");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		response.setContentType(RESPONSE_CONTENT_TYPE);

		output(out, selectType, selectLength, selectMaxCode);

	}

	public void output(
		OutputStream out,
		String selectType,
		int selectLength,
		String selectMaxCode)
		throws IOException {

		Connection con = null;

		try {

			con = DBConnection.getConnection("java:comp/env/dbpos");

			if (con != null) {
				GoodsList glst = null;
				GoodsExtList glest = null;
				if (selectType.equals("price"))
					glst =
						PosMinister.getGoodsList(
							con,
							selectLength,
							selectMaxCode);
				else if (selectType.equals("priceExt"))
					glest =
						PosMinister.getGoodsExtList(
							con,
							selectLength,
							selectMaxCode);

				XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
				outputter.setTextTrim(true);
				Document doc = null;
				try {
					if (selectType.equals("price"))
						doc = new Document(glst.toElement());
					else if (selectType.equals("priceExt"))
						doc = new Document(glest.toElement());
				} catch (Exception e) {
					e.printStackTrace();
					//To change body of catch statement use Options | File Templates.
				}
				outputter.output(doc, out);
			}

			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeAll(null, null, con);
		}

	}
}
