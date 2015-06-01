package com.efuture.javaPos.Struct;

// 顾客信息采集类
public class BuyerInfoDef
{
	public static String[] ref = {"code","type","sjcode","name"};
	
	public String code;				// 代码,主键		
	public char type;				// 类型,主键	;树模式,代表上级
	public String sjcode;			// 上级代码
	public String name;				// 名称						
}
