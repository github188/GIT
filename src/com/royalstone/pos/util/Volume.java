package com.royalstone.pos.util;
import java.text.DecimalFormat;

/**
 * Volume 主要是为处理油品和称重商品的销售数量而设计. 
 * 油品的销售数量处理工作有以下特殊要求: 
 * 输入数量时以升为单位; 系统中存储的数量以毫升为单位; 
 * 显示的数量以升为单位,并以逗号分隔; 生成的XML节点中以升为单位并且没有逗号分隔.
 * Volume 根据以上特殊要求而提供一系列的方法,使用程序可以进行方便的转换. 
   @version 1.0 2004.05.27
   @author  Mengluoyi, Royalstone Co., Ltd.
 */


public class Volume
{
	public Volume()
	{
		millivolume = 0;
	}
	
	/**
	 * @param milli		以毫升为单位的容量.
	 */
	public Volume( int milli )
	{
		millivolume = milli;
	}

	public Volume( Volume v )
	{
		millivolume = v.millivolume;
	}
	
	/**
	 * @return 以毫升为单位的容量.
	 */
	public int getMilliVolume()
	{
		return millivolume;
	}
	
	/**
	 * @return 以升为单位的容量(float 型).
	 */
	public double getVolume()
	{
		return (double)millivolume / 1000.0;
	}
	
	public void setVolume( double volume )
	{
		millivolume = (int) ( volume * 1000 );
	}

	/**
	 * Volume 对象的加法函数.
	 * @param b A instance of Volume.
	 * @return  A Volume obj which equals this minus b.
	 */
	public Volume add( Volume b )
	{
		return new Volume ( millivolume + b.millivolume );
	}

	/**
	 * Volume 对象的减法函数.
	 */
	public Volume minus( Volume b )
	{
		return new Volume ( millivolume - b.millivolume );
	}

	/**
	 * Volume 对象的乘法.
	 * @param n 
	 * @return ( this * n ).
	 */
	public Volume multiply( int n )
	{
		return new Volume ( millivolume * n );
	}

	/**
	 * Volume 对象的除法.
	 * @param n 被除数.
	 * @return
	 */
	public Volume divide( int n )
	{
		return new Volume ( millivolume / n );
	}

	public int  compareTo( Volume b )
	{
		return ( millivolume - b.millivolume);
	}

	public boolean equals( Volume v )
	{
		return ( millivolume == v.millivolume );
	}
	
	/**
	 * 转换成显示格式
	 * @return XML节点中使用的字串(以升为单位,有逗号分隔).
	 */
	public String toString()
	{
		DecimalFormat df = new DecimalFormat( ",##0.000" );
		return df.format( (double) millivolume / 1000.0 );
	}

	/**
	 * 转换成以升为单位的字串.
	 * @return XML节点中使用的字串(以升为单位,无逗号分隔).
	 */
	public String toVolumeStr()
	{
		DecimalFormat df = new DecimalFormat( "0.000" );
		return df.format( (double) millivolume / 1000.0 );
	}
	
	/**
	 * 取油品数量中的零头(毫升部分).
	 * @return 油品数量中不足一升的部分(以毫升为单位)
	 */
	public int oddment()
	{
		return ( millivolume % 1000 );
	}

	private int millivolume = 0;
}
