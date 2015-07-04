package com.royalstone.pos.web;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.royalstone.pos.web.util.DBConnection;
/**
 * fire
 */

public class PriceSize extends HttpServlet {

	private static String RESPONSE_CONTENT_TYPE =
		"application/x-java-serialized-object";

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {

		response.setContentType(RESPONSE_CONTENT_TYPE);

		ServletOutputStream sos = response.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(sos);

		output(oos);

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		System.out.println("fgdgdfg");
	}

	public void output(ObjectOutputStream oos) {

		Connection con = null;

		String priceSize = "0";
		String priceExtSize = "0";
		ResultSet rs = null;
		ResultSet rsExt = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtExt = null;

		try {

			con = DBConnection.getConnection("java:comp/env/dbpos");

			if (con != null) {
				pstmt =
					con.prepareStatement(
						" SELECT count(*)as priceSize FROM price_lst; ");

				rs = pstmt.executeQuery();
				if (rs.next())
					priceSize = rs.getString(1);
				pstmtExt =
					con.prepareStatement(
						" SELECT count(*)as priceSize FROM price_lst,goodsext6 where goodsext6.vgno=price_lst.vgno; ");
				rsExt = pstmtExt.executeQuery();
				if (rsExt.next())
					priceExtSize = rsExt.getString(1);
			}

			oos.writeObject(priceSize + "," + priceExtSize);
			oos.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
			if (rsExt != null)
				try {
					rsExt.close();
				} catch (SQLException e) {
				}
			if (pstmtExt != null)
				try {
					pstmtExt.close();
				} catch (SQLException e) {
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			DBConnection.closeAll(null, null, con);
		}

	}

}
