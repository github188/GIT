package com.royalstone.pos.data;

import java.io.Serializable;

/**
 * @author 
 */
public class PosPriceData implements Serializable
{
	public PosPriceData(){
		this.setSaleAmount(1);//��ʼ��
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

	private String saleCode;/*��Ʒ���룬����ԭ��code*/
	private int saleAmount;/*��Ʒ������*/
	private String shopid;/*���*/
	private int flag;/*��ʶ*/


}
