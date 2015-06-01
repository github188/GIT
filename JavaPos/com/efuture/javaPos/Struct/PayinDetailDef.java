package com.efuture.javaPos.Struct;

// 缴款明细定义
public class PayinDetailDef
{
	public static String[] ref = {"syjh","seqno","rowno","code","zs","je","hl"};
	
	public String syjh;				// 收银机号,唯一键					
	public int seqno;				// 缴款单序号,唯一键
	public int rowno;				// 行号,唯一键
	public String code;				// 缴款代码
	public int zs;					// 张数
	public double je;				// 金额
	public double hl;				// 汇率	
}
