package com.efuture.javaPos.Struct;

// 柜组交易汇总定义
public class SaleGzSummaryDef
{
	public static String[] ref = {"bc","syyh","gz","name","xsbs","xsje","xszk","thbs","thje"
		,"thzk"};
	public static String[] refUpdate = {"xsbs","xsje","xszk","thbs","thje"
		,"thzk","bc","syyh","gz"};
	
	public char bc;				// 收银员班次,主键					
	public String syyh;				// 收银员工号,主键
	public String gz;				// 柜组号,主键
	public String name;				// 柜组名称
	public int xsbs;				// 销售笔数
	public double xsje;			// 销售金额
	public double xszk;			// 销售折扣
	public int thbs;				// 退货笔数
	public double thje;			// 退货金额
	public double thzk;			// 退货折扣
}
