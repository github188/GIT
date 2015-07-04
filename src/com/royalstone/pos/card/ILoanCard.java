package com.royalstone.pos.card;

/**
 * ¹ÒÕË¿¨½Ó¿Ú
 * @author liangxinbiao
 */
public interface ILoanCard {
	
	/**
	 * ¹ÒÕË¿¨²éÑ¯ 
	 * @param cardNo
	 * @param secrety
	 * @return
	 */
	public abstract LoanCardQueryVO query(String cardNo, String secrety);
	
	/**
	 * ¹ÒÕË¿¨Ö§¸¶
	 * @param cp
	 * @return
	 */
	public abstract String pay(LoanCardPayVO cp);
	
	/**
	 * ¹ÒÕË¿¨×Ô¶¯³åÕı
	 * @param cp
	 * @return
	 */
	public abstract String autoRever(LoanCardPayVO cp);
}