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
public class Disc4Member extends DiscCriteria {
	public Disc4Member(String vgno, int disc_point, int cust_level) {
		super();
		this.vgno = vgno;
		this.disc_point = disc_point;
		this.cust_level = cust_level;
	}

	public Disc4Member(
		String vgno,
		int disc_point,
		int cust_level,
		GregorianCalendar start,
		GregorianCalendar end) {
		super(start, end);
		this.vgno = vgno;
		this.disc_point = disc_point;
		this.cust_level = cust_level;
	}

	public boolean matches(Goods goods, int qty, int cust_level) {
//		return (
//		super.valid()
//			&& (goods.getPType() != null
//				&& goods.getPType().equals(DiscCriteria.DISC4MEMBER))
//			&& this.vgno.equals(goods.getVgno())
//			&& this.cust_level == cust_level);
    		return (
		super.valid()
			&& (goods.getPType() != null)
			&& this.vgno.equals(goods.getVgno())
			&& this.cust_level == cust_level);
	}
	
	public boolean matches(String goodsNo) {
		return vgno.equals(goodsNo);
	}


	public Discount getDiscount(Goods goods, int qty, int cust_level) {
		if (matches(goods, qty, cust_level))
			return new DiscRate(Discount.MEMBERDISC, disc_point);
		else
			return new Discount(Discount.NONE);
	}

	private String vgno;
	private int disc_point;
	private int cust_level;
}
