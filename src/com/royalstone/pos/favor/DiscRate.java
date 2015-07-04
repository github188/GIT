package com.royalstone.pos.favor;


/**
   @version 1.0 2004.05.25
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

/**	在业务上,销售折扣按操作的方式可分两大类:按百分点定义的折扣,和按促销价格定义的折扣.
 * DiscRate 表示按百分比定义的折扣, DiscPrice 表示按促销价定义的折扣.
 * @author Mengluoyi
 */
public class DiscRate extends Discount
{
	/**
	 * @param disc_type		折扣类型
	 * @param disc_point	折扣点数
	 */
	public DiscRate( int disc_type, int disc_point )
	{
		super( disc_type );
		point = disc_point;
	}

	/**
	 * @return		折扣点数
	 */
	public int getPoint()
	{
		return point;
	}
	
	/**
	 * for debug use.
	 */
	public String toString()
	{
		return "Disc Point: " + point + "% ";
	}

	/**
	 * <code>point</code>	折扣点数. 缺省值为0.
	 */
	private int point = 0;
}
