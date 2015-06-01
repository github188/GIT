package com.efuture.javaPos.Struct;

// 收银机状态类
public class SyjStatusDef
{
	static public String[] ref = {"syjh","fphm","status","netstatus","bc","syyh","bs","je","xjje"};
	
	public String syjh;					// 收银机号,	主键
	public long fphm;					// 当前最大小票号				
	public char status;				// 当前收银状态				
	public char netstatus;				// 当前网络状态				
	public char bc;					// 当前班次					
	public String syyh;					// 当前收银员					
	public int bs;						// 当前销售笔数，销售+退货		
	public double je;					// 当前销售金额，销售+退货		
	public double xjje;				// 当前现金存量
}
