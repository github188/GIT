package com.efuture.javaPos.Struct;

// 商品批量定义
public class GoodsAmountDef
{
	public static String[] ref=      {"code","gz","uid","pllsj","plhyj","plsl","ksrq","jsrq","memo"};
	public static String[] refLocal= {"code","gz","uid","pllsj","plhyj","plsl","ksrq","jsrq","memo"};
	
	public static String[] key = {"code","gz","uid","plsl"};
	
	public String code;				// 商品编码,主键
	public String gz;				// 柜组，主键
	public String uid;				// 规格单位,主键
	public double pllsj;			// 批量售价
	public double plhyj;			// 批量会员价				
	public double plsl;			// 批量数量,主键
	public String ksrq;				// 开始日期,YYYY/MM/DD				
	public String jsrq;				// 结束日期,YYYY/MM/DD
	public String memo;				// 备注
}

