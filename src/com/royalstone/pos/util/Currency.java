package com.royalstone.pos.util;

/**
 * Currency 用于表示POS系统中的货币代码. 目前,POS只支持三种货币:人民币,港币,美元.
   @version 1.0 2004.05.14
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

final public class Currency
{
	Currency( String c )
	{
		code = c;
	}

	public String getCode()
	{
		return new String(code);
	}

	public String getName()
	{
		return new String( code );
	}

	public String getUnit()
	{
		return new String( "yuan" );
	}

	boolean	equals(Currency c )
	{
		return code.equals( c.getCode() );
	}

	boolean	equals(String currencode )
	{
		return code.equals( currencode );
	}

	/**
	 * <code>RMB</code>
	 * 人民币<br/>
	 * <code>HKD</code>
	 * 港币<br/>
	 * <code>USD</code>
	 * 美元<br/>
	 */
	final public static String	RMB = "RMB";
	final public static String	HKD = "HKD";
	final public static String	USD = "USD";
	private String	code;
}
