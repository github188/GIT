package com.royalstone.pos.notify;

/**
 * @author liangxinbiao
 */

public class NotifyPos {

	String posID;
	String ip;
	int port;

	/**
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return
	 */
	public String getPosID() {
		return posID;
	}

	/**
	 * @param string
	 */
	public void setIp(String string) {
		ip = string;
	}


	/**
	 * @param string
	 */
	public void setPosID(String string) {
		posID = string;
	}

	/**
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param i
	 */
	public void setPort(int i) {
		port = i;
	}

}
