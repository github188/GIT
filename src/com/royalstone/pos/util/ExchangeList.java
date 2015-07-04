package com.royalstone.pos.util;
import java.util.*;

import java.io.*;

import org.jdom.*;
import org.jdom.input.*;

/** 
   @version 1.0 2004.05.12
   @author  Mengluoyi, Royalstone Co., Ltd.
 **/

public class ExchangeList
{

	public static void main(String[] args)
	{
		ExchangeList lst = null;
		Document doc;

		try {
			doc = (new SAXBuilder()).build(new FileInputStream("pos.xml"));
			Element   root   = doc.getRootElement();
			Element   elm_lst	 = root.getChild( "exchange_list" );
			lst = new ExchangeList( elm_lst );
			
			for( int i=0; i< lst.rate_list.size(); i++ ){
				Exchange exch = (Exchange) lst.rate_list.get(i);
				System.out.println( exch.toString()) ;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch ( InvalidDataException e ){
			e.printStackTrace();
		}

	}


	public ExchangeList()
	{
		rate_list = new Vector();
	}

	public ExchangeList( Element elm_lst ) throws InvalidDataException
	{
		try{
			rate_list = new Vector();
			List      parms  = elm_lst.getChildren( "exchange" );
			Iterator iter = parms.iterator();
			for(int i = 0; iter.hasNext(); i++){
				Element item     = (Element) iter.next();
				Element elm_code = item.getChild("code");
				Element elm_rate = item.getChild("rate");

				String  code = elm_code.getTextTrim();
				double  rate = Double.parseDouble( elm_rate.getTextTrim() );
				this.add( code, rate );				
			}
			
/*		for (int i = 0; i < parms.size(); i++)
			{
				Element item     = (Element) parms.get(i);
				Element elm_code = item.getChild("code");
				Element elm_rate = item.getChild("rate");

				String  code = elm_code.getTextTrim();
				double  rate = Double.parseDouble( elm_rate.getTextTrim() );
				this.add( code, rate );
			}
*/
			if( ! isValid() ) throw new InvalidDataException( "Invalid exchange list!" );
		} catch ( NullPointerException e ){
			throw new InvalidDataException( "Invalid exchange list!" );		
		}
	}

	/**
	   @param file xml  containing currency exchange rate list.
	 */
	public void fromXML( String file )
	{
		try{
			Document  doc    = (new SAXBuilder()).build(new FileInputStream( file ) );
			Element   root   = doc.getRootElement();
			Element   config = root.getChild( "exchange_list" );
			List      parms  = config.getChildren( "exchange" );

			for (int i = 0; i < parms.size(); i++)
			{
				Element item     = (Element) parms.get(i);
				Element elm_code = item.getChild("code");
				Element elm_rate = item.getChild("rate");

				String  code = elm_code.getTextTrim();
				double  rate = Double.parseDouble( elm_rate.getTextTrim() );
				this.add( code, rate );
			}
		}
		catch ( Exception e ){
			e.printStackTrace();
		}
	}

	public void add( Exchange e )
	{
		rate_list.add( e );
	}

	/**
	   @param code currency code like RMB, HKD, USD ...
	   @param rate exchange rate value.
	   @return exchange rate value. If not found return 0.
	 */
	public void add( String code, double rate )
	{
		rate_list.add( new Exchange( code, rate ) );
	}

	/**
	   @param code currency code like RMB, HKD, USD ...
	   @return exchange rate value. If not found return 0.
	 */
	public double getRate( String code ) throws InvalidDataException
	{
		for( int i=0; i < size(); i++ ) {
			Exchange e = (Exchange) rate_list.get(i);
			if ( e.matches( code ) ) return e.getRate();
		}
		throw new InvalidDataException( code );
	}
	
	private boolean isValid()
	{
		if( rate_list != null ) 
			for( int i =0; i< rate_list.size(); i++ ){
				Exchange e = (Exchange) rate_list.get(i);
				if ( e.matches( "RMB" ) && e.getRate() == 1 ) return true;				
			}
		return false;
	}

	public double getRate( Currency c )
	{
		for( int i=0; i < size(); i++ ) {
			Exchange e = (Exchange) rate_list.get(i);
			if ( e.matches( c ) ) return e.getRate();
		}
		return 0;
	}

	public Exchange find( String code )
	{
		for( int i=0; i < size(); i++ ) {
			Exchange e = (Exchange) rate_list.get(i);
			if ( e.matches( code ) ) return e;
		}
		return null;
	}

	public int size()
	{
		return rate_list.size();
	}

	public Element toElement()
	{
		Element elm_list = new Element( "exchange_list" );
		for( int i=0; i<rate_list.size(); i++ ) 
			elm_list.addContent( ( (Exchange) rate_list.get(i) ).toElement() );
		return elm_list;
	}

	private Vector rate_list;
}
