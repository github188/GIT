package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 52
 * CmdMemo      = 上传销售小票信息至CRM
*/


public class DosSendSalePayReq
{
	public static String refSocket[] =
	{
		"paycode|S|5",
		"ybje|D|8",
		"hl|D|8",
		"payno|S|21",
		"idno|S|21",
		"kye|D|8",
		"flag|C|1"
	};
	
	public String paycode;//[4+1];	/* 付款方式代码				*/
	public double ybje;				/* 原币金额					*/
	public double hl;				/* 汇率						*/
	public String payno;//[20+1];	/* 付款卡号(20位,支票号/信用卡号/内部卡号/面值卡号)	*/
	public String idno;//[20+1];	/* 证件号码					*/
	public double kye;				/* 溢余金额					*/
	public char flag;				/* 标志:1-付款,2-找零,3-扣回*/
}
