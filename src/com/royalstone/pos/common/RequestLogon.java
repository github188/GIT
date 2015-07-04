/*
 * Created on 2004-6-13
 */
package com.royalstone.pos.common;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * 前台模块使用 RequestLogon 对象发送交易请示给后台模块.
 * NOTE: 在进行登录操作时，系统要求操作人员输入班次号但不要求录入营业日期. 营业日期由系统决定。
 * 这一规则基于以下考虑：对于操作人员，营业日期较容易造成混淆而班次号不容易混淆；
 * 反之，对于系统而言，确定营业日期较为容易。
 * @author Mengluoyi
 */

public class RequestLogon  implements Serializable
{
	/**
	 * @param posid			POS机ID.
	 * @param cashierid		收银员ID.
	 * @param plainpin		登录口令(明文).
	 */
	public RequestLogon( String posid, String cashierid, String plainpin )
	{
		this.posid = posid;
		this.cashierid = cashierid;
		this.plainpin = plainpin;
	}
	
	/**
	 * @param posid			POS机ID.
	 * @param cashierid		收银员ID.
	 * @param plainpin		登录口令(明文).
	 * @param shiftid		班次号(1,2,3...).
	 */
	public RequestLogon( String posid, String cashierid, String plainpin, int shiftid )
	{
		this.posid 		= posid;
		this.cashierid 	= cashierid;
		this.plainpin 	= plainpin;
		this.shiftid  	= shiftid;
	}
	
	/**
	 * @param posid			POS机ID.
	 * @param cashierid		收银员ID.
	 * @param plainpin		登录口令(明文).
	 * @param shiftid		班次号(1,2,3...).
	 * @param ip			POS机IP地址.
	 */
	public RequestLogon( String posid, String cashierid, String plainpin, int shiftid ,String ip)
	{
			this.posid 		= posid;
			this.cashierid 	= cashierid;
			this.plainpin 	= plainpin;
			this.shiftid  	= shiftid;
			this.ip = ip;
	}
	
	/**
	 * @return	发出登录请求的收银机ID.
	 */
	public String getPosid()
	{
		return posid;
	}
	
	/**
	 * @return	请求登录的收银员ID.
	 */
	public String getCashierid()
	{
		return cashierid;
	}
	
	/**
	 * @return	操作员输入的口令(明文).
	 */
	public String getPlainPin()
	{
		return plainpin;
	}
	
	/**
	 * @return	请求登录的班次号(1,2,3...).
	 */
	public int getShiftid()
	{
		return shiftid;
	}
	
	/**
	 * @return	发出登录请求的收银机IP地址.
	 */
	public String getIp()
	{
			return ip;
	}
	
	/**
	 * @return	加密后的登录口令. 由于数据库中存放的口令均为密文,后台程序需要调用此方法对收银员输入的口令作加密处理后再作对比.
	 */
	public String getPinEncrypted()
	{
		long k = 123456789;
		
		for( int i=0; i < cashierid.length(); i++){
			long	a = ( (int)cashierid.charAt(i) ) % 13 + 1;
			k = ( k * a ) % 9999999 + 1;
		}
		
		k = k % 98989898 + 99;
		for(int i=0; i<  plainpin.length(); i++){
			long	a = ( (int)plainpin.charAt(i) ) % 17 + 1;
			k = ( k % 9876543 + 1 ) * a;
		}
		
		DecimalFormat df = new DecimalFormat( "00000000" );
		return df.format( k % 100000000 );
	}
	
	/**
	 * @return a string summarizing the request.
	 */
	public String toString()
	{
		return "Posid: " + posid + "; Cashierid: " + cashierid 
			+ "; pin: " + plainpin + "; shiftid=" + shiftid;
	}

	private String posid 	 = "";
	private String cashierid = "";
	private String plainpin  = "";
	private int shiftid 	 = 0;
	private String ip        = "";	
}
