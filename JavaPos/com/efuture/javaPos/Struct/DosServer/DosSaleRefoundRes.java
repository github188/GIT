package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 86
 * CmdMemo      = 获取小票扣回信息（86--》52--》83）
*/


public class DosSaleRefoundRes
{

	public static String[] refSocket = 
	{
		"msg|S|251",
		"jfkhje|D|8", 
		"jfkhdesc|S|256", 
		"fqkhje|D|8", 
		"fqkhdesc|S|256", 
		"qtkhje|D|8",
		"qtkhdesc|S|256",
		"sellpays|S|513",
		"goodpays|S|513"
		//"goodpays|S|1025",
		//"sellpays|S|1025"
	};
	
	public String msg;
	public double jfkhje;
	public String jfkhdesc;
	public double fqkhje;
	public String fqkhdesc;
	public double qtkhje;
	public String qtkhdesc;
	
	public String sellpays;///*换销商品的固定付款方式   行号rowno;付款方式代码paycode;原币金额ybje;卡号payno;劵种inno,盈余金额kye;标志flag1付款2找零3扣回,*/
	public String goodpays;// /*换退商品分摊 小票明细行号;商品编码;付款代码;卡号;分摊金额,行号;商品编码;付款代码;卡号;分摊金额,*/ 
	
}
