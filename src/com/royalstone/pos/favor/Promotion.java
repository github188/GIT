/*
 * Created on 2004-6-3
 *
 */
package com.royalstone.pos.favor;

import java.util.GregorianCalendar;

import com.royalstone.pos.common.Goods;

/**	Promotion 实现按价格定义的促销, 不认是否会员都可以享受.
 * @author Mengluoyi
 */
public class Promotion extends DiscCriteria 
{
	/**
	 * @param vgno		商品编码
	 * @param price		商品促销售价
	 */
	public Promotion(String vgno, int price) 
	{
		super();
		this.vgno = vgno;
		this.price = price;
	}

	/**
	 * @param vgno		商品编码
	 * @param price		商品促销售价
	 * @param start		生效日期与时间
	 * @param end		截止日期与时间
	 */
	public Promotion(String vgno, int price, GregorianCalendar start, GregorianCalendar end) 
	{
		super(start, end);
		this.vgno = vgno;
		this.price = price;
	}

	/**	根据传入的参数决定是否可以享受促销折扣. 此函数实现父类中的抽象方法. 
	 * cust_level(客户级别)被忽略.
	 * @see com.royalstone.pos.favor.DiscCriteria#matches
	 */
	public boolean matches(Goods goods, int qty, int cust_level) 
	{
		// cust_level is ignored when searching for promotion price.
		return ( super.valid()
				&& (goods.getPType() != null && goods.getPType().equals(DiscCriteria.PROMOTION))
				&& vgno.equals(goods.getVgno()) );
	}

	/**	根据传入的参数决定商品可以享受的折扣. 此函数实现父类中的抽象方法. 
	 * cust_level(客户级别)被忽略.
	 * @see com.royalstone.pos.favor.DiscCriteria#getDiscount
	 * 
	 */
	public Discount getDiscount(Goods goods, int qty, int cust_level) 
	{
		if (matches(goods, qty, cust_level)) return new DiscPrice(Discount.PROMOTION, price);
		return new Discount(Discount.NONE);
	}

	/**
	 * for debug use.
	 */
	public String toString() 
	{
		return "{ " + vgno + ", " + price + " }";
	}

	/**
	 * <code>vgno</code>	商品编码
	 */
	private String vgno;
	
	/**
	 * <code>price</code>	商品促销售价
	 */
	private int price;

	public boolean matches(String goodsNo) {
		return vgno.equals(goodsNo);
	}
	
}
