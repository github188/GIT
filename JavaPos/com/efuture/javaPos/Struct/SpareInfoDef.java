package com.efuture.javaPos.Struct;

import java.io.Serializable;
import java.util.Vector;

// 备用信息类
public class SpareInfoDef implements Cloneable,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public String str1;			// BCRM:0000
	public String str2;
	public String str3;			// 对应的付款序号，积分换购的规则号 
	public char  char1;		// BCRM:会员促销价的商品
	public char  char2;		// Bcrm:积分换购标志
	public char  char3;		// BCRM:可用积分消费的商品
	public double num1;
	public double num2;
	public double num3;		// BHLS:赠送折扣分担
	public Vector payrule;		// 商品对应的收款规则
	public Vector payft;		// 商品对应的付款分摊 String[] = {"付款唯一行号","付款代码","付款名称","分摊金额" }
	public Vector popzk;		// BSTD:记录商品参与的促销折扣明细 String[] = {"促销序号"，"促销金额","促销备注"}
	public String memo;			// 备用字段 XJLY会员精度
	
	public String memo1;		// BCRM，积分规则 种类1|门槛1|上线1|积分1|抵现金1;种类2|门槛2|上线2|积分2|抵现金2;

	public String memo2;
	public String memo3;
	public String memo4;
	public String memo5;
	
	//bjcx 价随量变
	public String pmbillno;/*促销单号*/
	public String addrule ;/*累计规则 'YYYYY'*/
	public String Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
	public String pmrule;/*价随量变规则*/
	public String etzkmode2;/*1-阶梯折扣 2-统一打折*/
	public String seq; /*规则序号*/
	public String zkfd;
	public String bz;/*阶梯折扣时  Y/N是否循环   统一折扣时  Y/N是否启用上限数量*/
	public double maxnum; /*上限数量*/
	public double maxzke; /*上限折扣额*/
	
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
