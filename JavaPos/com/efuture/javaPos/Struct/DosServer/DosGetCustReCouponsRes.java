package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 76
 * CmdMemo      = 计算小票返券信息（计算实时返回券，若是纸券则打印，否则不打，重百暂无纸券）
 */

public class DosGetCustReCouponsRes 
{
	public static String refSocket[] =
	{
		"type|S|21",        	//券类型
		"code|S|21",       		//券号
		"info|S|251",       	//券描述
		"sl|D|8",              	//数量
		"je|D|8",             	//金额
		"memo|S|251"       		//备注
	};

	public String	type;	//[20+1];        	//券类型
	public String	code;	//[20+1];        	//券号
	public String	info;	//[250+1];       	//券描述
	public double	sl;              			//数量
	public double	je;              			//金额
	public String	memo;	//[250+1];       	//备注
}

