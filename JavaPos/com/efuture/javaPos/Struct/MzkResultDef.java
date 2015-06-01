package com.efuture.javaPos.Struct;

public class MzkResultDef implements Cloneable
{
	public static String[] ref = {"cardno","cardname","cardpwd","status","ispw","func","ye","money",
		"value1","value2","value3","memo",
		"str1","str2","str3","str4","num1","num2","num3", "pwdje"};
	
    public String cardno;				// 卡号
    public String cardname;				// 持卡人
    public String cardpwd;				// 卡密码
    									// HZJB 券类型
    									// SZXW 卡类型
    public String status;				// 卡状态
    public char ispw;					// 是否输入密码
    public String func;					// 其他功能
    									// [0]-状态
    									// [1-2]-卡类型
    									// [3]-是否回收
    									// [4]-密码校验次数,超过该次数，进行卡冻结
    public double ye;					// 余额
    public double money;				// 面值
    public double value1;				// 备用	
    									// FJK:B券余额
    public double value2;				// 备用
    									// BHLS:卡批次
    									// FJK:F券余额
    public double value3;				// 如果可回收，表示卡的工本费
    public String memo;					// 备注
    									// HZJB:卡序号
    									// SZXW:定金单消费后余额生成的新的定金单号
    									// 券信息 券种,券名称，余额,汇率
    public String str1;					// 查询用有效期
    									// SZXW:付款方式，卡号，柜组
    public String str2;					// 柜组限制
    public String str3;					// 说明信息
    public String str4;					// 备用
    public double num1;				// 备用
    public double num2;				// 备用   杭州汇德隆政府券的优惠金额
    public double num3;				// 备用
    public double pwdje;				// 零钞免密限额
    
    
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
