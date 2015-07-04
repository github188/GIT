/*
 * Created on 2004-6-1
 *
 */
package com.royalstone.pos.complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.core.SaleList;
import com.royalstone.pos.util.Day;
import com.royalstone.pos.util.PosTime;

/**
 * @author Mengluoyi
 */
public class DiscComplex implements Serializable {
	/**
	 * @param groupid		组合方案编号
	 * @param name			促销组合方案名称
	 * @param price			促销售价
	 * @param start_date	生效日期
	 * @param end_date		截止日期
	 * @param start_time	生效时间
	 * @param end_time		截止时间
	 * @param level			优先级
	 */
	public DiscComplex(
		String groupid,
		String name,
		int price,
		Day start_date,
		Day end_date,
		PosTime start_time,
		PosTime end_time,
		int level) {
		this.groupid = groupid;
		this.name = name;
		this.favored_price = price;
		this.start_date = start_date;
		this.end_date = end_date;
		this.start_time = start_time;
		this.end_time = end_time;
		this.level = level;
		cells = new Vector();
	}

	public String getGroupID() {
		return groupid;
	}

	/**
	 * @return		优先级
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return		商品促销单元清单的长度
	 */
	public int size() {
		return cells.size();
	}

	/**	取指定下标的商品促销单元
	 * @param i		下标
	 * @return		商品促销单元
	 */
	public DiscCell get(int i) {
		return (DiscCell) cells.get(i);
	}

	public Vector getCells() {
		return cells;
	}

	/**
	 * @return	商品组合促销的方案名称
	 */
	public String name() {
		return name;
	}

	/**	此函数在促销方案中添加等效商品小组.
	 * 如果在促销方案的等效商品组中找到了组长相同的等效组,则作合并操作;否则,把等效组添加到等效组列表中.
	 * @param pool	等效商品小组
	 */
	public void addPool(DiscPool pool) {
		for (int i = 0; i < cells.size(); i++) {
			DiscCell cell = (DiscCell) cells.get(i);
			if (cell.isSamePool(pool)) {
				cell.mergePool(pool);
				return;
			}
		}

		DiscCell cell = new DiscCell(pool);
		cells.add(cell);
	}

	/**	根据销售记录清单计算可以享受的组合促销优惠金额. 计算结果保存在各个促销单元DiscCell内.
	 * 调用 SaleList.consumeFavor 可以把这些组合促销优惠金额分摊到商品销售记录.
	 * 算法要点:
	 * 1) 清除现有的优惠.
	 * 2) 统计可以享受组合优惠的商品的数量.
	 * 3) 计算商品销售清单中所包含的优惠商品组合数.
	 * 4) 计算商品组合所可以享受的优惠金额.
	 * @see SaleList.consumeFavor
	 * @param sale_lst	商品销售记录清单
	 */
	public void computeFavor(SaleList sale_lst) {
		clear();
		checkinSaleList(sale_lst);
		setFavorBaskets();
		setFavor();
	}

	/**
	 * @param sale_lst
	 */
	public void computeFavorAfter(SaleList sale_lst) {
		clear();
		checkinSaleListAfter(sale_lst);
		setFavorBaskets();
		setFavor();
	}

	/**
	 * @return		可以享受促销方案的组合套数
	 */
	public int getFavorBaskets() {
		return favored_baskets;
	}

	/**
	 * @param sale_lst
	 */
	public void clearFavor(SaleList sale_lst) {
		for (int i = 0; i < this.size(); i++)
			this.get(i).clearinSaleList(sale_lst);
	}

	/**
	 * 清除优惠
	 */
	private void clear() {
		for (int i = 0; i < this.size(); i++)
			this.get(i).clear();
	}

	/**
	 * @param sale_lst
	 */
	private void checkinSaleList(SaleList sale_lst) {
		for (int i = 0; i < this.size(); i++)
			this.get(i).checkinSaleList(sale_lst);
	}

