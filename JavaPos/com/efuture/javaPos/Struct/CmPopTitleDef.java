package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class CmPopTitleDef implements Cloneable,Serializable
{
	private static final long serialVersionUID = 1L;

	public static String[] ref = {"dqid","name","ksrq","jsrq","ruleselmode","pri","dqtype","memo"};
	
	public static String[] key = {"dqid"};
	
	public String dqid;					//档期ID
	public String name;		          	//档期主题描述
	public String ksrq;					//活动档期开始日期
	public String jsrq;					//活动档期结束日期
	public char ruleselmode;			//同活动档期内多个规则的参与方式(0-手工选择一个规则/1-只参与最后一个规则/2-同时参与多个规则/3-按分组规则选择)
	public int pri;               		//活动优先级,多个活动时按优先级排序
	public String dqtype;				//档期类型(类型编码自定义用于区分不同的促销分类)(不同档期的同一档期类型以最后的一个活动档期为准)
	public String memo;					//备用
	
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
