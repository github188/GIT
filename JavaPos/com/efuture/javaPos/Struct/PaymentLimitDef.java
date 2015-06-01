package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class PaymentLimitDef implements Cloneable,Serializable
{
	private static final long serialVersionUID = 1L;

	public static String[] ref = {"syjh", "paycode", "paycap", "captype", "memo"};
	
	public String syjh;					// 收银机号
	
	public String paycode;				// 付款代码
	
	public double paycap;				// 付款上限
	
	public char captype;				// 上限类型    A-单笔交易金额上限 B-全天交易金额上限
	
	public String memo;					// 备用字段
	
	public Object clone()
	{
		try 
		{
			return super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			e.printStackTrace();
			return this;
		}
	}
}
