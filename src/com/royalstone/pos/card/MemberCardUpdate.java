/*
 * Created on 2005-5-30
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.card;

import java.io.Serializable;

/**
 * @author fire
 */
public class MemberCardUpdate implements Serializable
{
	public MemberCardUpdate(){
		
	}

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getCashierid() {
        return cashierid;
    }

    public void setCashierid(String cashierid) {
        this.cashierid = cashierid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPayvalue() {
        return payvalue;
    }

    public void setPayvalue(String payvalue) {
        this.payvalue = payvalue;
    }

    public String getCdseq() {
        return cdseq;
    }

    public void setCdseq(String cdseq) {
        this.cdseq = cdseq;
    }

    public String getPoint() {
        return point;
    }
    
    public int getListno(){
    	return listno;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getPosid() {
        return posid;
    }

    public void setPosid(String posid) {
        this.posid = posid;
    }
    
    public void setListno(int listno){
    	this.listno = listno;
    	}

    private String cardno;
	private String shopid;
	private String cashierid;
	private String time;
	private String payvalue;
	private String cdseq;
    private String point;
	private String posid;
	
	// zhouzhou Add
	private int listno;

    public String getCourrentPoint() {
        return courrentPoint;
    }
 
    public void setCourrentPoint(String courrentPoint) {
        this.courrentPoint = courrentPoint;
    }

    private String courrentPoint;

}
