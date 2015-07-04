/*
 * Created on 2004-6-1
 */
package com.royalstone.pos.complex;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.core.SaleList;
import com.royalstone.pos.invoke.realtime.RealTime;
import com.royalstone.pos.invoke.realtime.RealTimeException;
import com.royalstone.pos.notify.UnitOfWork;
import com.royalstone.pos.util.Day;
import com.royalstone.pos.util.PosTime;
import com.royalstone.pos.util.PosConfig;

/**
 * @author Mengluoyi
 */
public class DiscComplexList implements Serializable {

	public static void main(String[] args) {
		DiscPool a, b, c, d, m;
		DiscComplexList list;
		DiscComplex disc;

		list = new DiscComplexList();

		if (args.length > 0) {
			list.load("favor.lst");
			list.print();
		}

		if (args.length == 0) {
			a = new DiscPool("666301", "666301", 3, 1000);
			b = new DiscPool("666301", "666302", 3, 1000);
			c = new DiscPool("666301", "666303", 3, 1000);

			m = DiscPool.merge(a, b);
			m = DiscPool.merge(m, c);

			disc =
				new DiscComplex(
					"003",
					"3件10元",
					1000,
					new Day(2000, 1, 1),
					new Day(9000, 12, 31),
					new PosTime(0, 0, 0),
					new PosTime(23, 59, 59),
					0);
			disc.addPool(m);
			list.add(disc);

			a = new DiscPool("666701", "666701", 7, 2000);
			b = new DiscPool("666701", "666702", 7, 2000);
			c = new DiscPool("666701", "666703", 7, 2000);
			d = new DiscPool("666701", "666704", 7, 2000);
			m = DiscPool.merge(a, b);
			m = DiscPool.merge(m, c);
			m = DiscPool.merge(m, d);

			disc =
				new DiscComplex(
					"007",
					"7件20元",
					2000,
					new Day(2000, 1, 1),
					new Day(9000, 12, 31),
					new PosTime(0, 0, 0),
					new PosTime(23, 59, 59),
					0);
			disc.addPool(m);
			list.add(disc);
			list.print();
			list.unload("favor.lst");
		}
	}

	/**
	 * 
	 */
	public DiscComplexList() {
		disc_lst = new Vector();
	}

	/**
	 * @return
	 */
	public int size() {
		return disc_lst.size();
	}

	/**
	 * @param i
	 * @return
	 */
	public DiscComplex get(int i) {
		return (DiscComplex) disc_lst.get(i);
	}

	/**
	 * @param disc
	 */
	public void add(DiscComplex disc) {
		disc_lst.add(disc);
	}

	public void update(String goodsNo, DiscComplexList discComplexList) {
		ArrayList complexList = getMatchDiscComplex(goodsNo);
		for (int i = 0; i < complexList.size(); i++) {
			disc_lst.remove((DiscComplex) complexList.get(i));
		}
		if (discComplexList != null) {
			for (int i = 0; i < discComplexList.size(); i++) {
				add(discComplexList.get(i));
			}
		}

		if (!((complexList.size() == 0)
			&& (discComplexList == null || discComplexList.size() == 0))) {
			UnitOfWork.getInstance().updateComplex(true);
		}
	}

	/**
	 * @param file
	 */
	public void load(String file) {
		try {
			System.out.println("DiscComplexList load start ... ");
			ObjectInputStream in =
				new ObjectInputStream(new FileInputStream(file));
			DiscComplex disc;
			while (true) {
				disc = (DiscComplex) in.readObject();
				add(disc);
			}
		} catch (java.io.EOFException e) {
			// End of File met, loading succeeded.
			System.out.println("DiscComplexList load over. ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param file
	 */
	public void unload(String file) {
		try {
			ObjectOutputStream out =
				new ObjectOutputStream(new FileOutputStream(file));

			for (int i = 0; i < size(); i++)
				out.writeObject(get(i));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param sale_lst
	 * @param g
	 * @return
	 */
	public DiscComplex getMatchDiscComplexAfter(SaleList sale_lst, Goods g) {

		ArrayList matchList = new ArrayList();
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).match(g)) {
				DiscComplexList lst = new DiscComplexList();
				lst.add(this.get(i));
				lst.computeFavorAfter(sale_lst);
				if (this.get(i).getFavorBaskets() > 0) {
					matchList.add(this.get(i));
				}
			}
		}
		DiscComplex discMatch = null;
		for (int i = 0; i < matchList.size(); i++) {
			if (discMatch == null
				|| ((DiscComplex) matchList.get(i)).getLevel()
					> discMatch.getLevel()) {
				discMatch = (DiscComplex) matchList.get(i);
			}

		}
		return discMatch;
	}

	/**	计算组合促销的优惠金额
	 * @param sale_lst
	 */
	public void computeFavor(SaleList sale_lst) {
		for (int i = 0; i < this.size(); i++)
			this.get(i).computeFavor(sale_lst);
	}

	/**
	 * @param sale_lst
	 */
	public void computeFavorAfter(SaleList sale_lst) {
		for (int i = 0; i < this.size(); i++)
			this.get(i).computeFavorAfter(sale_lst);
	}

	/**
	 * @param sale_lst
	 * @param g
	 * @return
	 * @throws RealTimeException
	 */
	public ArrayList getMatchDiscComplex(SaleList sale_lst, Goods g) throws RealTimeException{
	       //TODO  沧州富达 by fire  2005_5_11
		ArrayList matchList = new ArrayList();
         PosConfig config=PosConfig.getInstance();
        String isRealTime=config.getString("ISREALTIME");
		if (PosContext.getInstance().isOnLine()&&"ON".equals(isRealTime)) {
			DiscComplexList discList =
				RealTime.getInstance().getComplextList(g.getVgno());
			for (int i = 0; i < discList.size(); i++) {
				if (discList.get(i).match(g)) {
					discList.get(i).computeFavor(sale_lst);
					if(discList.get(i).getFavorBaskets()>0){
						matchList.add(discList.get(i));
					}
				}
			}
		} else {
			
			for (int i = 0; i < this.size(); i++) {
				if (this.get(i).match(g)) {
					this.get(i).computeFavor(sale_lst);
					if (this.get(i).getFavorBaskets() > 0) {
						matchList.add(this.get(i));
					}
				}
			}
		}
		return matchList;
	}
		



	public ArrayList getMatchDiscComplex(String goodsNo) {

		ArrayList matchList = new ArrayList();
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).match(goodsNo)) {
				matchList.add(this.get(i));
			}
		}
		return matchList;
	}

	/**
	 * for debug use.
	 */
	public void print() {
		for (int i = 0; i < size(); i++)
			System.out.println(get(i).toString());
	}

	/**
	 * <code>disc_lst</code>	组合促销方案的清单
	 */
	private Vector disc_lst;
}
