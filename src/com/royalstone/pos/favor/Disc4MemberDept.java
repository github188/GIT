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
public class Disc4MemberDept extends DiscCriteria 
{
	public Disc4MemberDept(
		int deptid,
		int discLevel,
		int discRate,
		GregorianCalendar start,
		GregorianCalendar end) 
	{
		super(start, end);
		this.deptid = deptid;
		this.discLevel=discLevel;
		this.discRate=discRate;
	}

	public boolean matches(Goods goods, int qty, int cust_level) 
	{
//		return (
//		super.valid()
//			&& (goods.getPType() != null
//				&& goods.getPType().equals(DiscCriteria.DISC4MEMBERDEPT))
//			&& deptid==Integer.parseInt(goods.getDeptid())
//            && discLevel==cust_level);
    		return (
		super.valid()
			&& (goods.getPType() != null)
			&& deptid==Integer.parseInt(goods.getDeptid())
            && discLevel==cust_level);
	}
	

	public Discount getDiscount(Goods goods, int qty, int cust_level) {
		if (!matches(goods, qty, cust_level))
			return new Discount(Discount.NONE);

	    return new DiscRate(Discount.MEMBERDEPT, discRate);

	}

	private int deptid;
	private int discLevel;
	private int discRate;
	
	public boolean matches(String goodsNo) {
		return false;
	}
}
