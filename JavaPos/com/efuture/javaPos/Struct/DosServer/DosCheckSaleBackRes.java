package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 86
 * CmdMemo      = 验证原小票是否能退货
*/

public class DosCheckSaleBackRes
{
	public static String refSocket[] =
	{
		"status|C|1",
		"msg|S|251"
	};
	
	public char status;     //状态，Y表示能退，其它表示不能退货
	public String msg;		//[250+1];  //报错提示
}
