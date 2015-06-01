package com.efuture.javaPos.Struct;

// 营业员交易汇总定义
public class SaleManSummaryDef
{
	public static String[] ref = {"bc","syyh","yyyh","name","xsbs","xsje","xszk","thbs","thje"
		,"thzk"};
	public static String[] refUpdate = {"xsbs","xsje","xszk","thbs","thje"
		,"thzk","bc","syyh","yyyh"};
	
	public char bc;				// 收银员班次,主键					
	public String syyh;				// 收银员工号,主键
	public String yyyh;				// 营业员号,主键
	public String name;				// 营业员名称
	public int xsbs;				// 销售笔数
	public double xsje;			// 销售金额
	public double xszk;			// 销售折扣
	public int thbs;				// 退货笔数
	public double thje;			// 退货金额
	public double thzk;			// 退货折扣
}
