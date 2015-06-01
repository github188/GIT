package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class CustInfoDef implements Serializable 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String[] ref = {"custno","custname","custphone","custtel","custaddr"};
		
	public String custno;
	public String custname;
	public String custtel;
	public String custphone;
	public String custaddr;

}
