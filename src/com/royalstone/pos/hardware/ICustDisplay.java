package com.royalstone.pos.hardware;

import com.royalstone.pos.common.Sale;
import com.royalstone.pos.util.Value;

import jpos.LineDisplay;

/**
 * 顾显硬件层的接口
 * @author liangxinbiao
 */
public interface ICustDisplay {
	
	/**
	 * 初始化
	 * @param c 
	 */
	public  void init(LineDisplay c);
	
	/**
	 * 显示字符串
	 * @param value
	 */
	public  void printText(String value);
	
	/**
	 * 清屏 
	 */
	public  void clear();
	
	/**
	 * 显示欢迎信息 
	 */
	public  void welcome();
	
	/**
	 * 显示小计 
	 * @param value
	 */
	public  void printSubTotal(String value);
	
	/**
	 * 显示合计
	 * @param value
	 */
	public  void printTotal(String value);
	
	/**
	 * 显示找回
	 * @param value
	 */
	public  void printReturn(String value);
	
	/**
	 * 显示小计
	 * @deprecated
	 * @param value
	 */
	public  void printSubTotal(Value value);
	
	/**
	 * 显示合计
	 * @deprecated
	 * @param value
	 */
	public  void printTotal(Value value);
	
	/**
	 * 显示找回
	 * @deprecated
	 * @param value
	 */
	public  void printReturn(Value value);
	
	/**
	 * 显示所销售商品
	 * @param s
	 */
	public  void printGoods(Sale s);
	
	/**
	 * 显示金额
	 * @param value
	 */
	public  void printAmtPr(String value);
	
	/**
	 * 显示支付
	 * @param value
	 */
	public  void printPayment(String value);
	
	/**
	 * 显示支付
	 * @deprecated 
	 * @param value
	 */
	public  void printPayment(Value value);

}
