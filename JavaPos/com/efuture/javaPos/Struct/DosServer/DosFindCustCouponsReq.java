package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 79
 * CmdMemo      = 获取可用券信息(消费时获取可用电子券)
*/

public class DosFindCustCouponsReq
{
	public static String refSocket[] =
	{
		"termno|S|11",        //终端号
		"mkt|S|11",        	//门店号
		"jygs|S|11",        	//经营公司
		"syjh|S|5",         	//收银机号
		"syyh|S|9",         	//收银员号
		"djlb|C|1",             //单据类别
		"paycode|S|5",        //付款代码
		"track1|S|121",       //一磁道信息
		"track2|S|121",       //二磁道信息
		"track3|S|121",       //三磁道信息
		"passwd|S|21",        //密码
		"memo|S|251"       	//备注
	};

	public String termno;	//[10+1];        //终端号
	public String mkt;		//[10+1];        //门店号
	public String jygs;		//[10+1];        //经营公司
	public String syjh;		//[4+1];         //收银机号
	public String syyh;		//[8+1];         //收银员号
	public char djlb;		//               //单据类别
	public String paycode;	//[4+1];         //付款代码
	public String track1;	//[120+1];       //一磁道信息
	public String track2;	//[120+1];       //二磁道信息
	public String track3;	//[120+1];       //三磁道信息
	public String passwd;	//[20+1];        //密码
	public String memo;		//[250+1];       //备注
}
