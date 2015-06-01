package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class CmPopRuleLadderDef implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static String[] ref = {"dqid","ruleid","ladderid","laddername","ladderpri",
		"condsl","condje","levelsl","levelje","levelminus","popje","maxfb",
		"levelzk","memo","spare","reserve"};
	
	public static String[] key = {"dqid","ruleid","ladderid"};
	
	public String dqid;				//档期ID
	public String ruleid;			//规则ID
	public String ladderid;			//阶梯ID
	public String laddername;		//阶梯描述
	public int ladderpri;			//同规则阶梯优先级,优先级最大的阶梯先计算
	public double condsl;			//数量条件
	public double condje;			//金额条件
	public double levelsl;			//数量门槛
	public double levelje;			//金额门槛
	public char levelminus;			//除外门槛计算满足条件(Y/N)
	public double popje;			//促销形式对应的促销金额
	public int maxfb;				//促销翻倍倍数(0-不限制倍数)
	public double levelzk;			//折扣门槛(打折比例已低于该门槛不促销)
	public String memo;				//备用([0]=CmPopRuleGoodsDef.codemode商品数量分组方式/[1]=E-等于数量/L-小于数量/G-大于数量)
	public String spare;			//备用
	public String reserve;			//备用	
}
