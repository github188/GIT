package com.royalstone.pos.notify;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * @author liangxinbiao
 */
public class NotifyReceiver implements Runnable {

	private int port = 9999;

	public NotifyReceiver() {
		fromIni("pos.ini");
	}

	public void run() {
		ServerSocket ssc = null;
		Socket sc = null;
		try {
			ssc = new ServerSocket(port);
			while (true) {
				try {
					sc = ssc.accept();
					ObjectInputStream ois =
						new ObjectInputStream(sc.getInputStream());
					Notify message = (Notify) ois.readObject();
					NotifyQueue.getInstance().addNotify(message);
				} finally {
					if (sc != null) {
						sc.close();
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ssc != null) {
					ssc.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void fromIni(String file) {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(file));
			port = Integer.parseInt(prop.getProperty("listen_port"));
		} catch (NumberFormatException ex) {
			port = 9999;
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
