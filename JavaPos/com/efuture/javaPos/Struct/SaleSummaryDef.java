package com.efuture.javaPos.Struct;

// 交易汇总定义
public class SaleSummaryDef
{
	public static String[] ref = {"bc","syyh","ysje","sjfk","zl","sysy","zkje","xsbs"
		,"xsje","thbs","thje","hcbs","hcje","qxbs","qxje"};
	
	public static String[] refUpdate = {"ysje","sjfk","zl","sysy","zkje","xsbs"
		,"xsje","thbs","thje","hcbs","hcje","qxbs","qxje","bc","syyh"};
	
	public char bc;				// 收银员班次,主键					
	public String syyh;				// 收银员工号,主键
	public double ysje;			// 应收金额
	public double sjfk;			// 实际付款					
	public double zl;				// 找零						
	public double sysy;			// 损溢						
	public double zkje;			// 折扣金额					
	public int xsbs;				// 销售笔数					
	public double xsje;		  	// 销售金额					
	public int thbs;				// 退货笔数					
	public double thje;			// 退货金额					
	public int hcbs;				// 红冲笔数					
	public double hcje;			// 红冲金额					
	public int qxbs;				// 取消笔数					
	public double qxje;			// 取消金额			
	public String date;         // 日期时间
}
