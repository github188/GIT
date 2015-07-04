package com.royalstone.pos.util;

import org.jdom.Element;



/**
   @version 1.0 2004.05.12
   @author  Mengluoyi, Royalstone Co., Ltd.
 **/

public class Exchange
{
	public Exchange( String c, double r )
	{
		code = new String( c );
		rate = r;
	}

	public Exchange( Exchange e )
	{
		code = e.code;
		rate = e.rate;
	}

	public Exchange( Element elm ) throws InvalidDataException
	{
		try{
			this.code = elm.getChild( "code" ).getTextTrim();
			this.rate = Double.parseDouble( elm.getChild( "rate" ).getTextTrim() ); 
		} catch ( Exception e ){
			throw new InvalidDataException( "CashBox" );
		}
	}

	public Element toElement()
	{
		Element e = new Element( "exchange" );
		e.addContent( new Element( "code" ).addContent( code ) );
		e.addContent( new Element( "rate" ).addContent( "" + rate ) );
		return e;
	}

	public String getCode()
	{
		return new String (code);
	}

	public double getRate()
	{
		return rate;
	}

	public boolean matches( String c )
	{
		return code.equals( c );
	}

	public boolean matches( Currency c )
	{
		return code.equals( c.getCode() );
	}

	public String toString ()
	{
		return code + " @ " + rate;
	}
	
	private String code;
	private double rate;
}
