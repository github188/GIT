package com.royalstone.pos.notify;

import com.royalstone.pos.common.GoodsExtList;
import com.royalstone.pos.common.GoodsList;
import com.royalstone.pos.complex.DiscComplexList;
import com.royalstone.pos.favor.BulkPriceList;
import com.royalstone.pos.favor.DiscountList;

/**
 * @author liangxinbiao
 */

public class UnitOfWork {

	private static UnitOfWork instance = new UnitOfWork();

	private boolean goods = false;
	private boolean goodsext = false;
	private boolean discount = false;
	private boolean complex = false;
	private boolean bulk = false;

	public static UnitOfWork getInstance() {
		return instance;
	}

	public void updateGoods(boolean value) {
		goods = value;
	}

	public void updateGoodsExt(boolean value) {
		goodsext = value;
	}

	public void updateDiscount(boolean value) {
		discount = value;
	}

	public void updateComplex(boolean value) {
		goods = value;
	}

	public void updateBulk(boolean value) {
		goods = value;
	}

	public void begin() {
		goods = false;
		goodsext = false;
		discount = false;
		complex = false;
		bulk = false;
	}

	public void commit(
		GoodsList goodsList,
		GoodsExtList goodsExtList,
		DiscountList discountList,
		DiscComplexList complexList,
		BulkPriceList bulkPriceList) {

		if (goods) {
			goodsList.toXMLFile("price.xml");
		}
		if (goodsext) {
			goodsExtList.toXMLFile("priceExt.xml");
		}
		if (discount) {
			discountList.dump("discount.lst");
		}
		if (complex) {
			complexList.unload("favor.lst");
		}
		if (bulk) {
			bulkPriceList.dump("bulkprice.lst");
		}
	}

}
