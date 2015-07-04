package com.royalstone.pos.notify.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.royalstone.pos.notify.Notify;
import com.royalstone.pos.notify.NotifyItem;
import com.royalstone.pos.notify.NotifyPos;

/**
 * @author liangxinbiao
 */

public class NotifyGateWate {

	private static NotifyGateWate instance = new NotifyGateWate();

	private Connection conn = null;

	private String findNotifyListSql =
		"select * from notify,notifyitem"
			+ " where notify.notifyid=notifyitem.notifyid"
			+ " and notify.notifyid not in (select notifyid from notifiedpos where posid=?)";

	private String findNotifyPOSSql =
		"select pos_id,ip,isnull(port,9999) as port from pos_lst where not (current_op is null)";

	private String addNotifiedPosSql =
		"insert into notifiedpos (notifyID,posID) values (?,?)";

	private String addNotifyPosSql =
		"insert into notifypos (posID,ip,port) values (?,?,?)";

	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	public static NotifyGateWate getInstance() {
		return instance;
	}

	public ArrayList findNotifyPOS() {
		ArrayList result = new ArrayList();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(findNotifyPOSSql);
			while (rs.next()) {
				String posID = rs.getString("POS_ID");
				String ip = rs.getString("Ip");
				int port = rs.getInt("Port");
				NotifyPos notifyPos = new NotifyPos();
				notifyPos.setPosID(posID);
				notifyPos.setIp(ip);
				notifyPos.setPort(port);
				result.add(notifyPos);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList findNotifyList(String posID) {
		ArrayList result = null;

		try {
			PreparedStatement pstmt = conn.prepareStatement(findNotifyListSql);
			pstmt.setString(1, posID);
			ResultSet rs = pstmt.executeQuery();

			int notifyID = 0;

			HashMap hashMap = new HashMap();

			while (rs.next()) {

				notifyID = rs.getInt("NotifyID");
				int notifyType = rs.getInt("NotifyType");
				Date notifyTime = rs.getTimestamp("NotifyTime");
				String descript = rs.getString("Descript");

				Notify notify = (Notify) hashMap.get(new Integer(notifyID));
				if (notify == null) {

					notify = new Notify();
					notify.setNotifyID(notifyID);
					notify.setNofityType(notifyType);
					notify.setNotifyTime(notifyTime);
					notify.setDescript(descript);
					notify.setNotifyItemList(new ArrayList());

					hashMap.put(new Integer(notifyID), notify);

				}

				int itemID = rs.getInt("itemID");
				String value1 = rs.getString("Value1");
				String value2 = rs.getString("Value2");
				String value3 = rs.getString("Value3");
				NotifyItem notifyItem = new NotifyItem();
				notifyItem.setItemID(itemID);
				notifyItem.setValue1(value1);
				notifyItem.setValue2(value2);
				notifyItem.setValue3(value3);
				notify.getNotifyItemList().add(notifyItem);

			}
			result = new ArrayList(hashMap.values());

		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (result == null)
			result = new ArrayList();
		return result;
	}

	/**
	 * @param notify
	 * @param notifyPos
	 */
	public void addNotifiedPos(Notify notify, NotifyPos notifyPos) {

		try {
			PreparedStatement pstmt = conn.prepareStatement(addNotifiedPosSql);
			pstmt.setInt(1, notify.getNotifyID());
			pstmt.setString(2, notifyPos.getPosID());
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void addNotifyPos(NotifyPos notifyPos) {

		try {
			PreparedStatement pstmt = conn.prepareStatement(addNotifiedPosSql);
			pstmt.setString(1, notifyPos.getPosID());
			pstmt.setString(2, notifyPos.getIp());
			pstmt.setInt(3, notifyPos.getPort());
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
