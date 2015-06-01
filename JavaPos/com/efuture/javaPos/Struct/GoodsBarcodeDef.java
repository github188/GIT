package com.efuture.javaPos.Struct;

//商品代码转换模版
public class GoodsBarcodeDef 
{
	public static String[] ref = {"barcode","gdbarcode","gdname","gdbzhl"};
	public static String[] key = {"barcode","gdbarcode"};
	
	public String barcode;   	//原条码
	public String gdbarcode; 	//根据原条码区得的条码
	public String gdname; 		//商品描述
	public double gdbzhl;		//包装含量
}
