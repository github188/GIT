package com.efuture.javaPos.Struct.DosServer;

/**
 * 重百新世纪储值卡 响应类
 * @author wangy
 *
 */
public class CbbhXsjMzkRes
{


	public static String refSocket[] =
	{
		"value|I|2",
		"result|S|3",
		"errmsg|S|51",
		"kh|S|21",
		"dqye|D|8",
		"time|S|21",
		"money|D|8"
	};
	
	public short value;   //暂未用：返回代码    1-消费、退货冲正 2-退货、消费冲正 3-查询 11-无此卡号 12-磁条密码错误 13-有效期小于当前日期 14-提货卡无效 15-余额不足 16-退货金额不能超过发卡金额 17-手工校验密码错误		
	public String result; //[2+1];    //返回代码 00成功 其它失败
	public String errmsg; //[50+1];     //错误信息
	public String kh; //[20+1]     			 //卡号
	public double dqye;    			 //当前余额	
	public String time; //[20+1]     			 //提交时间
	public double money;      			 //卡面额
	
}
