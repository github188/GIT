package com.efuture.javaPos.Struct.DosServer;

/**
 * 重百储值卡 请求类
 * @author wangy
 *
 */
public class CbbhMzkReq
{

	public static String refSocket[] =
	{
		"seqno|S|7",
		"type|S|3",
		"termno|S|9",
		"syyh|S|9",
		"invno|S|8",
		"amount|S|13",
		"track2|S|38",
		"track3|S|105",
		"passwd|S|7",
		"memo|S|21"
	};
	
	public String seqno; //[6+1];    //交易流水，每台每笔交易产生一个流水
	public String type; //[2+1];    //交易类型,’01’-消费,’02’-消费冲正,’03’-退货,’04’-退货冲正,’05’-余额查询
	public String termno; //[8+1];     //终端号，统一编码，建立对照表能确定到是哪个分公司的哪个
	public String syyh; //[8+1]     			 //收银员号	
	public String invno; //[7+1]     			 //款机的销售小票号
	public String amount; //[12+1]     			 //交易金额，精确到分,比如10.05元则传1005（不含小数点）
	public String track2; //[37+1]     			 //2磁道信息
	public String track3; //[104+1]     			 //3磁道信息
	public String passwd; //[6+1]     			 //是否输入密码Y/N（取自参数的第一个字符）
	public String memo; //[20+1]     			 //面值卡密码
	
}
