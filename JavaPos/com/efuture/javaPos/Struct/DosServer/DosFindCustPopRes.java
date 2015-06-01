package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 75
 * CmdMemo      = 查找会员促销信息 
 */

public class DosFindCustPopRes 
{
	public static String[] refSocket = 
	{
		"seqno|L|4"	,	/* 序号						*/
		"djbh|S|16"	,	/* 优惠单号					*/
		"type|C|1"	,	/* 优惠类别					*/
		"code|S|14"	,	/* 商品编码,核算码			*/
		"gz|S|21"	,	/* 柜组						*/
		"dzxl|S|11"	,	/* 商品类别					*/
		"pp|S|7"	,	/* 品牌						*/
		"spec|S|3"	,	/* 多单位规格，'00'-基础信息,'AL'-所有规格都优惠,'xx'-指定规格优惠 */
		"ksrq|S|11"	,	/* 开始日期					*/
		"jsrq|S|11"	,	/* 结束日期					*/
		"kssj|S|81"	,	/* 开始时间					*/
		"jssj|S|81"	,	/* 结束时间					*/
		"yhsl|D|8"	,	/* 满减上限金额*/
		"yhspace|D|8",	/* 组合码（1210,打折，满减，返券，返礼） 	*/
		"yhlsj|D|8"	,	/* 满的条件金额： 满金额n*/
		"yhhyj|D|8"	,	/* 减现的金额：   减金额n*/
		"yhpfj|D|8"	,	/* 促销档期				*/
		"yhzkl|D|8"	,	/* BHLS:规则促销的打折比例				*/
		"yhhyzkl|D|8",	/* BHLS:促销折上折标志			*/
		"yhpfzkl|D|8",	/* 忽略其他积分优惠（1是，0否）			*/
		"zkfd|D|8"	,	/* 折扣分担				*/
		"hyzkfd|D|8",	/* 规则促销折扣控制			*/
		"pfzkfd|D|8",	/* 积分倍率				*/
		"rule|S|101",	/* 商品属性码*/
		"rulemode|S|101",	/* 接券规则：券种,满,收,促销分组|券种,满,收,促销分组*/
		"popbillno|S|101",	/* 活动单号					*/
		"memo|S|101",	/* 满减规则,返券规则,返礼规则,抽奖规则,价随量变规则,价随量变折扣分担  */
		"str1|S|101",	/* ALL:会员与非会员/@1:全部会员/@2:非会员/首字符为#:卡分组） */
		"str2|S|101",	/* 活动单号,收券规则编码（收券按该项合并）,收券规则单据编号,收B券模式,B券条件金额,收A券模式,A券条件金额  收券模式:3-比率,6-满收,是否收券：0-不收券,1-可以收券*/
		"str3|S|101",	/* 多级满减(由大到小)：满金额n-1,减金额n-1;满金额n-2,减金额n-2;满金额n-3,减金额3-2; ...*/
		"str4|S|101",	/* 分组码*/
		"str5|S|101",	/* */
		"num1|D|8"	,	/* 收A券比率或满收金额*/
		"num2|D|8"	,	/* 收B券比率或满收金额 */
	};
	
	public long seqno;			//				/* 序号						*/
	public String djbh;			//[15+1]		/* 优惠单号					*/
	public char type;			//				/* 优惠类别					*/
	public String code;			//[13+1];		/* 商品编码,核算码			*/
	public String gz;			//[20+1];		/* 柜组						*/
	public String dzxl;			//[10+1];		/* 商品类别					*/
	public String pp;			//[6+1];		/* 品牌						*/
	public String spec;			//[2+1];		/* 多单位规格，'00'-基础信息,'AL'-所有规格都优惠,'xx'-指定规格优惠 */
	public String ksrq;			//[10+1];		/* 开始日期					*/
	public String jsrq;			//[10+1];		/* 结束日期					*/
	public String kssj;			//[80+1];		/* 开始时间					*/
	public String jssj;			//[80+1];		/* 结束时间					*/
	public double yhsl;			//				/* 满减上限金额*/
	public double yhspace;		//				/* 组合码（1210,打折，满减，返券，返礼） 	*/
	public double yhlsj;		//				/* 满的条件金额： 满金额n*/
	public double yhhyj;		//				/* 减现的金额：   减金额n*/
	public double yhpfj;		//				/* 促销档期				*/
	public double yhzkl;		//				/* BHLS:规则促销的打折比例				*/
	public double yhhyzkl;		//				/* BHLS:促销折上折标志			*/
	public double yhpfzkl;		//				/* 忽略其他积分优惠（1是，0否）			*/
	public double zkfd;			//				/* 折扣分担				*/
	public double hyzkfd;		//				/* 规则促销折扣控制			*/
	public double pfzkfd;		//				/* 积分倍率				*/
	public String rule;			//[100+1];		/* 商品属性码*/
	public String rulemode;		//[100+1];		/* 接券规则：券种,满,收,促销分组|券种,满,收,促销分组*/
	public String popbillno;	//[100+1];		/* 活动单号					*/
	public String memo;			//[100+1];		/* 满减规则,返券规则,返礼规则,抽奖规则,价随量变规则,价随量变折扣分担  */
	public String str1;			//[100+1];		/* ALL:会员与非会员/@1:全部会员/@2:非会员/首字符为#:卡分组） */
	public String str2;			//[100+1];		/* 活动单号,收券规则编码（收券按该项合并）,收券规则单据编号,收B券模式,B券条件金额,收A券模式,A券条件金额  收券模式:3-比率,6-满收,是否收券：0-不收券,1-可以收券*/
	public String str3;			//[100+1];		/* 多级满减(由大到小)：满金额n-1,减金额n-1;满金额n-2,减金额n-2;满金额n-3,减金额3-2; ...*/
	public String str4;			//[100+1];		/* 分组码*/
	public String str5;			//[100+1];		/* */
	public double num1;			//				/* 收A券比率或满收金额*/
	public double num2;			//				/* 收B券比率或满收金额 */
}
