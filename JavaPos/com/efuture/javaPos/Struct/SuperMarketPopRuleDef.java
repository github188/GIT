package com.efuture.javaPos.Struct;

public class SuperMarketPopRuleDef
{

	public static String[] ref = {
									"seqno",
									"djbh",
									"type",
									"code",
									"gz",
									"dzxl",
									"pp",
									"spec",
									"ksrq",
									"jsrq",
									"kssj",
									"jssj",
									"zkfd",
									"yhspace",
									"yhlsj",
									"yhhyj",
									"yhzkl",
									"yhhyzkl",
									"yhpfj",
									"yhpfzkl",
									"yhhyzkfd",
									"yhpfzkfd",
									"yhdjlb",
									"yhplsl",
									"presentcode",
									"title",
									"maxnum",
									"presentcode1",
									"presentunit",
									"presentjs",
									"presentsl",
									"presentxl",
									"presentjg",
									"jc",
									"ppistr1",
									"ppistr2",
									"ppistr3",
									"ppistr4",
									"ppistr5",
									"ppistr6",
									"iszsz",
									"isptgz",
									"istjn" };

	public long seqno; /* 序号						*/
	public String djbh; /* 优惠单号					*/
	public char type; /* 优惠类别					*/
	public String code; /* 商品编码,核算码			*/
	public String gz; /* 柜组						*/
	public String dzxl; /* 商品类别					*/
	public String pp; /* 品牌						*/
	public String spec; /* 多单位规格，'00'-基础信息,'AL'-所有规格都优惠,'xx'-指定规格优惠 */
	public String ksrq; /* 开始日期					*/
	public String jsrq; /* 结束日期					*/
	public String kssj; /* 开始时间					*/
	public String jssj; /* 结束时间					*/
	public double zkfd; /* 商家折扣分担				*/
	public double yhspace; /* 电子秤商品的优惠的时间间隔 	*/
	public double yhlsj; /* 优惠零售价					*/
	public double yhhyj; /* 优惠会员价					*/
	public double yhzkl; /* 优惠折扣率					*/
	public double yhhyzkl; /* 优惠会员折扣率				*/
	public double yhpfj; /* 优惠批发价					*/
	public double yhpfzkl; /* 优惠批发折扣率				*/
	public double yhhyzkfd; /* 优惠会员折扣分担				*/
	public double yhpfzkfd; /* 优惠批发折扣分担				*/
	public char yhdjlb; /* 优惠单据类别，其中7为批量优惠单*/
	public double yhplsl; /* 批量优惠数量					*/
	public String presentcode; /* 印花码						*/

	//规则促销新增字段
	public String title; //
	public double maxnum; //
	public String presentcode1; //赠品编码
	public String presentunit; //赠品单位
	public double presentjs; //赠品件数
	public double presentsl; //赠品数量
	public double presentxl; //赠品限量,用于是否系列商品:0->非系列商品,1->是系列商品
	public double presentjg; //赠品价格,用于取价方式：0->取价格,1->取折扣,2->取扣额,3->用于其它用途
	public long jc; //级次
	public String ppistr1;
	public String ppistr2;
	public String ppistr3;
	public String ppistr4;
	public String ppistr5;
	public String ppistr6;
	public String iszsz;
	public String isptgz;
	public String istjn;

}
