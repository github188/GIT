package com.royalstone.pos.favor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.invoke.realtime.RealTime;
import com.royalstone.pos.invoke.realtime.RealTimeException;
import com.royalstone.pos.notify.UnitOfWork;
import com.royalstone.pos.util.PosConfig;

/**
   @version 1.0 2004.05.25
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class DiscountList implements Serializable {

	// for test only
	public static void main(String[] args) {
		if (args.length == 0) {
			DiscountList lst = new DiscountList();

			lst.add(new Disc4Goods("000123", 5, 10, 10, 24, 20, 240));
			lst.add(new Disc4Goods("000121", 10, 1, 10, 1, 10, 1));

			lst.dump("discount.lst");
		} else {
			DiscountList lst = new DiscountList();
			lst.load("discount.lst");
			Prom4Member p = (Prom4Member) lst.list.get(0);
			System.out.println(p.toString());
		}
	}

	public DiscountList() {
		list = new Vector();
	}

	public void init() {
		add(new Disc4Goods("000123", 5, 10, 10, 24, 20, 240));
		add(new Disc4Goods("000121", 10, 1, 10, 1, 10, 1));
	}

	public boolean matches(Goods goods, int qty, int cust_level)
		throws RealTimeException {
//
//		for (int i = 0; i < list.size(); i++) {
//			DiscCriteria c = (DiscCriteria) list.get(i);
//			if (c.matches(goods, qty, cust_level))
//				return true;
//		}
//		return false;
	    PosConfig config=PosConfig.getInstance();
        String isRealTime=config.getString("ISREALTIME");
        String ifSupportOffLine=PosConfig.getInstance().getString("IFSUPPORTOFFLINE");

        if (PosContext.getInstance().isOnLine()&&"ON".equals(isRealTime)||"OFF".equals(ifSupportOffLine)) {
			DiscCriteria c =
				RealTime.getInstance().getDiscCriteria(
					goods.getVgno(),
					goods.getPType(),
					String.valueOf(cust_level));
			if (c != null && c.matches(goods, qty, cust_level)) {
				return true;
			}
		} else {
			for (int i = 0; i < list.size(); i++) {
				DiscCriteria c = (DiscCriteria) list.get(i);
				if (c.matches(goods, qty, cust_level))
					return true;
			}
		}
		return false;

	}

	public Discount getDiscount(Goods goods, int qty, int cust_level)
		throws RealTimeException {
	       //TODO ²×ÖÝ¸»´ï by fire  2005_5_11
         PosConfig config=PosConfig.getInstance();
        String isRealTime=config.getString("ISREALTIME");
		 String ifSupportOffLine=PosConfig.getInstance().getString("IFSUPPORTOFFLINE");

        if (PosContext.getInstance().isOnLine()&&"ON".equals(isRealTime)||"OFF".equals(ifSupportOffLine)) {
			DiscCriteria c =
				RealTime.getInstance().getDiscCriteria(
					goods.getVgno(),
					goods.getPType(),
					String.valueOf(cust_level));
			if (c != null && c.matches(goods, qty, cust_level)) {
				return c.getDiscount(goods, qty, cust_level);
			}
		} else {
			for (int i = 0; i < list.size(); i++) {
				DiscCriteria c = (DiscCriteria) list.get(i);
				if (c.matches(goods, qty, cust_level))
					return c.getDiscount(goods, qty, cust_level);
			}
		}

		return new Discount(Discount.NONE);
	}

	public void add(DiscCriteria criteria) {
		list.add(criteria);
	}

	public int size() {
		return list.size();
	}

	public void dump(String file) {
		try {
			ObjectOutputStream out =
				new ObjectOutputStream(new FileOutputStream(file));
			for (int i = 0; i < list.size(); i++) {
				DiscCriteria c = (DiscCriteria) list.get(i);
				out.writeObject(c);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load(String file) {
		try {
			System.out.println("Load Discount_list ... ");
			ObjectInputStream in =
				new ObjectInputStream(new FileInputStream(file));
			DiscCriteria c;
			while (true) {
				c = (DiscCriteria) in.readObject();
				add(c);
			}
		} catch (java.io.EOFException e) {
			System.out.println("Discount_list Loaded.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(String goodsNo, DiscCriteria criteria) {
		ArrayList matchList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			DiscCriteria d = (DiscCriteria) list.get(i);
			if (d.matches(goodsNo)) {
				matchList.add(d);
			}
		}

		for (int i = 0; i < matchList.size(); i++) {
			remove((DiscCriteria) matchList.get(i));
		}

		if (criteria != null) {
			add(criteria);
		}

		if (!(matchList.size() == 0 && criteria == null)) {
			UnitOfWork.getInstance().updateDiscount(true);
		}
		
	}

	public void remove(DiscCriteria criteria) {
		list.remove(criteria);
	}

	private Vector list;

}
