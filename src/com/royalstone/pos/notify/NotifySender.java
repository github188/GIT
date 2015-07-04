package com.royalstone.pos.notify;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.ArrayList;

import com.royalstone.pos.notify.db.NotifyGateWate;

/**
 * @author liangxinbiao
 */

public class NotifySender {

	private Connection conn = null;

	public NotifySender(Connection conn) {
		this.conn = conn;
	}

	public void send() {

		NotifyGateWate notifyGateWate = NotifyGateWate.getInstance();
		notifyGateWate.setConnection(conn);

		ArrayList notifyPosList = notifyGateWate.findNotifyPOS();

		for (int i = 0; i < notifyPosList.size(); i++) {

			NotifyPos notifyPos = (NotifyPos) notifyPosList.get(i);

			ArrayList notifyList =
				notifyGateWate.findNotifyList(notifyPos.getPosID());
			for (int j = 0; j < notifyList.size(); j++) {

				Notify notify = (Notify) notifyList.get(j);
				sendNotify(notifyPos, notify, notifyGateWate);
			}
		}

	}

	public void sendNotify(
		NotifyPos notifyPos,
		Notify notify,
		NotifyGateWate notifyGateWate) {

		Socket sc = null;

		try {
			sc = new Socket(notifyPos.getIp(), notifyPos.getPort());
			ObjectOutputStream oos =
				new ObjectOutputStream(sc.getOutputStream());
			oos.writeObject(notify);
			oos.flush();
			oos.close();
			notifyGateWate.addNotifiedPos(notify, notifyPos);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(
				"Cound not connect to "
					+ notifyPos.getPosID()
					+ "("
					+ notifyPos.getIp().trim()
					+ ":"
					+ notifyPos.getPort()
					+ ")");
		} finally {
			if (sc != null)
				try {
					sc.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
	}

}
