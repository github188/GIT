package com.royalstone.pos.data;

import java.io.Serializable;

/**
 * @author 
 */
public class PosPriceData implements Serializable
{
	public PosPriceData(){
		this.setSaleAmount(1);//初始化
		this.setFlag(0);
	}

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public void setShopid(String shopid) {
		this.shopid = shopid;
	}

	public String getShopid() {
		return shopid;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

	public void setSaleAmount(int saleAmount) {
		this.saleAmount = saleAmount;
	}

	public int getSaleAmount() {
		return saleAmount;
	}

	private String saleCode;/*商品代码，代替原来code*/
	private int saleAmount;/*商品总数量*/
	private String shopid;/*店号*/
	private int flag;/*标识*/


}
