package com.royalstone.pos.card;

import java.io.Serializable;

import org.jdom.Element;

/**
 * 挂帐卡支付的值对象要传到服务器里。里面包括要用来支付的挂账卡的一些信息，和要支付的金额等。
 * @author liangxinbiao
 */
public class LoanCardPayVO implements Serializable {
	private String cardno="";
	private String subcardno="";
	private String shopid="";
	private String cashierid="";
	private String time="";
	private String payvalue="";
	private String password="";
	private String cdseq="";
	private String posid="";
	private String sheetid="";

	public LoanCardPayVO() {
	}

	/**
	 * @return 主卡号
	 */
	public String getCardno() {
		return cardno;
	}

	/**
	 * @param cardno 主卡号
	 */
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	/**
	 * @return 店号
	 */
	public String getShopid() {
		return shopid;
	}

	/**
	 * @param shopid 店号
	 */
	public void setShopid(String shopid) {
		this.shopid = shopid;
	}

	/**
	 * @return 收银员号
	 */
	public String getCashierid() {
		return cashierid;
	}

	/**
	 * @param cashierid 收银员号
	 */
	public void setCashierid(String cashierid) {
		this.cashierid = cashierid;
	}

	/**
	 * @return 请求支付时间
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time 请求支付时间
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return 请求支付金额
	 */
	public String getPayvalue() {
		return payvalue;
	}

	/**
	 * @param payvalue 请求支付金额
	 */
	public void setPayvalue(String payvalue) {
		this.payvalue = payvalue;
	}

	/**
	 * @return 挂账卡密码
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password 挂账卡密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return 请求顺序号
	 */
	public String getCdseq() {
		return cdseq;
	}

	/**
	 * @param cdseq 请求顺序号
	 */
	public void setCdseq(String cdseq) {
		this.cdseq = cdseq;
	}

	/**
	 * @return POS机号
	 */
	public String getPosid() {
		return posid;
	}
	
	
	/**
	 * @param posid POS机号
	 */
	public void setPosid(String posid) {
		this.posid = posid;
	}
	
	/**
	 * @return 单据号
	 */
	public String getSheetid(){
		return sheetid;
	}
	
	/**
	 * @param sheetid 单据号 
	 */
	public void setSheetid(String sheetid){
		this.sheetid = sheetid;
	}

	/**
	 * @return 子卡号
	 */
	public String getSubcardno() {
		return subcardno;
	}

	/**
	 * @param string 子卡号
	 */
	public void setSubcardno(String string) {
		subcardno = string;
	}

	/**
	 * @return 代表此对象的XML的对象
	 */
	public Element toElement() {
		Element g = new Element("CardPayVO");
		g.addContent(new Element("cardno").addContent(cardno));
		g.addContent(new Element("subcardno").addContent(subcardno));
		g.addContent(new Element("shopid").addContent(shopid));
		g.addContent(new Element("cashierid").addContent(cashierid));
		g.addContent(new Element("time").addContent(time));
		g.addContent(new Element("payvalue").addContent(payvalue));
		g.addContent(new Element("password").addContent(password));
		g.addContent(new Element("cdseq").addContent(cdseq));
		g.addContent(new Element("posid").addContent(posid));
		g.addContent(new Element("sheetid").addContent(sheetid));
		

		return g;
	}

	/**
	 * @param elm 用XML对象生成LoanCardPayVO
	 */
	public LoanCardPayVO(Element elm) {
		try {
			cardno = elm.getChildTextTrim("cardno");
			subcardno = elm.getChildTextTrim("subcardno");
			shopid = elm.getChildTextTrim("shopid");
			cashierid = elm.getChildTextTrim("cashierid");
			time = elm.getChildTextTrim("time");
			payvalue = elm.getChildTextTrim("payvalue");
			password = elm.getChildTextTrim("password");
			cdseq = elm.getChildTextTrim("cdseq");
			posid = elm.getChildTextTrim("posid");
			sheetid = elm.getChildTextTrim("sheetid");

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

}
