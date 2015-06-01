package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 52
 * CmdMemo      = 上传销售小票信息至CRM
*/

public class DosSendSaleHeadReq 
{
	public static String refSocket[] = 
	{
		"comnum|I|2",			/* 商品明细的个数				*/
		"paynum|I|2",			/* 付款明细的个数				*/
		"bc|C|1",				/* 班次代码	#号表示扣回时预传*/
		"syjh|S|5",			/* 收银机号					*/
		"fphm|L|4",				/* 小票号					*/
		"djlb|C|1",				/* 小票类别					*/
		"rqsj|S|20",			/* 交易时间，发送时在数据库中还记录发送时间	*/
		"syyh|S|9",			/* 收银员号，发送时在数据库中转换为帐号		*/
		"hykh|S|21",			/* 会员卡号					*/
		"sqkh|S|14",			/* 折扣卡号					*/
		"sqktype|C|1",			/* 折扣卡类别	'1'-员工卡，'2'-打折卡		*/
		"ysje|D|8",				/* 应收金额					*/
		"sjfk|D|8",				/* 实际付款					*/
		"zl|D|8",				/* 找零						*/
		"sysy|D|8",				/* 收银损溢					*/
		"hyzke|D|8",			/* 会员折扣额(来自会员优惠)	*/
		"yhzke|D|8",			/* 优惠折扣额(来自营销优惠)	*/
		"lszke|D|8",			/* 零时折扣额(来自手工打折)	*/
		"lszre|D|8",			/* 零时折让额(来自手工打折)	*/
		"custinfo|S|7",		/* 顾客信息(每一类占2位,未选折=00)	*/
		"hhflag|C|1",						//换货标志
		"custtype|S|5",		//会员卡类型
		"mkt|S|31",			//门店号
		"str1|S|81"			//备用
	};
	                     
	public short comnum;				/* 商品明细的个数				*/
	public short paynum;				/* 付款明细的个数				*/
	public char bc;					/* 班次代码	#号表示扣回时预传*/
	public String syjh;//[4+1];		/* 收银机号					*/
	public long fphm;				/* 小票号					*/
	public String djlb;				/* 小票类别					*/
	public String rqsj;//[19+1];	/* 交易时间，发送时在数据库中还记录发送时间	*/
	public String syyh;//[8+1];		/* 收银员号，发送时在数据库中转换为帐号		*/
	public String hykh;//[20+1];	/* 会员卡号					*/
	public String sqkh;//[13+1];	/* 折扣卡号					*/
	public char sqktype;			/* 折扣卡类别	'1'-员工卡，'2'-打折卡		*/
	public double ysje;				/* 应收金额					*/
	public double sjfk;				/* 实际付款					*/
	public double zl;				/* 找零						*/
	public double sysy;				/* 收银损溢					*/
	public double hyzke;			/* 会员折扣额(来自会员优惠)	*/
	public double yhzke;			/* 优惠折扣额(来自营销优惠)	*/
	public double lszke;			/* 零时折扣额(来自手工打折)	*/
	public double lszre;			/* 零时折让额(来自手工打折)	*/
	public String custinfo;//[6+1];		/* 顾客信息(每一类占2位,未选折=00)	*/
	public char hhflag;						//换货标志
	public String custtype;//[4+1];		//会员卡类型
	public String mkt;//[30+1];		//门店号
	public String str1;//[80+1];		//备用
}
