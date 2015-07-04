/*
 * Good Day;
 */
package com.royalstone.pos.common;

import java.io.Serializable;

/**
 * @author wubingyan
 * Created by King_net  on 2005-7-19  16:10:39
 */
public class BankCardTransReturnValue implements Serializable{

	private String returnValue;
	private String transType;
	
	public BankCardTransReturnValue(String returnValue){
		this.returnValue=returnValue;
	}
	
	public String getValue(){
		return returnValue==null?"":returnValue;
	}
	
	public String getTransResultCode(){
		return returnValue.substring(0,6);
	}
	public String getShopNO(){
		return returnValue.substring(6,21);
	}
	public String getTerminalNO(){
		return returnValue.substring(21,29);
	}
	public String getBankNO(){
		return returnValue.substring(29,31);
	}
	public String getCardNO(){
		return returnValue.substring(31,50);
	}
	public String getCenterSerailNO(){
		
		return returnValue.substring(50,62);
	}
	public String getTransDate(){
		return returnValue.substring(62,66);
	}
	public String getTransTime(){
		return returnValue.substring(66,72);
	}
	public String getCardValidDate(){
		return returnValue.substring(72,76);
	}
	public String getWholeSerialNO(){
		return returnValue.substring(76,82);
	}
	public String getShopSerialNO(){
		return returnValue.substring(82,88);
	}
	public String getAuthNO(){
		return returnValue.substring(88,94);
	}
	public String getTransSum(){
		return returnValue.substring(94,106);
	}
	
	/**
	 * @return
	 */
	public String getTransType() {
		return transType;
	}

	/**
	 * @param string
	 */
	public void setTransType(String string) {
		transType = string;
	}

}
