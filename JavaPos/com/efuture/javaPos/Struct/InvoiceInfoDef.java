package com.efuture.javaPos.Struct;

// 收银机网上交易信息
public class InvoiceInfoDef
{
	public static String[] ref={"maxinv","bs","je"};
	
	public long maxinv;			// 最大交易号
	public int bs;					// 当日已送网交易笔数
	public double je;				// 当日已送网交易金额				
}
