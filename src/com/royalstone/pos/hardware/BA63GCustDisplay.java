package com.royalstone.pos.hardware;

import com.royalstone.pos.common.Sale;
import com.royalstone.pos.util.Value;

import jpos.JposException;
import jpos.LineDisplay;

/**
 * BA63G顾显属于硬件层（在JavaPos标准的Control上的层），可显示中文
 * @author liangxinbiao
 */
public class BA63GCustDisplay implements ICustDisplay {

	private LineDisplay control;

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#init()
	 */
	public void init(LineDisplay c) {
		this.control = c;
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printText(java.lang.String)
	 */
	public void printText(String value) {
		try {
			control.displayText(value, 0);
		} catch (JposException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#clear()
	 */
	public void clear() {
		try {
			control.clearText();
		} catch (JposException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#welcome()
	 */
	public void welcome() {
		printText("欢迎光临");
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printSubTotal(java.lang.String)
	 */
	public void printSubTotal(String value) {
		printText("小计：" + value);
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printTotal(java.lang.String)
	 */
	public void printTotal(String value) {
		printText("合计：" + value);
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printReturn(java.lang.String)
	 */
	public void printReturn(String value) {
		printText("找回：" + value);
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printGoods(java.lang.String)
	 */
	public void printGoods(Sale s) {
		if(s.getName()!=null && s.getName().length()>5){
			printText(
				s.getName().substring(0,5)
					+ " "
					+ (new Value(s.getStdPrice())).toString()
					+ "x"
					+ s.getQtyStr());
		}else{
			printText(
				(s.getName()==null?"":s.getName())
					+ " "
					+ (new Value(s.getStdPrice())).toString()
					+ "x"
					+ s.getQtyStr());
			
		}
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printAmtPr(java.lang.String)
	 */
	public void printAmtPr(String value) {
		printText("金额：" + value);
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printPayment(java.lang.String)
	 */
	public void printPayment(String value) {
		printText("收款：" + value);

	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printSubTotal(com.royalstone.pos.util.Value)
	 */
	public void printSubTotal(Value value) {
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printTotal(com.royalstone.pos.util.Value)
	 */
	public void printTotal(Value value) {
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printReturn(com.royalstone.pos.util.Value)
	 */
	public void printReturn(Value value) {
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printPayment(com.royalstone.pos.util.Value)
	 */
	public void printPayment(Value value) {
	}

}
