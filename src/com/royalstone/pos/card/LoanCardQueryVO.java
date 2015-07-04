package com.royalstone.pos.card;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 挂账卡查询值对象，是从服务器返回的挂账卡信息
 * @author liangxinbiao
 */
public class LoanCardQueryVO implements Serializable {

	private String cardNo;
	private String subcardNo;
	private String detail;
	private ArrayList shopIDs;
	private ArrayList deptIDs;
	private String maxOilQtyPerTrans;
	private String oilGoodsID;
	private String custName;
	private String carID;
	private String cardType;
	private String credit;
	private String mainBalance;
	private ArrayList discs;

	private String exceptioninfo;

	public LoanCardQueryVO() {
		
	}
	
	
	/**
	 * @return 余额
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * 
	 * @param detail 余额
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

	/**
	 * @return 错误信息
	 */
	public String getExceptioninfo() {
		return exceptioninfo;
	}
	
	/**
	 * @param exceptioninfo 错误信息
	 */
	public void setExceptioninfo(String exceptioninfo) {
		this.exceptioninfo = exceptioninfo;
	}

	/**
	 * @return 卡号
	 */
	public String getCardNo() {
		return cardNo;
	}

	/**
	 * @return 可消费商品小类号列表
	 */
	public ArrayList getDeptIDs() {
		return deptIDs;
	}


	/**
	 * @return 可消费店的店号列表
	 */
	public ArrayList getShopIDs() {
		return shopIDs;
	}

	/**
	 * @return 子卡号
	 */
	public String getSubcardNo() {
		return subcardNo;
	}

	/**
	 * @param string 子卡号
	 */
	public void setCardNo(String string) {
		cardNo = string;
	}

	/**
	 * @param list 可消费商品小类号列表
	 */
	public void setDeptIDs(ArrayList list) {
		deptIDs = list;
	}


	/**
	 * @param list 可消费店的店号列表
	 */
	public void setShopIDs(ArrayList list) {
		shopIDs = list;
	}

	/**
	 * @param string 子卡号
	 */
	public void setSubcardNo(String string) {
		subcardNo = string;
	}

	/**
	 * @return 一单最大消费油品数
	 */
	public String getMaxOilQtyPerTrans() {
		return maxOilQtyPerTrans;
	}

	/**
	 * @return 参考油品的商品编码
	 */
	public String getOilGoodsID() {
		return oilGoodsID;
	}

	/**
	 * @param string 一单最大消费油品数
	 */
	public void setMaxOilQtyPerTrans(String string) {
		maxOilQtyPerTrans = string;
	}

	/**
	 * @param string 参考油品的商品编码
	 */
	public void setOilGoodsID(String string) {
		oilGoodsID = string;
	}

	/**
	 * @return 车牌号
	 */
	public String getCarID() {
		return carID;
	}

	/**
	 * @return 客户名称
	 */
	public String getCustName() {
		return custName;
	}

	/**
	 * @param string 车牌号
	 */
	public void setCarID(String string) {
		carID = string;
	}

	/**
	 * @param string 客户名称
	 */
	public void setCustName(String string) {
		custName = string;
	}

	/**
	 * @return 卡类型
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * @param string 卡类型
	 */
	public void setCardType(String string) {
		cardType = string;
	}

	/**
	 * @return 信用额度
	 */
	public String getCredit() {
		return credit;
	}

	/**
	 * @param string 信用额度
	 */
	public void setCredit(String string) {
		credit = string;
	}

	/**
	 * @return 主卡余额
	 */
	public String getMainBalance() {
		return mainBalance;
	}

	/**
	 * @param string 主卡余额
	 */
	public void setMainBalance(String string) {
		mainBalance = string;
	}

	/**
	 * @return
	 */
	public ArrayList getDiscs() {
		return discs;
	}

	/**
	 * @param list
	 */
	public void setDiscs(ArrayList list) {
		discs = list;
	}

}
