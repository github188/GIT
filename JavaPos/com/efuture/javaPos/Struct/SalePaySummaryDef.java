package com.efuture.javaPos.Struct;

// 付款汇总定义
public class SalePaySummaryDef
{
	public static String[] ref = {"bc","syyh","paycode","payname","bs","je"};
	public static String[] refUpdate = {"bs","je","bc","syyh","paycode"};
	
	public char bc;				// 收银员班次,主键					
	public String syyh;				// 收银员工号,主键						
	public String paycode;			// 付款方式,主键						
	public String payname;			// 付款方式名称				
	public int bs;					// 笔数						
	public double je;				// 金额,付款金额-找零金额						
}
