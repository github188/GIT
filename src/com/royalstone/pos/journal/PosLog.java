/*
 * Created on 2004-8-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.pos.journal;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import org.jdom.Element;



/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PosLog implements Serializable {
	
	public static void main(String[] args) {
	}

	public PosLog(Element root) {
		List list;
		Element elm_list;
		try {
			Element elm_log = root.getChild("log_lst");
			shop_id = elm_log.getChild("shop_id").getTextTrim();
			pos_id = elm_log.getChild("pos_id").getTextTrim();
			edate = elm_log.getChild("edate").getTextTrim();
			etime = elm_log.getChild( "etime" ).getTextTrim();
			action = elm_log.getChild("action").getTextTrim();
			resultflag = elm_log.getChild("resultflag").getTextTrim();
			cashier_id = elm_log.getChild("cashier_id").getTextTrim();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save(Connection connection) throws Exception {
		try {
			PreparedStatement pstmt;
			boolean duplicated = isDuplicated(connection);

			connection.setAutoCommit(false);

			if (!duplicated)
				writeLog(connection);
			
			connection.commit();

		} catch (Exception e) {
			e.printStackTrace();
			connection.rollback();
			throw e;
		}
	}
	
	public boolean isDuplicated(Connection connection) throws Exception {
		boolean dup = false;
		try {
			PreparedStatement qstmt =
				connection.prepareStatement(
					" SELECT cashier_id FROM log_lst "
						+ " WHERE pos_id = ? AND edate = ? AND etime = ? ; ");

			qstmt.setString(1, pos_id);
			qstmt.setString(2, edate);
            qstmt.setString(3, etime);

			ResultSet rs = qstmt.executeQuery();
			if (rs.next())
				dup = true;
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return dup;
	}
	
	public void writeLog(Connection connection) throws Exception {
		PreparedStatement pstmt;

		try {
			pstmt =
				connection.prepareStatement(
					"INSERT INTO log_lst ( shop_id, pos_id, edate, etime, action, resultflag, cashier_id ) "
						+ " VALUES "
						+ "( ?, ?, ?, ?, ?, ? , ? ); ");

			pstmt.setString(1, shop_id);
			pstmt.setString(2, pos_id);
			pstmt.setString(3, edate);
			pstmt.setString(4, etime);
			pstmt.setString(5, action);
			pstmt.setString(6, resultflag);
			pstmt.setString(7, cashier_id);
			pstmt.addBatch();
			pstmt.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	String shop_id;
	String pos_id;
	String cashier_id;
	String edate;
	String etime;
	String action;
	String resultflag;
	
	
}
