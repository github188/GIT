package com.efuture.javaPos.Struct.DosServer;

/**
 * 重百储值卡 响应类
 * @author wangy
 *
 */
public class CbbhMzkRes
{

	public static String refSocket[] =
	{
		"retcode|S|3",
		"errmsg|S|41",
		"cardno|S|21",
		"amount|S|13",
		"curdate|S|11",
		"curtime|S|9",
		"name|S|21",
		"memo|S|21"
	};
	
	public String retcode; //[2+1];    //应答码,’00’-成功,否则不成功
	public String errmsg; //[40+1];    //错误信息,如果交易不成功提示该信息
	public String cardno; //[20+1];     //卡号
	public String amount; //[12+1]     			 //余额，精确到分,比如10.05元则是1005（不含小数点）
	public String curdate; //[10+1]     			 //交易日期	
	public String curtime; //[8+1]     			 //交易时间
	public String name; //[20+1]     			 //持卡人
	public String memo; //[20+1]     			 //保留
	
}
