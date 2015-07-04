package com.royalstone.pos.notify;

import java.io.Serializable;
import java.util.ArrayList;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.complex.DiscComplexList;
import com.royalstone.pos.favor.BulkPrice;
import com.royalstone.pos.favor.DiscCriteria;

/**
 * @author liangxinbiao
 */

public class GoodsUpdate implements Serializable{
	
	String goodsNo;
	Goods goods;
	ArrayList goodsExtList;

	BulkPrice bulkPrice;
	DiscComplexList complexList;
	DiscCriteria discCriteria;

	/**
	 * @return
	 */
	public BulkPrice getBulkPrice() {
		return bulkPrice;
	}

	/**
	 * @return
	 */
	public DiscComplexList getComplexList() {
		return complexList;
	}

	/**
	 * @return
	 */
	public DiscCriteria getDiscCriteria() {
		return discCriteria;
	}

	/**
	 * @return
	 */
	public Goods getGoods() {
		return goods;
	}

	/**
	 * @param price
	 */
	public void setBulkPrice(BulkPrice price) {
		bulkPrice = price;
	}

	/**
	 * @param list
	 */
	public void setComplexList(DiscComplexList list) {
		complexList = list;
	}

	/**
	 * @param criteria
	 */
	public void setDiscCriteria(DiscCriteria criteria) {
		discCriteria = criteria;
	}

	/**
	 * @param goods
	 */
	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	/**
	 * @return
	 */
	public ArrayList getGoodsExtList() {
		return goodsExtList;
	}

	/**
	 * @param list
	 */
	public void setGoodsExtList(ArrayList list) {
		goodsExtList = list;
	}

	/**
	 * @return
	 */
	public String getGoodsNo() {
		return goodsNo;
	}

	/**
	 * @param string
	 */
	public void setGoodsNo(String string) {
		goodsNo = string;
	}

}
