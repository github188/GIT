package com.royalstone.pos.notify;

import java.io.Serializable;

/**
 * @author liangxinbiao
 */

public class NotifyItem implements Serializable{

	private int itemID;
	private String value1;
	private String value2;
	private String value3;

	/**
	 * @return
	 */
	public int getItemID() {
		return itemID;
	}

	/**
	 * @return
	 */
	public String getValue1() {
		return value1;
	}

	/**
	 * @return
	 */
	public String getValue2() {
		return value2;
	}

	/**
	 * @return
	 */
	public String getValue3() {
		return value3;
	}

	/**
	 * @param i
	 */
	public void setItemID(int i) {
		itemID = i;
	}

	/**
	 * @param string
	 */
	public void setValue1(String string) {
		value1 = string;
	}

	/**
	 * @param string
	 */
	public void setValue2(String string) {
		value2 = string;
	}

	/**
	 * @param string
	 */
	public void setValue3(String string) {
		value3 = string;
	}

}
