/*
 * Created on 2004-6-3
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.favor;

import java.util.GregorianCalendar;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.util.Value;

/**
 * @author Mengluoyi
 * BulkPrice 表示量贩价,与数据库中的表discPrice_vgno中数据相对应.
 */
public class BulkPrice extends DiscCriteria 
{
	/**
	 * @param vgno			商品编码
	 * @param bulk_price1	量贩价格1
	 * @param qty1			量贩价格1的数据下限
	 * @param bulk_price2	量贩价格2
	 * @param qty2			量贩价格2的数据下限
	 * @param bulk_price3	量贩价格3
	 * @param qty3			量贩价格3的数据下限
	 * @param bulk_price4	量贩价格4
	 * @param qty4			量贩价格4的数据下限
	 * @param bulk_price5	量贩价格5
	 * @param qty5			量贩价格5的数据下限
	 * @param bulk_price6	量贩价格6
	 * @param qty6			量贩价格6的数据下限
	 */
	public BulkPrice( String vgno,
		int bulk_price1, int qty1,
		int bulk_price2, int qty2,
		int bulk_price3, int qty3,
		int bulk_price4, int qty4,
		int bulk_price5, int qty5,
		int bulk_price6, int qty6 ) 
	{
		super();
		this.vgno = vgno;
		this.bulk_price1 = bulk_price1;
		this.qty1 = qty1;
		this.bulk_price2 = bulk_price2;
		this.qty2 = qty2;
		this.bulk_price3 = bulk_price3;
		this.qty3 = qty3;
		this.bulk_price4 = bulk_price4;
		this.qty4 = qty4;
		this.bulk_price5 = bulk_price5;
		this.qty5 = qty5;
		this.bulk_price6 = bulk_price6;
		this.qty6 = qty6;
	}

	/**
	 * @param vgno			商品编码
	 * @param bulk_price1	量贩价格1
	 * @param qty1			量贩价格1的数据下限
	 * @param bulk_price2	量贩价格2
	 * @param qty2			量贩价格2的数据下限
	 * @param bulk_price3	量贩价格3
	 * @param qty3			量贩价格3的数据下限
	 * @param bulk_price4	量贩价格4
	 * @param qty4			量贩价格4的数据下限
	 * @param bulk_price5	量贩价格5
	 * @param qty5			量贩价格5的数据下限
	 * @param bulk_price6	量贩价格6
	 * @param qty6			量贩价格6的数据下限
	 * @param start			起始时间
	 * @param end			截止时间
	 */
	public BulkPrice(
		String vgno,
		int bulk_price1,
		int qty1,
		int bulk_price2,
		int qty2,
		int bulk_price3,
		int qty3,
		int bulk_price4,
		int qty4,
		int bulk_price5,
		int qty5,
		int bulk_price6,
		int qty6,
		GregorianCalendar start,
		GregorianCalendar end) 
	{
		super(start, end);
		this.vgno = vgno;
		this.bulk_price1 = bulk_price1;
		this.qty1 = qty1;
		this.bulk_price2 = bulk_price2;
		this.qty2 = qty2;
		this.bulk_price3 = bulk_price3;
		this.qty3 = qty3;
		this.bulk_price4 = bulk_price4;
		this.qty4 = qty4;
		this.bulk_price5 = bulk_price5;
		this.qty5 = qty5;
		this.bulk_price6 = bulk_price6;
		this.qty6 = qty6;
	}

	/**	此函数根据商品,商品数量和会员级别判断是否可以享受折扣.
	 * @param goods		商品
	 * @param qty		商品数量
	 * @param cust_level	客户级别
	 * @return	true	匹配;<br/>false	不匹配;
	 * @see com.royalstone.pos.favor.DiscCriteria#matches(com.royalstone.pos.common.Goods, int, int)
	 */
	public boolean matches(Goods goods, int qty, int cust_level) 
	{
		if (!super.valid() || !vgno.equals(goods.getVgno())) return false;
		if (qty1 > 0 && qty >= qty1) return true;
		if (qty2 > 0 && qty >= qty2) return true;
		if (qty3 > 0 && qty >= qty3) return true;
		if (qty4 > 0 && qty >= qty4) return true;
		if (qty5 > 0 && qty >= qty5) return true;
		if (qty6 > 0 && qty >= qty6) return true;

		return false;
	}
	
