package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 52
 * CmdMemo      = 上传销售小票信息至CRM
*/

public class DosSendSaleComReq 
{
	public static String refSocket[] = 
	{
		"yyyh|S|9",			/* 营业员					*/
		"barcode|S|14",		/* 商品条码					*/
		"code|S|14",		/* 商品编码					*/
		"type|C|1",			/* 编码类别					*/
		"gz|S|21",			/* 柜组						*/
		"dzxl|S|11",		/* 商品类别					*/
		"pp|S|7",			/* 品牌						*/
		"spec|S|3",			/* 规格						*/
		"batch|S|16",		/* 批号						*/
		"yhdjbh|S|16",	/* 优惠单据编号				*/
		"name|S|41",		/* 名称						*/
		"unit|S|5",			/* 单位						*/
		"bzhl|D|8",				/* 包装含量					*/
		"sl|D|8",				/* 销售数量					*/
		"lsj|D|8",				/* 零售价					*/
		"jg|D|8",				/* 销售价格					*/
		"zje|D|8",				/* 总金额(sl*jg)			*/
		"hyzke|D|8",			/* 会员折扣额(来自会员优惠)	*/
		"yhzke|D|8",			/* 优惠折扣额(来自营销优惠)	*/
		"yhzkfd|D|8",			/* 优惠折扣分担				*/
		"lszke|D|8",			/* 零时折扣额(来自手工打折)	*/
		"lszre|D|8",			/* 零时折让额(来自手工打折)	*/
		"zzke|D|8",				/* 总品折扣					*/
		"zzre|D|8",				/* 总品折让					*/
		"plzke|D|8",			/* 批量折扣					*/
		"zszke|D|8",			/* 赠送折扣					*/
		"sqkh|S|14",				/* 单品授权卡号				*/
		"sqktype|C|1",			/* 单品授权卡类别			*/
		"pfzkfd|D|8",			/* 批发折扣分担				*/
		"spzkfd|D|8",			/* 会员折扣分担或以旧换新折扣分担*/
		"xxtax|D|8",			/* 税率						*/
		"flag|C|1",				/* 商品标志，0-小计行,1-赠品,2-电子秤条码，3-削价，4-一般 */
		"yjhxcode|S|14",	/* 以旧换新条码				*/
		"ysyjh|S|5",		/* 原收银机号				*/
		"yfphm|L|4",		/* 原小票号					*/
		"fhdd|S|21",		/* 发货地点					*/
		"commemo|S|101",	/* 备注：最大收券额,会员卡类型          */
		"comstr1|S|101",	//记录积分换购信息：付款方式代码,卡号,换购金额,换购积分,限量单据编号
		"comstr2|S|251",	/*记录分摊金额 付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额.......*/
		"comstr3|S|251",	/*活动规则|D|8",商品属性码|D|8",满减规则,返券规则,返礼规则(逗号分格)|D|8",忽略其他积分优惠（1是，0否）|D|8",积分倍率|D|8",促销档期|D|8",会员商品限量促销单|D|8",限量标志*/
		"comstr9|S|21",    	//分组码
		"comnum1|D|8",			/* 记录是否特价的标志 0-非特价码 2-取特价码的积分率*/
		"comnum2|D|8",			/* 会员限量促销的价格*/
		"popje|D|8",			/* 促销金额          		*/
		"zsdjbh|S|16",			/* 促销单号              */
		"zszkfd|D|8",			/* 促销折扣分担             */
		"comnum4|D|8",			/* 记录银行追送的折扣额*/
		"comstr4|S|101",	/* 第1位是小票的单据编号,第2位开始是银行追送的单据编号  */
		"comstr5|S|251",	/*记录分摊金额 付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额.......*/
		"comstr6|S|251",	/*记录分摊金额 付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额.......*/
		"comstr7|S|251",	/*记录分摊金额 付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额.......*/
		"comstr8|S|251"	    /*记录分摊金额 付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额.......*/	
	};
	
	
	public String yyyh;//[8+1];			/* 营业员					*/
	public String barcode;//[13+1];		/* 商品条码					*/
	public String code;//[13+1];		/* 商品编码					*/
	public char type;					/* 编码类别					*/
	public String gz;//[20+1];			/* 柜组						*/
	public String dzxl;//[10+1];		/* 商品类别					*/
	public String pp;//[6+1];			/* 品牌						*/
	public String spec;//[2+1];			/* 规格						*/
	public String batch;//[15+1];		/* 批号						*/
	public String yhdjbh;//[15+1];		/* 优惠单据编号				*/
	public String name;//[40+1];		/* 名称						*/
	public String unit;//[4+1];			/* 单位						*/
	public double bzhl;				/* 包装含量					*/
	public double sl;				/* 销售数量					*/
	public double lsj;				/* 零售价					*/
	public double jg;				/* 销售价格					*/
	public double zje;				/* 总金额(sl*jg)			*/
	public double hyzke;			/* 会员折扣额(来自会员优惠)	*/
	public double yhzke;			/* 优惠折扣额(来自营销优惠)	*/
	public double yhzkfd;			/* 优惠折扣分担				*/
	public double lszke;			/* 零时折扣额(来自手工打折)	*/
	public double lszre;			/* 零时折让额(来自手工打折)	*/
	public double zzke;				/* 总品折扣					*/
	public double zzre;				/* 总品折让					*/
	public double plzke;			/* 批量折扣					*/
	public double zszke;			/* 赠送折扣					*/
	public String sqkh;//[13+1];	/* 单品授权卡号				*/
	public char   sqktype;			/* 单品授权卡类别			*/
	public double pfzkfd;			/* 批发折扣分担				*/
	public double spzkfd;			/* 会员折扣分担或以旧换新折扣分担*/
	public double xxtax;			/* 税率						*/
	public char flag;				/* 商品标志，0-小计行,1-赠品,2-电子秤条码，3-削价，4-一般 */
	public String yjhxcode;//[13+1];	/* 以旧换新条码				*/
	public String ysyjh;//[4+1];		/* 原收银机号				*/
	public long yfphm;					/* 原小票号					*/
	public String fhdd;//[20+1];		/* 发货地点					*/
	public String commemo;//[100+1];	/* 备注：最大收券额,会员卡类型          */
	public String comstr1;//[100+1];	//记录积分换购信息：付款方式代码,卡号,换购金额,换购积分,限量单据编号
	public String comstr2;//[250+1];	/*记录分摊金额 付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额.......*/
	public String comstr3;//[250+1];	/*活动规则;商品属性码;满减规则,返券规则,返礼规则(逗号分格);忽略其他积分优惠（1是，0否）;积分倍率;促销档期;会员商品限量促销单;限量标志*/
	public String comstr9;//[20+1];     //分组码
	public double comnum1;				/* 记录是否特价的标志 0-非特价码 2-取特价码的积分率*/
	public double comnum2;				/* 会员限量促销的价格*/
	public double popje;				/* 促销金额          		*/
	public String zsdjbh;//[15+1];		/* 促销单号              */
	public double zszkfd;				/* 促销折扣分担             */
	public double comnum4;				/* 记录银行追送的折扣额*/
	public String comstr4;//[100+1];	/* 第1位是小票的单据编号,第2位开始是银行追送的单据编号  */
	public String comstr5;//[250+1];	/*记录分摊金额 付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额.......*/
	public String comstr6;//[250+1];	/*记录分摊金额 付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额.......*/
	public String comstr7;//[250+1];	/*记录分摊金额 付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额.......*/
	public String comstr8;//[250+1];	/*记录分摊金额 付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额.......*/
}
