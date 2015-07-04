package com.royalstone.pos.util;
import java.text.DecimalFormat;

/**
 * Value 用于表示金额.为了避免出现"零误差",POS内部以long 型表示金额,单位为分.
 * 显示和打印时,需要转换为以逗号分隔的字串,在XML文档中需要转换成无逗号分隔的字串.
 * 使用该类可以方便地实现与金额相关的一系列的转换和计算.
 * 
 * Value 提供了与 "加/减/乘/除" 运算相对应的方法: add, minus, multiply, divide.
 * 其中, add/minus 只可以在 Value 对象之间进行. multiply/divide 的参数必须是整形数. 计算称重商品和油品的金额需要先做乘法再做除法.
   @version 1.0 2004.05.27
   @author  Mengluoyi, Royalstone Co., Ltd.
 */


public class Value
{

	/**
	 * @param cents	以分为单位表示的金额.
	 */
	public Value( long cents )
	{
		value = cents;
	}

	/**
	 * @param cents	以分为单位表示的金额.
	 */
	public Value( int cents )
	{
		value = cents;
	}

	/**
	 * @param v	A instance of Value.
	 */
	public Value( Value v )
	{
		value = v.value;
	}

	/**
	 * @return	以分为单位表示的金额.
	 */
	public long getCents()
	{
		return value;
	}

	/**
	 * @param b	A instance of Value.
	 * @return	A instance of Value which equals to this Value added to argument b.
	 */
	public Value add( Value b )
	{
		return new Value ( value + b.value );
	}

	/**
	 * @param b	A instance of Value.
	 * @return	A instance of Value which equals to this Value minus argument b.
	 */
	public Value minus( Value b )
	{
		return new Value ( value - b.value );
	}

	/**
	 * @param n	a integer.
	 * @return	A instance of Value which equals to this Value times n.
	 */
	public Value multiply( int n )
	{
		return new Value ( value * n );
	}

	/**
	 * @param n	a integer.
	 * @return	A instance of Value which equals to this Value divided by n.
	 * 由于Value 的精度为分,divide运算可能存在误差. 例如, 100 表示1.00元,除以3后结果为33(相当于0.33元)
	 */
	public Value divide( int n )
	{
		return new Value ( value / n );
	}
	
	/**
	 * @param b A instance of Value.
	 * @return	The value 0 if the argument b is a Value equal to this Value, 
	 * the value less than 0 if argument b is greater than this Value,
	 * the value greater than 0 if argument b is less than this Value.
	 */
	public long  compareTo( Value b )
	{
		return ( value - b.value);
	}

	/**
	 * @param v	A instance of Value.
	 * @return	true is argument v equals to this Value, false if argument v does not equals to this Value.
	 */
	public boolean equals( Value v )
	{
		return ( value == v.value );
	}

	/**
	 * 此方法主要用于生成显示时使用的字串.
	 * @return	表示金额的字串, 以元为单位, 小数点后保留两位, 有逗号分隔.
	 */
	public String toString()
	{
		DecimalFormat df = new DecimalFormat( ",##0.00" );
		return df.format( (double) value / 100.0 );
	}

	/**
	 * 此方法主要用于生成XML文档内的字串.
	 * @return	表示金额的字串, 以元为单位, 小数点后保留两位, 无逗号分隔.
	 */
	public String toValStr()
	{
		DecimalFormat df = new DecimalFormat( "0.00" );
		return df.format( (double) value / 100.0 );
	}
	
	
	public String toValSpecStr()
	{
		DecimalFormat df = new DecimalFormat( "0.00" );
		return df.format( (double) value / 100.0 );
	}


	private long value = 0;
}
