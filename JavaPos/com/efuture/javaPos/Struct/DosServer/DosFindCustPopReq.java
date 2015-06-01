package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 75
 * CmdMemo      = 查找会员促销信息 
 */

public class DosFindCustPopReq
{
	public static String[] refSocket = 
	{
		"mkt|S|31",				//经营公司，门店号
		"barcode|S|21",			//商品条码
		"code|S|14",			//商品编码
		"gz|S|21",				//柜组
		"spec|S|3",				//规格
		"spsx|S|3",				//属性码
		"dzxl|S|11",			//小类
		"pp|S|7",				//品牌
		"yhsj|S|20",			//优惠时间，格式：YYYY/MM/DD 24HH:MM:SS
		"cardno|S|21",			//会员卡号
		"custtype|S|5"			//会员卡类别
	};
	
	public String mkt;    		//[30+1];		//经营公司，门店号
	public String barcode;		//[20+1];		//商品条码
	public String code;			//[13+1];		//商品编码
	public String gz;			//[20+1];		//柜组
	public String spec;			//[2+1];		//规格
	public String spsx;			//[2+1];		//属性码
	public String dzxl;			//[10+1];		//小类
	public String pp;			//[6+1];		//品牌
	public String yhsj;			//[19+1];		//优惠时间，格式：YYYY/MM/DD 24HH:MM:SS
	public String cardno;		//[20+1];		//会员卡号
	public String custtype;		//[4+1];		//会员卡类别
}
