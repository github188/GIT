/*
 * Created on 2004-6-2
 */
package com.royalstone.pos.complex;

import java.io.Serializable;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.common.Sale;
import com.royalstone.pos.core.SaleList;
import com.royalstone.pos.favor.DiscCriteria;

/**
 * @author Mengluoyi
 */
public class DiscCell implements Serializable {

	/**
	 * @param discpool	组合促销方案中的可互换商品小组
	 */
	public DiscCell(DiscPool discpool) {
		pool = discpool;
	}

	/**	将传入的 DiscPool 对象合并到DiscCell 的pool 对象中.
	 * @param discpool	An instance of DiscPool
	 */
	public void mergePool(DiscPool discpool) {
		if (pool.isSamePool(discpool))
			pool = DiscPool.merge(pool, discpool);
	}

	/**	判断商品编码是否在等效商品小组内
	 * @param vgno	商品编码
	 * @return	true	传入参数在等效商品小组内;<br/>
	 * 			false	不在等效小组内.
	 */
	public boolean hasVgno(String vgno) {
		return pool.hasVgno(vgno);
	}

	/**	判断传入的等效商品组是否和DiscCell 的等效小组属于同一个等效组.
	 * @param discpool
	 * @return
	 */
	public boolean isSamePool(DiscPool discpool) {
		return this.pool.isSamePool(discpool);
	}

	/**
	 * @param slist
	 */
	public void clearinSaleList(SaleList slist) {
		for (int i = 0; i < slist.size(); i++) {
			Sale sale = slist.get(i);
			if (sale.getType() != Sale.WITHDRAW
				&& pool.hasVgno(sale.getVgno())
				&& (sale.getGoods().getPType() != null
					&& sale.getGoods().getPType().equals(
						DiscCriteria.DISCCOMPLEX))) {
				sale.clearFavor();
			}
		}
	}

	/**	对SaleList 对象进行扫描,如果等效商品组内的商品,则累计其售出数量.
	 * @param slist	商品销售记录
	 */
	public void checkinSaleList(SaleList slist) {
		for (int i = 0; i < slist.size(); i++)
			checkinSale(slist.get(i));
	}

	/**	该函数将对销售记录进行检查. 如果符合参与组合促销的条件,则累计其售出数量.
	 * 是否符合的判断条件:
	 * 1) 记录类型不得为退货;
	 * 2) 商品的折扣类型为组合促销;
	 * 3) 商品编码在促销小组清单内.
	 * @param sale	商品销售记录
	 */
	private void checkinSale(Sale sale) {
		if (sale.getType() != Sale.WITHDRAW
			&& pool.hasVgno(sale.getVgno())
			&& (sale.getGoods().getPType() != null
				&& sale.getGoods().getPType().equals(DiscCriteria.DISCCOMPLEX)))
			qty_sold += sale.getQty() / sale.getGoods().getX();
	}

	/**
	 * @param slist
	 */
	public void checkinSaleListAfter(SaleList slist) {
		for (int i = 0; i < slist.size(); i++)
			checkinSaleAfter(slist.get(i));
	}

	/**
	 * @param sale
	 */
	private void checkinSaleAfter(Sale sale) {
		if (sale.getType() != Sale.WITHDRAW
			&& pool.hasVgno(sale.getVgno())
			&& (sale.getGoods().getPType() != null
				&& sale.getGoods().getPType().equals(DiscCriteria.DISCCOMPLEX)))
			qty_sold += (sale.getQty() - sale.getQtyDisc())
				/ sale.getGoods().getX();
	}

	/**
	 * @param sale
	 * @return
	 */
	public boolean match(Sale sale) {
		if (sale.getType() != Sale.WITHDRAW
			&& pool.hasVgno(sale.getVgno())
			&& (sale.getGoods().getPType() != null
				&& sale.getGoods().getPType().equals(DiscCriteria.DISCCOMPLEX))) {
			return true;
		}
		return false;
	}

	/**
	 * @param g
	 * @return
	 */
	public boolean match(Goods g) {
		if (pool.hasVgno(g.getVgno())) {
			return true;
		}
		return false;
	}

	public boolean match(String goodsNo) {
		if (pool.hasVgno(goodsNo)) {
			return true;
		}
		return false;
	}

	/**	计算DiscCell中满足促销条件的商品的促销销售额.
	 * @return	在DiscCell 内,可以享受促销售价的商品按照促销价格计算的金额.
	 */
	public int getPromValue() {
		return pool.getPromPrice() * baskets_favored;
	}

	/**
	 * @return	售出商品所包含的等效商品组数
	 */
	public int getSoldBaskets() {
		return (pool.getMinQty() == 0) ? 0 : (qty_sold / pool.getMinQty());
	}

	/**
	 * @param baskets_favored	享受促销优惠价格的小组数
	 */
	public void setFavor(int baskets_favored) {
		this.baskets_favored = baskets_favored;
		qty_favored = baskets_favored * pool.getMinQty();
	}

	/**
	 * 
	 */
	public void clear() {
		qty_sold = 0;
		qty_favored = 0;
	}

	/**
	 * @return
	 */
	private DiscPool getPool() {
		return pool;
	}

	/**
	 * @return
	 */
	public int getFavorQty() {
		return qty_favored;
	}

	/**
	 * @return
	 */
	public int getFavorValue() {
		return pool.getPromPrice() * baskets_favored;
	}

	/**
	 * for debug use.
	 */
	public String toString() {
		return pool.toString();
	}

	public DiscCell[] split() {

		DiscCell[] result = null;

		if (pool != null) {
			DiscPool[] pools = pool.split();
			result = new DiscCell[pools.length];
			for (int i = 0; i < pools.length; i++) {
				result[i] = new DiscCell(pools[i]);
			}
		}

		return result;
	}

	/**
	 * <code>pool</code>		促销商品小组
	 */
	private DiscPool pool;

	/**
	 * <code>qty_sold</code>	促销小组的已售出数量和
	 */
	private int qty_sold = 0;

	/**
	 * <code>qty_favored</code>	可以享受促销特价的小组内商品的数量
	 */
	private int qty_favored = 0;

	/**
	 * <code>baskets_favored</code>	可以享受促销特价的商品小组的数量
	 */
	private int baskets_favored = 0;
}
