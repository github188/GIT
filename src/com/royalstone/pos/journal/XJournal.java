/*
 * Created on 2004-6-3
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.journal;

import java.io.*;
import java.sql.Connection;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.royalstone.pos.common.Payment;
import com.royalstone.pos.common.Sale;
import com.royalstone.pos.core.PaymentList;
import com.royalstone.pos.core.SaleList;
import com.royalstone.pos.util.Day;
import com.royalstone.pos.util.PosTime;

/**
 * @deprecated
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XJournal 
{

	public static void main(String[] args) throws Exception
	{
		Element root = XJournal.getRoot( "001.xml" );
		PosJournal j = new PosJournal(root);
		DataSource datasrc = new DataSource( "172.16.7.163", 1433, "ApplePos" );
		Connection con = datasrc.open( "sa", "sa" );
		j.save( con );
	}

	public static void unload( String file )
	{
	}


	
	/**
	 * 
	 * @param 	file XML file name.
	 * @return	PaymentList contained in file.
	 */
	public static PaymentList Xml2PaymentList( String file )
	{
		PaymentList pay_list = new PaymentList();
		try{
			
			Element elm_plist = getRoot( file ).getChild("sheet").getChild( "paymentlist");
			List list = elm_plist.getChildren( "payment" );

			for(int i = 0; i < list.size(); i++) {
				Element item = (Element) list.get(i);
				Payment p    = Element2Payment(item);
				if( p != null ) System.out.println( p );
				if( p != null ) pay_list.add( p );
			}

System.out.println( "Name: " + elm_plist.getName() );
			return pay_list;
		}
		catch( Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Element getRoot( String file )
	{
		try{
			Document  doc    = (new SAXBuilder()).build(new FileInputStream( file ) );
			Element   root   = doc.getRootElement();
			return root;
		}
		catch ( Exception e ){
			e.printStackTrace();
		}
		return null;
	}
	
	public static SaleList Element2SaleList( Element elm )
	{
		return null;
	}
	
	public static Sale Element2Sale( Element elm )
	{
		String vgno 		= elm.getChild( "vgno" ).getTextTrim();
		String barcode 		= elm.getChild( "barcode" ).getTextTrim();
		String orgcode 		= elm.getChild( "orgcode" ).getTextTrim();
		String deptid 		= elm.getChild( "deptid" ).getTextTrim();
		String qty 			= elm.getChild( "qty" ).getTextTrim();
		String type 		= elm.getChild( "type" ).getTextTrim();
		String disctype 	= elm.getChild( "disctype" ).getTextTrim();
		String waiter 		= elm.getChild( "waiter" ).getTextTrim();
		String authrizer 	= elm.getChild( "authrizer" ).getTextTrim();
		String placeno 		= elm.getChild( "placeno" ).getTextTrim();
		String stdprice 	= elm.getChild( "stdprice" ).getTextTrim();
		String discvalue 	= elm.getChild( "discvalue" ).getTextTrim();
		String factvalue 	= elm.getChild( "factvalue" ).getTextTrim();
		String sysdate 		= elm.getChild( "sysdate" ).getTextTrim();
		String systime 		= elm.getChild( "systime" ).getTextTrim();
		
		return null;
	}
	
	public static Payment Element2Payment(Element elm )
	{
		String reason 		= elm.getChild( "reason" ).getTextTrim();
		String type   		= elm.getChild( "type" ).getTextTrim();
		String currency   	= elm.getChild( "currency" ).getTextTrim();
		String value   		= elm.getChild( "value" ).getTextTrim();
		String value_equiv	= elm.getChild( "value_equiv" ).getTextTrim();
		String cardno    	= elm.getChild( "cardno" ).getTextTrim();
		String sysdate   	= elm.getChild( "sysdate" ).getTextTrim();
		String systime   	= elm.getChild( "systime" ).getTextTrim();

		return new Payment( 
			(int) reason.charAt(0),
			(int) type.charAt(0),
			currency,
			atoi(value),
			atoi(value_equiv),
			cardno,
			str2date(sysdate),
			str2time(systime) );
	}
	
	private static Day str2date( String sysdate )
	{
		return new Day( atoi( sysdate.substring(0,4)), 
			atoi(sysdate.substring(5,7) ), atoi( sysdate.substring(8,10) ) );
	}
	
	private static PosTime str2time( String systime )
	{
		return new PosTime( atoi(systime.substring(0,2)), 
			atoi(systime.substring(3,5)), atoi(systime.substring(6,8)) );
	}

	public static PaymentList Element2PaymentList( Element elm )
	{
		return null;
	}
	
	// basic method for toutine work
	private static int atoi(String s) 
	{
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw e;
		}
	}
}
