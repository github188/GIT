package com.royalstone.pos.common;

import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.*;

import java.io.*;

/**	Payment 模拟顾客的支付行为,及找赎行为.
   @version 1.0 2004.05.12
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

final public class Payment implements Serializable {
	/**
	   @param v value paid.
	 */
	public Payment(int v) {
		value = v;
		type = CASH;
		reason = PAY;
		curren_code = Currency.RMB;
	}

	public Payment(int r, int v) {
		reason = r;
		value = v;
		value_equiv = v;
		type = CASH;
		curren_code = Currency.RMB;
		cardno = "";
	}

	public Payment(int reason, int type, int value) {
		this.reason = reason;
		this.type = type;
		this.value_equiv = this.value = value;
		this.curren_code = Currency.RMB;
		this.cardno = "";
	}

	public Payment(
		int reason,
		int type,
		String curren_code,
		int value,
		int value_equiv) {
		this.reason = reason;
		this.type = type;
		this.value = value;
		this.value_equiv = value_equiv;
		this.curren_code = curren_code;
	}

	/**
	   @param r payment reason.
	   @param t payment type.
	   @param curren_code currency code.
	   @param v value in the currency paid.
	   @param cardno card number, cheque number.
	 */
	public Payment(
		int reason,
		int type,
		String curren_code,
		int value,
		int value_equiv,
		String cardno) {
		this.reason = reason;
		this.type = type;
		this.value = value;
		this.value_equiv = value_equiv;
		this.curren_code = curren_code;
		this.cardno = cardno;
	}

	public Payment(
		int reason,
		int type,
		String curren_code,
		int value,
		int value_equiv,
		String cardno,
		Day sysdate,
		PosTime systime) {
		this.reason = reason;
		this.type = type;
		this.value = value;
		this.value_equiv = value_equiv;
		this.curren_code = new String(curren_code);
		this.cardno = new String(cardno);
		this.sysdate = sysdate;
		this.systime = systime;
	}

	public Payment(
		int reason,
		int type,
		String curren_code,
		int value,
		int value_equiv,
		String cardno,
		int trainflag,
		Day sysdate,
		PosTime systime) {
		this.reason = reason;
		this.type = type;
		this.value = value;
		this.value_equiv = value_equiv;
		this.curren_code = new String(curren_code);
		this.cardno = new String(cardno);
		this.trainflag = trainflag;
		this.sysdate = sysdate;
		this.systime = systime;
	}

	public Day getSysDate() {
		return sysdate;
	}
	public PosTime getSysTime() {
		return systime;
	}
	public int getReason() {
		return reason;
	}
	public int getType() {
		return type;
	}
	public String getCurrenCode() {
		return curren_code;
	}
	public int getValue() {
		return value;
	}
	public int getValueEquiv() {
		return value_equiv;
	}
	public String getCardno() {
		return cardno;
	}
	public String getReasonCode() {
		return "" + (char) reason;
	}

	public String getTypeCode() {
		int t = (type == CARDLOAN) ? CARDSHOP : type;
		return "" + (char) t;
	}

	public static String getTypeName(int t) {
		if (t == CASH)
			return "现金";
		if (t == CHEQUE)
			return "支票";
		if (t == VOUCHER)
			return "代币券";
		//if( t == CARDSHOP   ) return "提货卡";如果增加提货卡功能，请另行命名。
		if (t == CARDSHOP)
			return "储值卡";
		if (t == CARDMEMBER)
			return "会员卡";
		if (t == CARDBANK)
			return "银行卡";
		if (t == CARDLOAN)
			return "挂帐卡";
		if (t == FLEE)
			return "逃车";
		if (t == SAMPLE)
			return "抽样检测";
		if (t == OILTEST)
			return "油机测试";
		if (t == ICCARD)
			return "IC卡";
		if (t == PayStock)
			return "抵用卷";
		if (t == Cure)
			return "银行卡";
		//return "UNDEFINED";
		return pos.core.payModeList.getPayName(String.valueOf((char)t));
	}

	public static String getReasonName(int r) {
		if (r == PAY)
			return "支付";
		if (r == CHANGE)
			return "找赎";
		if (r == PSEUDO)
			return "虚拟";
		if (r == FLEE)
			return "逃车";
		if (r == SAMPLE)
			return "抽样检测";
		if (r == OILTEST)
			return "油机测试";
		return "UNDEFINED";
	}

	public void setAsTraining() {
		trainflag = 1;
	}

	public void setAsDeleted() {
		trainflag = 2;
	}

	public void setTrainFlag(int flag) {
		trainflag = flag;
	}

	public int getTrainFlag() {
		return trainflag;
	}

	public String toString() {
		return "{ "
			+ getReasonName(reason)
			+ ", "
			+ getTypeName(type)
			+ ", "
			+ curren_code
			+ " $"
			+ value
			+ "; "
			+ value_equiv
			+ ", "
			+ cardno
			+ ", "
			+ sysdate
			+ ", "
			+ systime
			+ " }";
	}

	public void setBankCardTransReturnValue(BankCardTransReturnValue value) {
		bankCardTransReturnValue = value;
	}

	public BankCardTransReturnValue getBankCardTransReturnValue() {
		return bankCardTransReturnValue;
	}

	private BankCardTransReturnValue bankCardTransReturnValue;

	/**
	 * <code>CASH</code>	现金
	 */
	/*
	paytype        	C	现金           
	paytype        	D	代币券         
	paytype        	K	支票           
	paytype        	L	挂帐卡         
	paytype        	R	银行卡         
	paytype        	V	提货卡         
	pay_reason     	R	找零           
	*/
	final public static int CASH = 'C';
	/**
	 * <code>CHEQUE</code>	支票
	 */
	final public static int CHEQUE = 'K';
	/**
	 * <code>VOUCHER</code>	礼券
	 */
	final public static int VOUCHER = 'D';
	/**
	 * <code>CARDSHOP</code>储值卡
	 */
	final public static int CARDSHOP = 'V';
	/**
	 * <code>CARDMEMBER</code>会员卡
	 */
	final public static int CARDMEMBER = 'M';
	/**
	 * <code>CARDBANK</code>银行卡
	 */
	final public static int CARDBANK = 'R';
	/**
	 * <code>CARDLOAN</code>挂帐卡
	 */
	final public static int CARDLOAN = 'L';

	/**
	 * <code>FLEE</code>	逃车
	 */
	final public static int FLEE = 'F';
	/**
	 * <code>SAMPLE</code>	抽样
	 */
	final public static int SAMPLE = 'S';
	/**
	 * <code>OILTEST</code>	油机测试
	 */
	final public static int OILTEST = 'T';

	/**
	 * <code>PAY</code>		顾客支付
	 */
	final public static int PAY = 'P';
	/**
	 * <code>CHANGE</code>	找赎
	 */
	final public static int CHANGE = 'R';
	/**
	 * <code>PSEUDO</code>	
	 */
	final public static int PSEUDO = 'U';
	/**
	 * <code>CASHIN</code>	入款
	 */
	final public static int CASHIN = 'I';
	/**
	 * <code>CASHOUT</code>	出款
	 */
	final public static int CASHOUT = 'D';

	/**
	 * <code>ICCARD</code>	IC卡支付
	 */
	final public static int ICCARD = 'A';
	// zhouzhou add 20061206 抵用卷
	final public static int PayStock = 'Z';
	// zhouzhou add 20061206 医保卡
	final public static int Cure = 'O';

	
	private int trainflag = 0;
	private int reason;
	private int type;
	private String curren_code = Currency.RMB;
	private int value, value_equiv;
	private String cardno = "";

	private PosTime systime = new PosTime();
	private Day sysdate = new Day();
}
