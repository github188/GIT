package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class CmRuleMFListDef implements Serializable
{
	private static final long serialVersionUID = 1L; 
	
	public static String[] ref = {"seqno","mfruleid","mfruletype","mfmode","mfcode",
	                              "mfattr01","mfattr02","mfattr03","mfattr04",
	                              "mfattr05","mfattr06","mfattr07","mfattr08",
	                              "joinmode"};
	
	public static String[] key = {"seqno"};
	
	public long   seqno;				//序号
	public String mfruleid;				//营销规则ID
	public String mfruletype;			//营销类型(CUSTZKL-会员折扣门店范围/CMPOP-促销门店范围/PAYRULE-收款规则门店范围/JFRULE-积分规则门店范围)
	public char   mfmode;				//范围模式(%-按管理架构属性范围/0-门店号/x-按管理架构级次代码)
	public String mfcode;				//管理架构代码
	public String mfattr01;				//管理属性1
	public String mfattr02;				//管理属性2
	public String mfattr03;				//管理属性3
	public String mfattr04;				//管理属性4
	public String mfattr05;				//管理属性5
	public String mfattr06;				//管理属性6
	public String mfattr07;				//管理属性7
	public String mfattr08;				//管理属性8
	public String joinmode;				//参与方式,Y:参与/N:不参与
}
