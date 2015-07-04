package com.royalstone.pos.favor;


/**
   @version 1.0 2004.05.25
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

/**	引入BulkFavor,是为了解决量贩优惠金额的分摊问题. BulkFavor有两个要素: 优惠金额,享受优惠的商品数量.
 * BulkFavor 最重要的成员函数是shareFavor. 调用该函数的结果,将把原始的BulkFavor 一分为二, 
 * 返回的BulkFavor 对象对应于数量为qty 的商品应分享的部分,剩下部分为待分摊的优惠金额.
 * @author Mengluoyi
 */
public class BulkFavor extends Discount
{
	/**
	 * @param qty_favored		享受优惠金额的商品数量
	 * @param value_favored		优惠金额
	 */
	public BulkFavor( int qty_favored, int value_favored )
	{
		super( Discount.BULK );
		this.favor_value 	= value_favored;
		this.favor_qty 		= qty_favored;
	}

	/**
	 * @return	优惠金额
	 */
	public int getValue()
	{
		return favor_value;
	}
	
	/**
	 * @return	享受优惠的商品数量
	 */
	public int getQty()
	{
		return favor_qty;
	}
	
	/**
	 * @return	享受优惠的商品数量
	 */
	public int getQtyFavored()
	{
		return favor_qty;
	}
	
	/**	该函数用于量贩折扣的分摊计算.
	 * 计算量贩折扣,首先要把分布在多条记录中的商品销售记录按商品作汇总,
	 * 然后在折扣表中求出可以享受的优惠金额,
	 * 再把优惠金额分摊到具体的商品销售记录中.
	 * 函数shareFavor 的用途就是把优惠金额分摊到数量为qty 的商品记录中.
	 * 业务上,要求分摊结果做到"一分不差". 
	 * 例如, 三条商品记录,数量均为1,总优惠金额为100,则分摊结果应为:33,33,34.
	 * 
	 * @param qty	分享优惠金额的商品数量
	 * @return		如果this 对象中优惠额为0, 返回一个优惠为0的 BulkFavor对象;<br/>
	 * 如果qty 大于this 对象中的优惠数量,将分得全部优惠金额, this 对象变为0;<br/>
	 * 如果qty 小于this 对象中的优惠数量,则返回按比例分配的优惠, 同时,this 对象中的优惠作相应的扣减.
	 */
	public BulkFavor shareFavor( int qty )
	{
		int q = 0;
		int v = 0;
		
		if( favor_qty <= 0 || qty <= 0 ) return new BulkFavor( 0, 0 );
		if( qty >= favor_qty ){
			q = favor_qty;
			v = favor_value;
			favor_qty = 0;
			favor_value = 0;
			return new BulkFavor( q, v );
		} else {
			q = qty;
			v = favor_value*qty / favor_qty;
			favor_qty -= qty;
			favor_value -= v;
			return new BulkFavor(q, v); 
		}
	}
	
	/**
	 * for debug use.
	 */
	public String toString()
	{
		return "" + "value_favored: " + favor_value + "; qty_favored: " + favor_qty;
	}

	/**
	 * <code>favor_value</code>	优惠金额
	 */
	private int favor_value;
	
	/**
	 * <code>favor_qty</code>	优惠数量
	 */
	private int favor_qty;
}
