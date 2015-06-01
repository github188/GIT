package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class JfSaleRuleDef implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static String[] ref = {"jf","money","str1","str2","char1","char2","num1","num2","num3","num4"};

	public int	   jf;					//积分
	public double money;				//兑换金额
	public String  str1;				//备用	换购规则单号
	public String  str2;				//备用
										//HZJB 
										//
	public char   char1;				//备用	是否限量y-限量n-不限量
	public char   char2;				//备用	换购模式:如果Y表示积分换多少钱,否则为积分加多少钱
	public int    num1;				//备用
									
	public int    num2;				//备用
									//HZJB: 选择档期积分还是常规积分（1-常规、2-档期）
									
	public double num3;				//备用
									//HZJB: 积分
									//CZ:折扣分摊
	public double num4;				//备用
									//CZ:值多少钱
	
}