	/**	测试商品是否可以享受量贩折扣.
	 * @param goods	商品
	 * @param qty	商品数量
	 * @return		true	匹配;<br/>false	不匹配.
	 */
	public boolean matches(Goods goods, int qty) {
		if (!super.valid()
			|| !vgno.equals(goods.getVgno())
			|| (goods.getPType() == null
				|| !goods.getPType().equals(DiscCriteria.BULKPRICE)))
			return false;
		if (qty1 > 0 && qty >= qty1) return true;
		if (qty2 > 0 && qty >= qty2) return true;
		if (qty3 > 0 && qty >= qty3) return true;
		if (qty4 > 0 && qty >= qty4) return true;
		if (qty5 > 0 && qty >= qty5) return true;
		if (qty6 > 0 && qty >= qty6) return true;

		return false;
	}
	
	public boolean matches(String goodsNo) {
		return vgno.equals(goodsNo);
	}


	/**
	 * @param goods	商品.
	 * @param qty	商品的数量.
	 * @param cust_level
	 * @return	如果商品数量达到量贩标准,则返回为量贩价格,以DiscBulk 对象表示.<br/>
	 * 如果商品数量未达到量贩标准,返回值为null.
	 */
	public Discount getDiscount(Goods goods, int qty, int cust_level) 
	{
		if (!matches(goods, qty, cust_level))
			return new Discount(Discount.NONE);
		if (qty6 > 0 && qty >= qty6)
			return new DiscBulk(Discount.BULK, bulk_price6, qty6);
		if (qty5 > 0 && qty >= qty5)
			return new DiscBulk(Discount.BULK, bulk_price5, qty5);
		if (qty4 > 0 && qty >= qty4)
			return new DiscBulk(Discount.BULK, bulk_price4, qty4);
		if (qty3 > 0 && qty >= qty3)
			return new DiscBulk(Discount.BULK, bulk_price3, qty3);
		if (qty2 > 0 && qty >= qty2)
			return new DiscBulk(Discount.BULK, bulk_price2, qty2);
		if (qty1 > 0 && qty >= qty1)
			return new DiscBulk(Discount.BULK, bulk_price1, qty1);
		return new Discount(Discount.NONE);
	}

	/**	POS系统中可以为一个商品定义6个级别的量贩售价. 
	 * 本函数可以根据商品的数量,查询一个最高级别的量贩价格.
	 * @param goods	商品.
	 * @param qty	商品的数量.
	 * @return	如果商品数量达到量贩标准,则返回为量贩价格,以DiscBulk 对象表示.如果商品数量未达到量贩标准,返回值为null.
	 */
	public DiscBulk getDiscBulk(Goods goods, int qty) 
	{
		if (qty6 > 0 && qty >= qty6)
			return new DiscBulk(Discount.BULK, bulk_price6, qty6);
		if (qty5 > 0 && qty >= qty5)
			return new DiscBulk(Discount.BULK, bulk_price5, qty5);
		if (qty4 > 0 && qty >= qty4)
			return new DiscBulk(Discount.BULK, bulk_price4, qty4);
		if (qty3 > 0 && qty >= qty3)
			return new DiscBulk(Discount.BULK, bulk_price3, qty3);
		if (qty2 > 0 && qty >= qty2)
			return new DiscBulk(Discount.BULK, bulk_price2, qty2);
		if (qty1 > 0 && qty >= qty1)
			return new DiscBulk(Discount.BULK, bulk_price1, qty1);
		return null;
	}

	/**
	 * for debug use.
	 */
	public String toString() {
		return "{ " + vgno + " " + new Value(bulk_price1).toString() + "@"
				+ qty1 + "; " + new Value(bulk_price2).toString() + "@" + qty2
				+ "; " + new Value(bulk_price3).toString() + "@" + qty3 + " }";
	}

	private String vgno;
	private int bulk_price1, qty1;
	private int bulk_price2, qty2;
	private int bulk_price3, qty3;
	private int bulk_price4, qty4;
	private int bulk_price5, qty5;
	private int bulk_price6, qty6;
}
