package com.royalstone.pos.card;

/**
 * ��ֵ���ӿ�

 * @author liangxinbiao
 */
public interface IShoppingCard {
	
	public abstract SHCardQueryVO query(String cardNo, String secrety);
	public abstract String pay(SHCardPayVO cp);
    public abstract String autoRever(SHCardPayVO cp);
    public abstract String isNeedPass(String cardNo);

}