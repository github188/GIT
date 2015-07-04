package com.royalstone.pos.favor;


/**	DiscBulk 用于描述量贩价促销. 
 * 量贩价的特点是:只有整数倍于量贩数量的商品是可以打折的.
 * 例如, 量贩数量为12只,顾客购买15只,则其中12只可以享受量贩价,而其余3只不可以.
 * @version 1.0 2004.05.25
 * @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class DiscBulk extends Discount
{
	/**
	 * @param disc_type		折扣类型
	 * @param disc_price	折扣价格
	 * @param qty_bulk		折扣数量
	 */
	public DiscBulk( int disc_type, int disc_price, int qty_bulk )
	{
		super( disc_type );
		this.price = disc_price;
		this.qty_bulk = qty_bulk;
	}

	/**
	 * @return	量贩价
	 */
	public int getPrice()
	{
		return price;
	}
	
	/**
	 * @return	量贩价
	 */
	public int getBulkPrice()
	{
		return price;
	}
	
	/**
	 * @return	可以享受量贩价的商品数量
	 */
	public int getBulkVolume()
	{
		return qty_bulk;
	}
	
	/**
	 * @return	可以享受量贩价的商品数量
	 */
	public int getQtyBulk()
	{
		return qty_bulk;
	}
	
	/**
	 * for debug use.
	 */
	public String toString()
	{
		return "Bulk Price: " + price;
	}

	/**
	 * <code>price</code>	量贩价
	 */
	private int price;
	
	/**
	 * <code>qty_bulk</code>	可以享受量贩价的商品数量
	 */
	private int qty_bulk;
}
