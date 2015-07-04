/*
 * Good Day;
 */
package com.royalstone.pos.hardware;

import jpos.JposException;
import jpos.LineDisplay;

import com.royalstone.pos.common.Sale;
import com.royalstone.pos.util.Value;

/**
 * @author wubingyan
 * Created by King_net  on 2005-7-20  11:04:25
 */
public class PD3000CustDisplay implements ICustDisplay{

	private LineDisplay control;

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#init()
	 */
	public void init(LineDisplay c) {
		this.control = c;
		try {
			control.clearText();
		} catch (JposException ex) {
			ex.printStackTrace();
		}
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
		
		printText("ª∂”≠π‚¡Ÿ");
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printSubTotal(java.lang.String)
	 */
	public void printSubTotal(String value) {
		printText("SubTotal: " + value);
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printTotal(java.lang.String)
	 */
	public void printTotal(String value) {
		
		String text="◊‹∂Ó: " + value;
		printText(text);
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printReturn(java.lang.String)
	 */
	public void printReturn(String value) {
		clear();
		String text="’“¡„: " + value;
		printText(text);
		
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printGoods(java.lang.String)
	 */
	public void printGoods(Sale s) {
		clear();
		
		String text=s.getName().substring(0,5)+": "
				+ (new Value(s.getStdPrice())).toString()
				+ "x"
				+ s.getQtyStr();
		printText(text);
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printAmtPr(java.lang.String)
	 */
	public void printAmtPr(String value) {
		printText("Amount: " + value);
	}

	/**
	 * @see com.royalstone.pos.hardware.ICustDisplay#printPayment(java.lang.String)
	 */
	public void printPayment(String value) {
//		String text="÷ß∏∂: " + value;
//		printText(text);
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
