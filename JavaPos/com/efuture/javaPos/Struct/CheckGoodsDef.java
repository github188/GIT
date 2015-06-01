package com.efuture.javaPos.Struct;

// 盘点商品明细
public class CheckGoodsDef 
{
	public static String[] ref = {"code","gz","row","kcsl","kcje","pdsl","pdje","uid","bzhl"};
	
	public String code;   		//	商品编码
	public String gz; 			//	柜组
	public int   row;			// 	行号
	public String kcsl;			//	帐存数量(null表示无帐存返回)
	public String kcje;			//	帐存金额(null表示无帐存返回)
	public String pdsl;			//	实盘数量(null表示没有输入实盘数量)
	public String pdje;			//	实盘金额(null表示没有输入实盘金额)
	public String uid;			//  多单位码
	public double bzhl;		//  包装含量 
	
	public String handInputcode; //中商平价盘点要求记录手工输入的编码
}
