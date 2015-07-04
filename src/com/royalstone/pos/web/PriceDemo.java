/*
 * Created on 2004-6-6
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.web;


import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;

import javax.naming.Context;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.common.GoodsList;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class PriceDemo extends HttpServlet {

	private static String RESPONSE_CONTENT_TYPE = "text/html";

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		OutputStream out =  response.getOutputStream();

		Connection con    = null;
		Context ctx       = null;
		DataSource ds     = null;

		try {
			response.setContentType(RESPONSE_CONTENT_TYPE);
			Goods g ;
			g =
				new Goods(
					"000120",
					"9787508315164",
					"JAVA ±‡≥Ã”Ô—‘",
					"223141",
					"BOOK",
					"volume",
					6500);
			GoodsList lst = new GoodsList();
			lst.add(g);
			
			XMLOutputter outputter = new XMLOutputter( "  ", true, "GB2312" );
			outputter.setTextTrim(true);
			outputter.output( new Document( lst.toElement() ), out );
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
