
package com.royalstone.pos.util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jdom.Element;

/**
 * PosTime 针对POS 系统中以下场合的需要而设计:
 * 取当前系统时间;
 * 把时间转换为惯用格式( hh:mm:ss ).
 * 与JDBC 中的类 java.sql.Time 互转换.
 * 与XML节点互转换.
 * 与通用类GregorianCalendar转换.
   @version 1.0 2004.05.27
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class PosTime implements Serializable
{
	public static void main(String[] args)
	{
		System.out.println( new PosTime() );
	}

	public PosTime()
	{
		GregorianCalendar now = new GregorianCalendar(); 
		hour   = now.get(Calendar.HOUR_OF_DAY);
		minute = now.get(Calendar.MINUTE);
		second = now.get(Calendar.SECOND);
	}
   
	public PosTime(GregorianCalendar gDate)
	{   
		hour   = gDate.get(Calendar.HOUR_OF_DAY);
		minute = gDate.get(Calendar.MINUTE);
		second = gDate.get(Calendar.SECOND);
	}

	public PosTime( PosTime t )
	{
		this.hour = t.getHours();
		this.minute = t.getMinutes();
		this.second = t.getSeconds();
      		if (!isValid()) 
         		throw new IllegalArgumentException();
	}

	public PosTime( int hour, int minute )
	{
		this.hour = hour;
		this.minute = minute;
		this.second = 0;
      		if (!isValid()) 
         		throw new IllegalArgumentException();
	}

	public PosTime( int hour, int minute, int second )
	{
		this.hour = hour;
		this.minute = minute;
		this.second = second;
      		if (!isValid()) 
         		throw new IllegalArgumentException();
	}
	
	/**
	 * 把JDBC 的查询结果转换为PosTime.
	 * @param sqltime JDBC 查询结果.
	 */
	public PosTime( java.sql.Time sqltime )
	{
		GregorianCalendar calender=new GregorianCalendar();
		calender.setTime(sqltime);
		hour   = calender.get(Calendar.HOUR_OF_DAY);
		minute = calender.get(Calendar.MINUTE);
		second = calender.get(Calendar.SECOND);

	}
   
	/**
	 * 把XML中的时间转换为PosTime.
	 * @param elm	XML element.
	 * @throws InvalidDataException
	 */
	public PosTime( Element elm ) throws InvalidDataException
	{
	 try{
		hour = Integer.parseInt( elm.getChild( "hour" ).getTextTrim() );
		minute = Integer.parseInt( elm.getChild( "minute" ).getTextTrim() );
		second = Integer.parseInt( elm.getChild( "second" ).getTextTrim() );
	 }
	 catch( Exception e){
		 throw new InvalidDataException();
	 }

	 if (!isValid()) 
		throw new InvalidDataException();
	}
	
	/**
	 * @return XML element representing PosTime.
	 */
	public Element toElement()
	{
		Element elm = new Element( "time" );
		Element h = ( new Element( "hour" )).addContent( "" + hour );
		Element m = ( new Element( "minute" )).addContent( "" + minute );
		Element s = ( new Element( "second" )).addContent( "" + second );
		elm.addContent( h );
		elm.addContent( m );
		elm.addContent( s );
		return elm;
	}

	public int getHours()
	{
		return hour;
	}

	public int getMinutes()
	{
		return minute;
	}

	public int getSeconds()
	{
		return second;
	}

	/**
	 * 转换为显示格式.
	 * @return 以冒号分隔的惯用格式.
	 */
	public String toString()
	{
		DecimalFormat df = new DecimalFormat( "00" );
		return df.format( hour ) + ":" + df.format( minute ) + ":" + df.format( second );
	}

	public String LtoString(){
		DecimalFormat df = new DecimalFormat( "00" );
		return df.format( hour ) + df.format( minute ) + df.format( second );
	}
	
	
	public int compareTo( PosTime b )
	{
		return ( this.getValue() - b.getValue() );
	}

	public boolean before( PosTime b )
	{
		return ( this.getValue() < b.getValue() );
	}

	public boolean after( PosTime b )
	{
		return ( this.getValue() > b.getValue() );
	}

	public boolean equals( PosTime b )
	{
		return ( this.getValue() == b.getValue() );
	}

	private int getValue()
	{
		return ( hour * 3600 + minute * 60 + second ) ;
	}

	private boolean isValid()
	{
		return ( 0 <= hour && hour <= 23 
		      && 0 <= minute && minute <= 59
		      && 0 <= second && second <= 59 );
	}

	private int hour;
	private int minute;
	private int second;
}
