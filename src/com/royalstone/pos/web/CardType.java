/*
 * Created on 2004-8-2
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
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.db.PosMinister;
import com.royalstone.pos.web.util.DBConnection;

/**
 * @author Administrator
 */

public class CardType extends HttpServlet {

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		System.err.println("CardType ...");
		OutputStream out = response.getOutputStream();
		response.setContentType("text/html");

		output(out);

	}

	public void output(OutputStream out) throws IOException {

		Connection con = null;

		try {

			con = DBConnection.getConnection("java:comp/env/dbpos");

			if (con != null) {
				Element elm_card = PosMinister.getCardType(con);
				XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
				outputter.setTextTrim(true);
				outputter.output(new Document(elm_card), out);
			}

			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} finally {
			DBConnection.closeAll(null, null, con);
		}

	}
}
