package com.royalstone.pos.card;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author liangxinbiao
 */
public class LoanCardDisc implements Serializable{
	
	public final static int DISC_PRICE=0;
	public final static int DISC_RATE=1;
	
	public final static int BIGDEPT_ITEMTYPE=1;
	public final static int MIDDEPT_ITEMTYPE=2;
	public final static int SMALLDEPT_ITEMTYPE=3;
	public final static int SINGLE_ITEMTYPE=4;

	/**
	 * 编码类型 1=大类 2=中类 3=小类 4=商品
	 */
	private int itemType;

	/**
	 * 编号 类别编号或者商品编码
	 */
	private int itemID;

	/**
	 * 0=优惠额度 1=百分比
	 */
	private int discType;

	/**
	 * 挂帐客户油品优惠额度 百分比或者优惠金额
	 */
	private BigDecimal discCount;

	/**
	 * @return
	 */
	public BigDecimal getDiscCount() {
		return discCount;
	}

	/**
	 * @return
	 */
	public int getDiscType() {
		return discType;
	}

	/**
	 * @return
	 */
	public int getItemID() {
		return itemID;
	}

	/**
	 * @return
	 */
	public int getItemType() {
		return itemType;
	}

	/**
	 * @param decimal
	 */
	public void setDiscCount(BigDecimal decimal) {
		discCount = decimal;
	}

	/**
	 * @param i
	 */
	public void setDiscType(int i) {
		discType = i;
	}

	/**
	 * @param i
	 */
	public void setItemID(int i) {
		itemID = i;
	}

	/**
	 * @param i
	 */
	public void setItemType(int i) {
		itemType = i;
	}

}
