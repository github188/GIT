package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 10
 * CmdMemo      = 查找会员信息
*/

public class DosFindCustReq
{
	public static String[] refSocket = 
	{
		"mkt|S|11",
		"jygs|S|11",
		"track|S|121"
	};
	
	public String mkt;
	public String jygs;
	public String track;
		 
}