	/**
	 * @param g
	 * @return
	 */
	public boolean match(Goods g) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).match(g))
				return true;
		}
		return false;
	}

	public boolean match(String goodsNo) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).match(goodsNo))
				return true;
		}
		return false;
	}

	/**
	 * @param sale_lst
	 */
	private void checkinSaleListAfter(SaleList sale_lst) {
		for (int i = 0; i < this.size(); i++)
			this.get(i).checkinSaleListAfter(sale_lst);
	}

	/**
	 * 此函数遍历所有的商品组合单元, 求出各单元内所包含的小组数的最小值. 该最小值作为可享受优惠的商品组合数.
	 */
	private void setFavorBaskets() {
		int min = 0;

		if (cells.size() > 0)
			min = this.get(0).getSoldBaskets();

		for (int i = 1; i < this.size(); i++) {
			int n = this.get(i).getSoldBaskets();
			if (n < min)
				min = n;
		}
		favored_baskets = min;
	}

	/**
	 * 设置所有组合单元DiscCell 的可优惠小组数.
	 */
	private void setFavor() {
		for (int i = 0; i < this.size(); i++)
			this.get(i).setFavor(favored_baskets);
	}

	/**
	 * for debug use.
	 */
	public String toString() {
		String s = "### DiscComplex ###\n";
		s += "Groupid	" + groupid + "\n";
		s += "Name		" + name + "\n";
		for (int i = 0; i < size(); i++)
			s += "# " + i + " " + get(i).toString() + "\n";
		s += "######\n";
		return s;
	}

	public DiscComplex[] split() {

		if (cells != null) {

			ArrayList cellsList = new ArrayList();

			if (cells.size() > 1) {

				DiscCell[] cells1 = ((DiscCell) cells.get(0)).split();
				DiscCell[] cells2 = ((DiscCell) cells.get(1)).split();

				for (int i = 0; i < cells1.length; i++) {
					for (int j = 0; j < cells2.length; j++) {
						ArrayList horizonCellsList = new ArrayList();
						horizonCellsList.add(cells1[i]);
						horizonCellsList.add(cells2[j]);
						cellsList.add(horizonCellsList);
					}
				}

				for (int k = 2; k < cells.size(); k++) {

					ArrayList newcellsList = new ArrayList();
					DiscCell[] cells3 = ((DiscCell) cells.get(k)).split();

					for (int i = 0; i < cellsList.size(); i++) {
						for (int j = 0; j < cells3.length; j++) {
							ArrayList horizonCellsList = new ArrayList();
							horizonCellsList.addAll(
								(ArrayList) cellsList.get(i));
							horizonCellsList.add(cells3[j]);
							newcellsList.add(horizonCellsList);
						}
					}

					cellsList = newcellsList;
				}

			} else {

				if(cells.size()>0){

					DiscCell[] cells1 = ((DiscCell) cells.get(0)).split();
					for (int i = 0; i < cells1.length; i++) {
						ArrayList horizonCellsList = new ArrayList();
						horizonCellsList.add(cells1[i]);
						cellsList.add(horizonCellsList);
					}
					
				}
				
			}

			DiscComplex[] result = new DiscComplex[cellsList.size()];

			for (int i = 0; i < cellsList.size(); i++) {

				DiscComplex complex =
					new DiscComplex(
						groupid + "_" + i,
						name,
						favored_price,
						start_date,
						end_date,
						start_time,
						end_time,
						level);

				complex.getCells().addAll((ArrayList) cellsList.get(i));

				result[i] = complex;

			}

			return result;

		}

		return null;

	}

	/**
	 * <code>cells</code>				促销方案中 DiscCell (促销单元) 的清单
	 */
	private Vector cells = null;

	/**
	 * <code>groupid</code>				促销方案的编号
	 */
	private String groupid;

	/**
	 * <code>name</code>				促销方案名称
	 */
	private String name;

	/**
	 * <code>favored_price</code>		促销方案的总价(促销售价)
	 */
	private int favored_price;

	/**
	 * <code>favored_baskets</code>		可以享受组合促销方案的商品的套数
	 */
	private int favored_baskets = 0;

	/**
	 * <code>end_date</code>			截止日期
	 */
	/**
	 * <code>start_date</code>			生效日期
	 */
	private Day start_date, end_date;

	/**
	 * <code>end_time</code>			截止时间
	 */
	/**
	 * <code>start_time</code>			生效时间
	 */
	private PosTime start_time, end_time;

	/**
	 * <code>level</code>				促销方案的优先级
	 */
	private int level;
}
