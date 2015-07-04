package com.royalstone.pos.io;
import com.royalstone.pos.card.ICCardProcess;
import com.royalstone.pos.card.LoanCardProcess;
import com.royalstone.pos.common.*;

public class PosInputPayment extends PosInput {

	public void setBankCardTransReturnValue(BankCardTransReturnValue value) {
		bankCardTransReturnValue = value;
	}

	public BankCardTransReturnValue getBankCardTransReturnValue() {
		return bankCardTransReturnValue;
	}

	private BankCardTransReturnValue bankCardTransReturnValue;

	public PosInputPayment(int v, int t) {
		super(PosFunction.PAYMENT);
		type = t;
		cents = v;
	}

	public PosInputPayment(int v, int t, String cardno) {
		super(PosFunction.PAYMENT);
		type = t;
		cents = v;
		media_no = cardno;
	}
	
	public PosInputPayment(int v, int t, String cardno,BankCardTransReturnValue bankCardTransReturnValue) {
		super(PosFunction.PAYMENT);
		type = t;
		cents = v;
		media_no = cardno;
		this.bankCardTransReturnValue = bankCardTransReturnValue;
	}


	/**
	   @return Payment type: CASH, CHEQUE, VOUCHER ...
	 */
	public int getType() {
		return type;
	}

	public String getMediaNumber() {
		return media_no;
	}

	/**
	   @return payment value expressed as cents. Currency paid is defined in context, not by operation.
	 */
	public int getCents() {
		return cents;
	}

	public String toString() {
		return "{ PAYMENT: "
			+ Payment.getTypeName(type)
			+ " : "
			+ cents
			+ " cents. "
			+ media_no
			+ " }";
	}

	public static void main(String[] args) {
		String cardno = "12345";
		PosInputPayment inp = new PosInputPayment(100, Payment.CASH, cardno);
		cardno = "ABCED";
		System.out.println(inp.toString());
	}

	private int type;
	private int cents;
	private String media_no = "";
	private String extraData = "";
	private LoanCardProcess loanCardProcess;
	private ICCardProcess icCardProcess;
	/**
	 * @return
	 */
	public String getExtraData() {
		return extraData;
	}

	/**
	 * @param string
	 */
	public void setExtraData(String string) {
		extraData = string;
	}

	/**
	 * @return
	 */
	public String getMediaNo() {
		return media_no;
	}

	/**
	 * @param string
	 */
	public void setMediaNo(String string) {
		media_no = string;
	}

	/**
	 * @return
	 */
	public LoanCardProcess getLoanCardProcess() {
		return loanCardProcess;
	}

	/**
	 * @param process
	 */
	public void setLoanCardProcess(LoanCardProcess process) {
		loanCardProcess = process;
	}

	/**
	 * @param icp
	 */
	public void setICCardProcess(ICCardProcess icp) {
		icCardProcess = icp;
	}

	public ICCardProcess getICCardProcess() {
		return icCardProcess;
	}

}
