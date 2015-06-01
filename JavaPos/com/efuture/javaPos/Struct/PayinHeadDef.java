package com.efuture.javaPos.Struct;

// 缴款单头定义
public class PayinHeadDef
{
	public static String[] ref = {"syjh","syyh","rqsj","jkrq","jkbc","seqno","je","netbz","hcbz"};
	
	public String syjh;				// 收银机号,唯一键					
	public int seqno;				// 缴款单序号,唯一键
	public String rqsj;				// 日期时间		
	public String syyh;				// 收银员号					
	public String jkrq;				// 缴款日期
	public char jkbc;				// 缴款班次
	public double je;				// 合计金额										
	public char netbz;				// 送网标志
	public char hcbz;				// 红冲标志
	
	// -----------------------
	// 重打印标志，不记录本地
	public String reprint;			//重打印标志
}

