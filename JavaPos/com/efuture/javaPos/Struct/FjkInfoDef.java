package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class FjkInfoDef implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String[] ref = {"cardno","cardname","status","startdate","enddate","yeA",
		"yeB","yeF","str1","str2","str3","value1","value2","value3"};
	
	public String cardno;				// 卡号			
										// HZJB:规则代码
    public String cardname;				// 持卡人		
    									// HZJB:规则描述
    public String status;				// 卡状态
    public String startdate;			// 开始日期
    public String enddate;				// 结束日期
    public double yeA;					// 余额A
    public double yeB;					// 余额B
    public double yeF;					// 余额F
    public String str1;					// 备用
    public String str2;					// 备用
    public String str3;					// 备用
    public double value1;				// 备用
    public double value2;				// 备用
    public double value3;				// 备用
}
