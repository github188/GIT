package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 86
 * CmdMemo      = 验证原小票是否能退货
*/

public class DosCheckSaleBackReq 
{
	public static String refSocket[] =
	{
		"mkt|S|11",
		"jygs|S|11",
		"syjh|S|5",
		"fphm|L|4"
	};
	
	public String mkt;		//[10+1];    //门店号
	public String jygs;		//[10+1];    //经营公司
	public String syjh; 	//[4+1];     //收银机号
	public long fphm;          			 //小票号
}
