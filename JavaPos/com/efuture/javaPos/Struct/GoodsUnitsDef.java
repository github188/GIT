package com.efuture.javaPos.Struct;

// 商品多单位定义
public class GoodsUnitsDef
{
	public static String[] ref={"code","uid","unit","bzhl","barcode"};
	public static String[] refLocal={"code","uid","unit","bzhl","barcode"};	
	public static String[] key = {"code","uid"};
	
	public String code;				// 商品编码,核算码,主键			
	public String uid;				// 单位代码,主键		
	public String unit;				// 单位			
	public double bzhl;			// 包装含量
	public String barcode;			// 对应的商品信息中的条码		
}
