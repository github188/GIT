package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class MzkRequestDef implements Cloneable,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String[] ref = {"type","seqno","termno","mktcode","syjh","fphm","syyh","invdjlb","paycode",
		"je","track1","track2","track3","passwd","memo",
		"str1","str2","str3","num1","num2","num3"};
	
    public String type;					// 交易类型,'01'-消费,'02'-消费冲正,'03'-退货,'04'-退货冲正,'05'-查询,'06'-冻结
    public long seqno;					// 交易序号
    public String termno;				// 终端号
    public String mktcode;				// 门店号
    public String syjh;					// 收银机号
    public long fphm;					// 小票号
    public String syyh;					// 收银员号
    public String invdjlb;				// 小票交易类型
    public String paycode;				// 付款代码
    public double je;					// 交易金额
    public String track1;				// 磁道一
    public String track2;				// 磁道二
    public String track3;				// 磁道三
    public String passwd;				// 密码
    public String memo;					// 备注
    public String str1;					// 备用 超市用于存放修改后的新密码
    public String str2;					// 备用
    public String str3;					// 备用
    public double num1;					// 备用
    public double num2;					// 备用
    public double num3;					// 备用
    
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
