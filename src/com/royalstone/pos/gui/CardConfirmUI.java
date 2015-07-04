
package com.royalstone.pos.gui;

/**
 * 卡的确认接口
 * @author Liangxinbiao
 */
public interface CardConfirmUI {
	public abstract void setCardNo(String value);
	public abstract void setTenderAmount(String value);
	public abstract void setCardAmount(String value);
	public abstract void setBalance(String value);
	/**
	 * @return
	 */
	public abstract boolean confirm();
}