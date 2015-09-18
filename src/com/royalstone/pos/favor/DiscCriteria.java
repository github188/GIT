package com.royalstone.pos.favor;

import java.util.*;
import java.io.*;

import com.royalstone.pos.common.Goods;

public abstract class DiscCriteria implements Serializable {
	public DiscCriteria() {
		time_start = new GregorianCalendar(1900, 0, 1);
		time_end = new GregorianCalendar(9000, 11, 31);
	}

	public DiscCriteria(GregorianCalendar start, GregorianCalendar end) {
		time_start = start;
		time_end = end;
	}

	public boolean valid() {
		GregorianCalendar now = new GregorianCalendar();
		return true;
		//		return ( time_start.before( now ) && time_end.after( now ) );
	}

	public abstract boolean matches(Goods goods, int qty, int cust_level);
	public abstract Discount getDiscount(Goods goods, int qty, int cust_level);
	public abstract boolean matches(String goodsNo);

	private GregorianCalendar time_start;
	private GregorianCalendar time_end;

	public final static String NORMAL = "n"; //没促销
	public final static String PROMOTION = "p"; //单品促销
	public final static String DISC4GOODS = "v"; //单品折扣
	public final static String DISC4DEPT = "d"; //整类折扣
    public final static String DISC4MEMBERDEPT = "M"; //会员整类折扣
	public final static String BULKPRICE = "b"; //量贩折扣
	public final static String DISCCOMPLEX = "s"; //组合促销
	public final static String DISCTIME = "t"; //时点折扣
	public final static String DISC4MEMBER = "k"; //会员折扣
	public final static String PROM4MEMBER = "h"; //会员价

}
