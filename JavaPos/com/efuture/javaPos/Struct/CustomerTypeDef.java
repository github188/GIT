package com.efuture.javaPos.Struct;


// 会员卡类型定义
public class CustomerTypeDef
{
	public static String[] ref = {"code","ishy","iszk","isjf","func","zkl","popzkl",
		"value1","value2","value3","value4","value5",
		"valstr1","valstr2","valstr3","valnum1","valnum2","valnum3"};
	
	public static String[] key = {"code"};
	
	public String code;				// 卡类型代码,主键				
	public char ishy;				// 是否会员
	public char iszk;				// 是否打折
	public char isjf;				// 是否积分
	public String func;				// 保留
									// [0]-是否有零钞转存功能(Y/N)
									// [1]-是否允许突破商品最低折扣控制
									// [2]-
	public double zkl;				// 普通折扣率
	public double popzkl;			// 促销折扣率
	public double value1;			// 保留						
	public double value2;			// 保留						
	public double value3;			// 保留
	public double value4;			// 保留						
	public double value5;			// 保留
									// CCZZ:是否打印办卡联
	public String valstr1;			// 保留
	public String valstr2;			// 保留
	public String valstr3;			// 保留
	public double valnum1;			// 保留
	public double valnum2;			// 保留
	public double valnum3;			// 保留
}
