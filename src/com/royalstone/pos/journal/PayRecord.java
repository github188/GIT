/*
 * Created on 2004-6-4
 *
 */
package com.royalstone.pos.journal;

import java.io.Serializable;

import org.jdom.Element;
import com.royalstone.pos.util.*;

/**
 * PayRecord 代表支付记录,用于前台模块向后台传递流水.
 * @author Mengluoyi
 *
 */
public class PayRecord implements Serializable {

	public static void main(String[] args) {
	}

	/**
	 * @param elm			存放支付信息的XML节点.
	 * @param onlineflag	联机标志.
	 */
	public PayRecord(Element elm, String onlineflag) {
		trainflag = elm.getChild("trainflag").getTextTrim();
		reason = elm.getChild("reason").getTextTrim();
		type = elm.getChild("type").getTextTrim();
		currency = elm.getChild("currency").getTextTrim();
		value = atoi(elm.getChild("value").getTextTrim());
		value_equiv = atoi(elm.getChild("value_equiv").getTextTrim());
		cardno = elm.getChild("cardno").getTextTrim();
		sysdate = str2day(elm.getChild("sysdate").getTextTrim());
		systime = str2time(elm.getChild("systime").getTextTrim());
		this.onlineflag = onlineflag;

	}

	/**	该方法用于将XML中的日期字串(yyyy-mm-dd)转换成Day 对象.
	 * @param sysdate	代表日期的字串. 如: 2004-09-28.
	 * @return	日期.
	 */
	private static Day str2day(String sysdate) {
		return new Day(
			atoi(sysdate.substring(0, 4)),
			atoi(sysdate.substring(5, 7)),
			atoi(sysdate.substring(8, 10)));
	}

	/**	该方法用于将XML中的时间字串(hh:mm:ss)转换成PosTime 对象.
	 * @param systime	代表时间的字串. 如:12:00:00.
	 * @return	PosTime 时间.
	 */
	private static PosTime str2time(String systime) {
		return new PosTime(
			atoi(systime.substring(0, 2)),
			atoi(systime.substring(3, 5)),
			atoi(systime.substring(6, 8)));
	}

	/**
	 * @return	培训标志.
	 */
	public String trainflag() {
		return trainflag;
	}
	
	/**
	 * @return	支付原因(普通支付,找赎).
	 */
	public String reason() {
		return reason;
	}
	
	/**
	 * @return	支付方式(现金,提货卡,礼券).
	 */
	public String type() {
		return type;
	}
	/**
	 * @return	支付币种.
	 */
	public String currency() {
		return currency;
	}
	/**
	 * @return	卡号.
	 */
	public String cardno() {
		return cardno;
	}
	/**
	 * @return	支付金额(以支付货币表示).
	 */
	public int value() {
		return value;
	}
	/**
	 * @return	等效支付金额(单位为人民币分).
	 */
	public int value_equiv() {
		return value_equiv;
	}
	/**
	 * @return	支付发生时的系统日期.
	 */
	public Day sysdate() {
		return sysdate;
	}
	/**
	 * @return	支付发生时的系统时间.
	 */
	public PosTime systime() {
		return systime;
	}
	
	/**	
	 * @param s	字串转为整形数.
	 * @return	字串s 所表达的数值.
	 */
	private static int atoi(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw e;
		}
	}

	/**
	 * <code>trainflag</code>	"培训状态"标志
	 */
	private String trainflag = "0";
	/**
	 * <code>reason</code>	支付原因
	 */
	private String reason;
	/**
	 * <code>type</code>	支付方式
	 */
	private String type;
	/**
	 * <code>currency</code>	支付币种代码
	 */
	private String currency;
	/**
	 * <code>cardno</code>	卡号
	 */
	private String cardno;
	/**
	 * <code>value</code>	支付金额(以支付所用倾向表示,单位为分)
	 */
	private int value;
	/**
	 * <code>value_equiv</code>	等效支付金额(以人民币表示,单位为分)
	 */
	private int value_equiv;
	/**
	 * <code>sysdate</code>	系统日期
	 */
	private Day sysdate;
	/**
	 * <code>systime</code>	系统时间
	 */
	private PosTime systime;
	/**
	 * <code>onlineflag</code>	联机标志
	 */
	private String onlineflag;
}

