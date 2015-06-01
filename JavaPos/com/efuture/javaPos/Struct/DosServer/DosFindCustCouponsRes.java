package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 79
 * CmdMemo      = 获取可用券信息(消费时获取可用电子券)
*/

public class DosFindCustCouponsRes 
{

	public static String refSocket[] =
	{
		"type|C|1",
		"name|S|61",
		"ye|D|8",
		"hl|D|8",
		"flag|C|1"
	};
	
	public char type;			//券类型
	public String name; 		//[60+1];	//券说明
	public double ye;			//券余额
	public double hl;			//券汇率
	public char flag;			//电子券1/手工券2
}
