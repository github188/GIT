package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 52
 * CmdMemo      = 上传销售小票信息至CRM
*/

public class DosSendSaleOKRes
{
	public static String refSocket[] = 
	{
		"msg|S|251",
		"bcjf|D|8",
		"ljjf|D|8"
	};
	
	public String msg;//[250+1];    //返回提示信息
	public double bcjf;        		//本次积分
	public double ljjf;        		//积分余额
}
