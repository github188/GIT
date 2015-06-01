package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 76
 * CmdMemo      = 计算小票返券信息（计算实时返回券，若是纸券则打印，否则不打，重百暂无纸券）
 */

public class DosGetCustReCouponsReq 
{
	public static String refSocket[] = 
	{
		"jygs|S|11",        //经营公司
		"mkt|S|11",         //门店号
		"ysyjh|S|5",        //原收银机号
		"yfphm|S|8",        //原小票号
		"vcdbz|S|9"         //备用	
	};
	                                            
	public String jygs;		//[10+1];        //经营公司
	public String mkt;		//[10+1];         //门店号
	public String ysyjh;	//[4+1];        //原收银机号
	public String yfphm;	//[7+1];        //原小票号
	public String vcdbz;	//[8+1];        //备用
}
