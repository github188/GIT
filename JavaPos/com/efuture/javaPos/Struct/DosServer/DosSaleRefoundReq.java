package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 86
 * CmdMemo      = 获取小票扣回信息（86--》52--》83）
*/

public class DosSaleRefoundReq
{
	public static String[] refSocket = 
	{
		"mkt|S|11",
		"jygs|S|11",
		"syjh|S|5",
		"fphm|L|4",
		"ishh|S|3"
	};
	
	public String mkt;
	public String jygs;
	public String syjh;
	public long fphm;
	public String ishh;//是否换货Y/N
		 
}
