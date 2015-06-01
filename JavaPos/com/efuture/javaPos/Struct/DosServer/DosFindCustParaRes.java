package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 77
 * CmdMemo      = 下载促销参数定义
*/

public class DosFindCustParaRes 
{
	public static String refSocket[] =
	{
		"code|S|5",
		"name|S|101",
		"value|S|251"
	};
	
	public String code;	//[4+1];			/* 参数代码					*/
	public String name;	//[100+1];			/* 参数说明					*/
	public String value;	//[250+1];			/* 参数值					*/
}
