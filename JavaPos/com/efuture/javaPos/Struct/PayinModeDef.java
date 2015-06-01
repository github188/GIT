package com.efuture.javaPos.Struct;

// 缴款模板定义
public class PayinModeDef
{
	public static String[] ref = {"code","name","type","base","hl"};
	
	public String code;				// 项目代码,主键						
	public String name;				// 项目名称					
	public char type;				// 分类,同付款方式定义						
	public double base;			// 基数						
	public double hl;				// 汇率						
}
