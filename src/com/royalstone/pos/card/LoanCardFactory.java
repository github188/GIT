package com.royalstone.pos.card;

/**
 * 工厂类负责生成实现了ILoanCard接口的类
 * @author liangxinbiao
 */
public class LoanCardFactory {

	private static ILoanCard instance = null;

	public static void setInstance(ILoanCard value) {
		instance = value;
	}

	public static ILoanCard createInstance() {
		if (instance == null) {
			instance = new LoanCard();
		}
		return instance;
	}

}
