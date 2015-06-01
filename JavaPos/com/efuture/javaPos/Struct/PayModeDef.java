package com.efuture.javaPos.Struct;

import java.io.Serializable;

// 付款方式定义
public class PayModeDef  implements Cloneable,Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7010787425589305268L;

	static public String[] ref = {"code","sjcode","level","name","type","iszl","isyy",
		"ismj","isbank","minval","maxval","hl","zlhl","sswrfs","sswrjd",
		"str1","str2","num1","num2"};
    
	public String code; 			// 付款方式代码,主键
	public String sjcode; 			// 上级编码
	public int level; 				// 编码级次
	public String name; 			// 付款方式名称
	public char type; 				// 付款类型,1-人民币类,2-支票类,3-信用卡类,4-面值卡类,5-礼券类,6-赊销类,7-其它
	public char iszl; 				// 是否找零（Y/N)
	public char isyy; 				// 是否溢余（Y/N）
	public char ismj; 				// 是否末级（Y/N）
	public char isbank; 			// 是否金卡工程(Y/N)
	public double minval; 			// 最低付款金额
	public double maxval; 			// 最高付款金额
    public double hl; 				// 付款汇率
    public double zlhl;				// 找零汇率
    public char sswrfs;				// 精度方式(Y:截断 N:四舍五入)0-精确到分、1-四舍五入到角、2-截断到角、3-四舍五入到元、4-截断到元、5-进位到角、6-进位到元
    public double sswrjd; 			// 四舍五入精度
    								// ZMJC 最小找零单位
    public String str1;				// 对应后台表MEMO字段，存放付款对象类名，用于创建自定义付款对象
    public String str2;				// 备用
    								// ZMJC 是否补找零(Y/N)
    public double num1;				// 备用
    									// ZMJC 最大找零金额
    public double num2;				// 付款方式排序号
    
	public Object clone()
	{
		try 
		{
			return super.clone();
		} catch (CloneNotSupportedException e) 
		{
			e.printStackTrace();
			return this;
		}
	}
}
