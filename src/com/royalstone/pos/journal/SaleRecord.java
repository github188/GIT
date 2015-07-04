/*
 * Created on 2004-6-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.journal;

import java.io.Serializable;

import org.jdom.Element;

import com.royalstone.pos.util.Day;
import com.royalstone.pos.util.PosTime;

/**
 * SaleRecord 表示商品销售流水记录. 
 * POS 系统采用以下方式完成销售流水记录: 销售过程中,客户端程序先把销售流水写入本地硬盘. 
 * 由一个独立线程读本地的流水文件, 把其中的记录(SaleRecord, PayRecord)送到后台,由后台Servlet写入数据库中.
 * 前台线程在把数据送到后台的过程中使用对象序列化(Serialization)机制.
 *   
 * @author Mengluoyi
 *
 */
public class SaleRecord  implements Serializable
{

	public static void main(String[] args) {
	}
	
	public SaleRecord( Element elm )
	{
		trainflag 		= elm.getChild( "trainflag" ).getTextTrim();
		vgno 			= elm.getChild( "vgno" ).getTextTrim();
		barcode 		= elm.getChild( "barcode" ).getTextTrim();
		orgcode 		= elm.getChild( "orgcode" ).getTextTrim();
		deptid 			= elm.getChild( "deptid" ).getTextTrim();
		type 			= elm.getChild( "type" ).getTextTrim();
		disctype 		= elm.getChild( "disctype" ).getTextTrim();
		waiter 			= elm.getChild( "waiter" ).getTextTrim();
		authorizer 		= elm.getChild( "authorizer" ).getTextTrim();
		placeno 		= elm.getChild( "placeno" ).getTextTrim();
		colorsize 		= elm.getChild( "colorsize" ).getTextTrim();

		qty 			= atoi( elm.getChild( "qty" ).getTextTrim() );
		stdprice 		= atoi( elm.getChild( "stdprice" ).getTextTrim() );
		x 				= atoi( elm.getChild( "x" ).getTextTrim() );
		itemvalue 		= atoi( elm.getChild( "itemvalue" ).getTextTrim() );
		discvalue 		= atoi( elm.getChild( "discvalue" ).getTextTrim() );
		factvalue 		= atoi( elm.getChild( "factvalue" ).getTextTrim() );
		sysdate 		= str2day ( elm.getChild( "sysdate" ).getTextTrim() );
		systime 		= str2time( elm.getChild( "systime" ).getTextTrim() );
	}

	public String trainflag(){ return trainflag; }
	public String vgno(){ return vgno; }
	public String barcode(){ return barcode; }
	public String orgcode(){ return orgcode; }
	public String deptid(){ return deptid; }
	public String type(){ return type; }
	public String disctype(){ return disctype; }
	public String waiter(){ return waiter; }
	public String authorizer(){ return authorizer; }
	public String placeno() { return placeno; }
	public String colorsize() { return colorsize; }
	public int qty() { return qty; }
	public int stdprice(){ return stdprice; }
	public int itemvalue(){ return itemvalue; }
	public int discvalue(){ return discvalue; }
	public int factvalue(){ return factvalue; }
	public int x() { return x;}
	public Day sysdate(){ return sysdate; }
	public PosTime systime(){ return systime; }
	
	public String toString ()
	{
		return vgno + " * " + qty + " at " + systime.toString() + " on " + sysdate.toString() ;
	}


/////////////////// internal routines ////////////////////
	private static Day str2day( String sysdate )
	{
		return new Day( atoi( sysdate.substring(0,4)), 
			atoi(sysdate.substring(5,7) ), atoi( sysdate.substring(8,10) ) );
	}
	
	private static PosTime str2time( String systime )
	{
		return new PosTime( atoi(systime.substring(0,2)), 
			atoi(systime.substring(3,5)), atoi(systime.substring(6,8)) );
	}

	private static int atoi(String s) 
	{
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw e;
		}
	}
	
	private String trainflag;
	private String vgno ;
	private String barcode;
	private String orgcode;
	private String deptid;
	private int qty;
	private String type;
	private String disctype;
	private String waiter;
	private String authorizer;
	private String placeno;
	private String colorsize;
	private int stdprice, x;
	private int itemvalue;
	private int discvalue;
	private int factvalue;
	private Day	   sysdate;
	private PosTime systime;
}
