/*
 * Created on 2004-6-17
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.common;

import java.io.Serializable;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RequestAlterPin implements Serializable
{

	public static void main(String[] args) 
	{
	}
	
	public RequestAlterPin ( String posid, String cashierid, String pin_old, String pin_new)
	{
		this.posid = posid;
		this.cashierid = cashierid;
		this.pin_old = pin_old;
		this.pin_new = pin_new;
	}
	
	public String getPosid()
	{
		return posid;
	}
	
	public String getCashierid()
	{
		return cashierid;
	}
	
	public String getPinOld()
	{
		return pin_old;
	}
	
	public String getPinNew()
	{
		return pin_new;
	}
	
	public String toString()
	{
		return "cashier: " + cashierid + "; pin: " + pin_old + " -> " + pin_new + "; posid: " + posid + ";";
	}
	
	private String posid;
	private String cashierid;
	private String pin_old;
	private String pin_new;
}
