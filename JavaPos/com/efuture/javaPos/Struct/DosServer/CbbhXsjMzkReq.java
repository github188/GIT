package com.efuture.javaPos.Struct.DosServer;

/**
 * 重百新世纪储值卡 请求类
 * @author wangy
 *
 */
public class CbbhXsjMzkReq
{


	public static String refSocket[] =
	{
		"seqno|S|21",
		"type|S|21",
		"termno|S|21",
		"syyh|S|21",
		"invno|S|21",
		"amount|S|21",
		"track2|S|21",
		"passwd|S|21",
		"memo|S|21"
	};
	
	public String seqno; //[20+1];    //交易流水，每台每笔交易产生一个流水
	public String type; //[20+1];    //交易类型，01消费 02消费冲正 03退货 04退货冲正 05余额查询
	public String termno; //[20+1];     //终端号，统一编码，建立对照表能确定到是哪个分公司的哪个
	public String syyh; //[20+1]     			 //收银员号	
	public String invno; //[20+1]     			 //款机的销售小票号
	public String amount; //[20+1]     			 //交易金额，不用转换，比如：10.05元则直接传10.05（含有小数点） 
	public String track2; //[20+1]     			 //2磁道信息
	public String passwd; //[20+1]     			 //密码：手工输入后六位卡号
	public String memo; //[20+1]     			 //预留
	
}
