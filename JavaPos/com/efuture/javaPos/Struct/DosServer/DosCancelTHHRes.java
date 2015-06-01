package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 90
 * CmdMemo      = 取消退换货 
 */

public class DosCancelTHHRes
{
	public static String[] refSocket = 
	{
		"msg|S|251"				//
	};
	
	public String msg;    		//[250+1];		//返回提示信息，第1位为Y表示成功，其它表示失败，第2位开始为报错提示信息
}

