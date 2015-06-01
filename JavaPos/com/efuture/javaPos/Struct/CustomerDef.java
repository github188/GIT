package com.efuture.javaPos.Struct;

import java.io.Serializable;

// 会员卡资料定义
public class CustomerDef implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String[] ref = {"code","type","name","status","maxdate","track","memo","valuememo",
		"str1","str2","str3","num1","num2","num3",
		"ishy","iszk","isjf","func","zkl","popzkl","deliveryinfo","value1","value2","value3","value4","value5","value6","value7","value8","value9","value10",
		"valstr1","valstr2","valstr3","valnum1","valnum2","valnum3","valstr4","valstr5","valstr6","valstr7","valstr8","valstr9","valstr10", "pwdje"};
	
	public static String[] refLocal = {"code","type","name","status","maxdate","track","memo","valuememo",
		"str1","str2","str3","num1","num2","num3"};
	
	public static String[] key = {"code"};
	
	public String code;				// 卡号,主键
	public String type;				// 卡类型
	public String name;				// 持卡人姓名
	public String status;			// 卡状态
	public String maxdate;			// 卡有效期
	public String track;			// 磁道卡号,唯一键
	public String memo;				// 积分折现规则,XX积分换XX元(100,10)
	public double valuememo;		// 积分
	public String str1;				// 备用
									// CCZZ:积分倍率
									// BJYS:手机号
	public String str2;				// 备用
	public String str3;				// 备用
	public double num1;			// 备用
	public double num2;			// 备用
	public double num3;			// 备用
	
	// 本地数据库无以下字段
	public char ishy;				// 是否会员
	public char iszk;				// 是否打折
	public char isjf;				// 是否积分
	public String func;				// 其他功能
									// [0]:是否零钞转存
									// [1]:是否临时返券卡
									// [2]:是否为团购卡
									// 
	public double zkl;				// 普通折扣率
	public double popzkl;			// 促销折扣率
	public String deliveryinfo;		// 送货信息str1:{str1};str2:{str2};str3:{str3};|str1:{str1};str2:{str2};str3:{str3};
	public double value1;			// 保留		
									// HZJB:零钞转存余额
									// 
	public double value2;			// 保留		
									// HZJB:零钞转存卡中最大的限额
	public double value3;			// 保留
	public double value4;			// 保留
									// HZJB YWJB：积分类型1-普通积分、2-档期积分
	public double value5;			// 保留
									// HZJB YWJB: 档期积分 
									// CCZZ:是否打印办卡联
	public double value6;			// 保留
									// NNMK:当期积分
	public double value7;			// 保留
									// NNMK:历史积分
	public double value8;			// 保留
	public double value9;			// 保留
	public double value10;			// 保留
	public String valstr1;			// 保留
									// BJYS:会员消费提醒
	public String valstr2;			// 保留
									// HZJB YWJB: 限额
									// CCZZ 账号
	public String valstr3;			// 保留
									// CMLS 柜组列表      
	public String valstr4;			// 保留
	public String valstr5;			// 保留
	public String valstr6;			// 保留
									// 零钞转存支付密码
	public String valstr7;			// 保留
	public String valstr8;			// 保留
	public String valstr9;			// 保留
	public String valstr10;			// 保留
	
	public double valnum1;			// 保留
	public double valnum2;			// 保留
	public double valnum3;			// 保留
    public double pwdje;				// 零钞免密限额
	
	public boolean isHandInput;		//是否手工输入的卡号
	public boolean ispay;			// 标准，是否允许付款消费 
}
