package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class MyStoreCouponDef implements Cloneable, Serializable
{
	private static final long serialVersionUID = 0L;
	public static String[] ref = { "type", "code", "info", "sl", "je", "startdate", "enddate", "memo" };

	public String type; 
	public String title;// 标题
	public String code; // 代码
	public String info; // 描述
	public int sl; // 数量
	public double je; // 金额
	public String startdate; // 券起始有效期
	public String enddate; // 券截止有效期
	public String memo; // 备注

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
