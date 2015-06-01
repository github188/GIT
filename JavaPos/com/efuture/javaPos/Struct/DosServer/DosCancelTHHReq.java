package com.efuture.javaPos.Struct.DosServer;


/*
 * CmdCode      = 90
 * CmdMemo      = 取消退换货 
 */

public class DosCancelTHHReq
{
	public static String[] refSocket = 
	{
		"mkt|S|11",				//门店号
		"jygs|S|11",			//经营公司
		"syjh|S|5",			//收银机号
		"fphm|L|4",				//小票号
		"ishh|S|2",				//是否换货
		"transid|L|4"				//事务号
	};
	
	public String mkt;    		//[10+1];		//门店号
	public String jygs;		//[10+1];		//经营公司
	public String syjh;			//[4+1];		//收银机号
	public long fphm;			//[4];		//柜组
	public String ishh;			//[1+1];		//是否换货
	public long transid;			//[4];		//事务号
}

