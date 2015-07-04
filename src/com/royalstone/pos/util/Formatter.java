package com.royalstone.pos.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 封装了有关日期、数字的格式化的方法的实用类
 * @author liangxinbiao
 */
public class Formatter {
	
	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm"); 
	private static SimpleDateFormat sdfTime=new SimpleDateFormat("HH:mm:ss");
	private static SimpleDateFormat sdfDate=new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdfDateFile=new SimpleDateFormat("yyyyMMddHHmmss");
	
	/**
	 * 将代表数字的字符串格式化为带两位小数的字符串
	 * @param s 要格式化的字符串
	 * @return 格式化后的字符串
	 */
	public static String toMoney(String s) {

		Double val = new Double(s);

		java.text.DecimalFormat format =
			(java.text.DecimalFormat) java
				.text
				.NumberFormat
				.getCurrencyInstance();
				
		format.applyPattern("#####0.00");

		return (format.format(val.doubleValue()));
	}

	/**
	 * 将日期格式化为字符串（ 四位年/两位月/两位日 两位小时:两位分 )
	 * @param date 要格式化的日期
	 * @return 格式化后的字符串
	 */
	public static String getLongDate(Date date) {
		return date!=null?sdf.format(date):"";
	}

	/**
	 * 将日期格式化为字符串（ 四位年两位月两位日两位小时两位分两位秒 )
	 * @param date 要格式化的日期
	 * @return 格式化后的字符串
	 */
	public static String getDateFile(Date date) {
		return date!=null?sdfDateFile.format(date):"";
	}

	/**
	 * 将日期格式化为字符串（ 两位小时:两位分:两位秒 )
	 * @param date 要格式化的日期
	 * @return 格式化后的字符串
	 */
	public static String getTime(Date date) {
		return date!=null?sdfTime.format(date):"";
	}
	
	/**
	 * 将日期格式化为字符串（ 四位年-两位月-两位日 )
	 * @param date 要格式化的日期
	 * @return 格式化后的字符串
	 */
	public static String getDate(Date date){
		return date!=null?sdfDate.format(date):"";
	}
	
	/**
	 * 去掉字符串的前后空格 
	 * @param value 要处理的字符串
	 * @return 处理后的字符串
	 */
	public static String mytrim(String value) {
		String result = value;
		if (value != null) {
			result = value.trim();
		}
		return result;
	}

}
