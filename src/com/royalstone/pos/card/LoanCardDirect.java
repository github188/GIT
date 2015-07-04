package com.royalstone.pos.card;

import com.royalstone.pos.web.command.LoanCardAutoReverCommand;
import com.royalstone.pos.web.command.LoanCardPayCommand;
import com.royalstone.pos.web.command.LoanCardQueryCommand;

/**
 * 挂账卡实现，它不透过服务器而直接调用查询、支付、自动冲正等实现类的方法，主要用来测试
 * @author liangxinbiao
 */
public class LoanCardDirect implements ILoanCard {

	/**
	 * @see com.royalstone.pos.card.ILoanCard#query(java.lang.String, java.lang.String)
	 */
	public LoanCardQueryVO query(String cardNo, String secrety) {

		LoanCardQueryVO result = null;

		LoanCardQueryCommand command = new LoanCardQueryCommand();
		Object[] params = new Object[3];
		params[0] = "";
		params[1] = cardNo;
		params[2] = secrety;
		Object[] results = command.excute(params);

		if (results != null && results.length > 0) {
			result = (LoanCardQueryVO) results[0];
		}

		return result;
	}

	/**
	 * @see com.royalstone.pos.card.ILoanCard#pay(com.royalstone.pos.card.LoanCardPayVO)
	 */
	public String pay(LoanCardPayVO cp) {

		String result = null;
		LoanCardPayCommand command = new LoanCardPayCommand();

		Object[] params = new Object[2];
		params[0] = "";
		params[1] = cp;
		Object[] results = command.excute(params);

		if (results != null && results.length > 0) {
			result = (String) results[0];
		}

		return result;

	}

	/**
	 * @see com.royalstone.pos.card.ILoanCard#autoRever(com.royalstone.pos.card.LoanCardPayVO)
	 */
	public String autoRever(LoanCardPayVO cp) {
		
		String result = null;
		LoanCardAutoReverCommand command = new LoanCardAutoReverCommand();

		Object[] params = new Object[2];
		params[0] = "";
		params[1] = cp;
		Object[] results = command.excute(params);

		if (results != null && results.length > 0) {
			result = (String) results[0];
		}

		return result;
	}

}
