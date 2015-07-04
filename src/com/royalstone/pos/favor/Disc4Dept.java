/*
 * Created on 2004-6-3
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.favor;

import java.util.GregorianCalendar;

import com.royalstone.pos.common.Goods;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Disc4Dept extends DiscCriteria 
{
	public Disc4Dept(
		String deptid,
		int disc_point1,
		int qty1,
		int disc_point2,
		int qty2,
		int disc_point3,
		int qty3,
		GregorianCalendar start,
		GregorianCalendar end) 
	{
		super(start, end);
		this.deptid = deptid;
		this.disc_point1 = disc_point1;
		this.qty1 = qty1;
		this.disc_point2 = disc_point2;
		this.qty2 = qty2;
		this.disc_point3 = disc_point3;
		this.qty3 = qty3;
	}

	public boolean matches(Goods goods, int qty, int cust_level) 
	{
		return (
			super.valid()
				&& (goods.getPType() != null
					&& goods.getPType().equals(DiscCriteria.DISC4DEPT))
				&& deptid.equals(goods.getDeptid())
				&& (qty >= qty1 || qty >= qty2 || qty >= qty3));
	}
	

	public Discount getDiscount(Goods goods, int qty, int cust_level) {
		if (!matches(goods, qty, cust_level))
			return new Discount(Discount.NONE);
		if (qty >= qty3)
			return new DiscRate(Discount.DEPT, disc_point3);
		if (qty >= qty2)
			return new DiscRate(Discount.DEPT, disc_point2);
		if (qty >= qty1)
			return new DiscRate(Discount.DEPT, disc_point1);
		return new Discount(Discount.NONE);
	}

	private String deptid;
	private int disc_point1, qty1;
	private int disc_point2, qty2;
	private int disc_point3, qty3;
	
	public boolean matches(String goodsNo) {
		return false;
	}
}
