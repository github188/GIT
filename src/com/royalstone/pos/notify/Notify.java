package com.royalstone.pos.notify;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author liangxinbiao
 */

public class Notify implements Serializable{
	
	public final static int CHANGE_PRICE=0;
	
	private int notifyID;
	private int nofityType;
	private Date notifyTime;
	private String descript;
	private ArrayList notifyItemList;
	
	/**
	 * @return
	 */
	public String getDescript() {
		return descript;
	}

	/**
	 * @return
	 */
	public int getNofityType() {
		return nofityType;
	}

	/**
	 * @return
	 */
	public int getNotifyID() {
		return notifyID;
	}

	/**
	 * @return
	 */
	public Date getNotifyTime() {
		return notifyTime;
	}

	/**
	 * @param string
	 */
	public void setDescript(String string) {
		descript = string;
	}

	/**
	 * @param i
	 */
	public void setNofityType(int i) {
		nofityType = i;
	}

	/**
	 * @param i
	 */
	public void setNotifyID(int i) {
		notifyID = i;
	}

	/**
	 * @param date
	 */
	public void setNotifyTime(Date date) {
		notifyTime = date;
	}

	/**
	 * @return
	 */
	public ArrayList getNotifyItemList() {
		return notifyItemList;
	}

	/**
	 * @param list
	 */
	public void setNotifyItemList(ArrayList list) {
		notifyItemList = list;
	}

}
