package com.royalstone.pos.favor;


/**
   @version 1.0 2004.05.25
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

/**	在业务上,销售折扣按操作的方式可分两大类:按百分点定义的折扣,和按促销价格定义的折扣.
 * DiscRate 表示按百分比定义的折扣, DiscPrice 表示按促销价定义的折扣.
 * @author Mengluoyi
 */

public class DiscPrice extends Discount
{
	/**
	 * @param disc_type		折扣类型
	 * @param disc_price	促销售价
	 */
	public DiscPrice( int disc_type, long disc_price )
	{
		super( disc_type );
		price = disc_price;
	}

	/**
	 * @return	商品售价(促销价)
	 */
	public long getPrice()
	{
		return price;
	}
	
	/**
	 * for debug use.
	 */
	public String toString()
	{
		return "Disc Price: " + price;
	}

	/**
	 * <code>price</code>	商品促销价(以分为单位)
	 */
	private long price;
}
