package com.royalstone.pos.card;

/**
 * ´¢Öµ¿¨½Ó¿Ú

 * @author liangxinbiao
 */
public interface IShoppingCard {
	
	public abstract SHCardQueryVO query(String cardNo, String secrety);
	public abstract String pay(SHCardPayVO cp);
    public abstract String autoRever(SHCardPayVO cp);
    public abstract String isNeedPass(String cardNo);

}