package com.efuture.javaPos.Struct;

// 电子秤模板定义
public class DzcModeDef
{
	public static String[] ref = {"symbol","length","symbolpos","symbollen","codepos","codelen",
		"pricepos","pricelen","pricedec","quantitypos","quantitylen","quantitydec","timepos","timelen"};
	
	public String symbol;			// 标识符					
	public int length;				// 总长度					
	public int symbolpos;			// 标识符位置					
	public int symbollen;			// 标识符长度					
	public int codepos;			// 实际编码位置				
	public int codelen;			// 实际编码长度				
	public int pricepos;			// 价格位置					
	public int pricelen;			// 价格长度					
	public int pricedec;			// 价格小数位数				
	public int quantitypos;		// 数量位置					
	public int quantitylen;		// 数量长度					
	public int quantitydec;		// 数量小数位数				
	public int timepos;			// 生产时间位置				
	public int timelen;			// 生产时间长度										
}
