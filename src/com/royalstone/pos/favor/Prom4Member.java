/*
 * Created on 2004-6-3
 */
package com.royalstone.pos.favor;

import java.util.GregorianCalendar;

import com.royalstone.pos.common.Goods;

/**	Promotion 实现按价格定义的会员促销, 必须会员级别满足要求才能享受相应的优惠. 可以为不同级别的会员定义不同的优惠价格.
 * @author Mengluoyi
 */

public class Prom4Member extends DiscCriteria 
{
	/**
	 * @param vgno		商品编码
	 * @param price		商品促销售价
	 * @param cust_level	可以享受促销价的客户级别
	 */
	public Prom4Member(String vgno, int price, int cust_level) 
	{
		super();
		this.vgno = vgno;
		this.price = price;
		this.cust_level = cust_level;
	}

	/**
	 * @param vgno		商品编码
	 * @param price		商品促销售价
	 * @param cust_level	可以享受促销价的客户级别
	 * @param start		生效日期与时间
	 * @param end		截止日期与时间
	 */
	public Prom4Member(String vgno, int price, int cust_level, GregorianCalendar start, GregorianCalendar end) 
	{
		super(start, end);
		this.vgno = vgno;
		this.price = price;
		this.cust_level = cust_level;
	}

	/**	根据传入的参数决定是否可以享受促销折扣. 此函数实现父类中的抽象方法. 
	 * 是否可以享受促销取决于以下几点:
	 * 1) 时间和日期是否在有效期内
	 * 2) 商品的折扣类型是否匹配
	 * 3) 客户级别是否匹配
	 * @see com.royalstone.pos.favor.DiscCriteria#matches
	 */
	public boolean matches(Goods goods, int qty, int cust_level) {
//		return (
//		super.valid()
//			&& (goods.getPType() != null && goods.getPType().equals(DiscCriteria.PROM4MEMBER))
//			&& this.vgno.equals(goods.getVgno())
//			&& this.cust_level == cust_level);
    return (
                super.valid()
                    && (goods.getPType() != null)
                    && this.vgno.equals(goods.getVgno())
                    && this.cust_level == cust_level);
	}

	/**	根据传入的参数决定商品可以享受的折扣. 此函数实现父类中的抽象方法. 
	 * 是否可以享受促销取决于以下几点:
	 * 1) 时间和日期是否在有效期内
	 * 2) 商品的折扣类型是否匹配
	 * 3) 客户级别是否匹配
	 * @see com.royalstone.pos.favor.DiscCriteria#getDiscount
	 */
	public Discount getDiscount(Goods goods, int qty, int cust_level) {
		if (matches(goods, qty, cust_level))
			return new DiscPrice(Discount.MEMBERPRICE, price);
		return new Discount(Discount.NONE);
	}
	
	public boolean matches(String goodsNo) {
		return vgno.equals(goodsNo);
	}


	/**
	 * for debug use.
	 */
	public String toString() {
		return "{ " + vgno + ", " + price + ", " + cust_level + " }";
	}

	/**
	 * <code>vgno</code>		商品编码
	 */
	private String vgno;
	
	/**
	 * <code>cust_level</code>	可以享受促销价的客户级别
	 */
	private int cust_level;
	
	/**
	 * <code>price</code>		商品的促销价(以分为单位)
	 */
	private int price;
}