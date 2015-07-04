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

import com.royalstone.pos.common.AccurateList;
import com.royalstone.pos.db.PosMinister;
import com.royalstone.pos.web.util.DBConnection;

/**
 * Created by IntelliJ IDEA.
 * User: fire
 * Date: 2005-5-27
 */
public class AccurateTable extends HttpServlet {

	private static String RESPONSE_CONTENT_TYPE = "text/html";

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		OutputStream out = response.getOutputStream();
		response.setContentType(RESPONSE_CONTENT_TYPE);

		output(out);

	}

	public void output(OutputStream out) throws IOException {

		Connection con = null;

		try {

			con = DBConnection.getConnection("java:comp/env/dbpos");

			if (con != null) {
				AccurateList alst = PosMinister.getAccurateList(con);
				XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
				outputter.setTextTrim(true);
				outputter.output(new Document(alst.toElement()), out);
			}

			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeAll(null, null, con);
		}

	}
}
