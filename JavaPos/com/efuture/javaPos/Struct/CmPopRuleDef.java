package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class CmPopRuleDef implements Cloneable,Serializable
{
	private static final long serialVersionUID = 1L;

	public static String[] ref = {"dqid","ruleid","rulename","ruletype",
		"summode","condtype","condmode","popmode","popzsz","maxpopje","isonegroup","payexcp","memo",
		"pri","rulegroup","payexcpnopop","spare","reserve"};
	
	public static String[] key = {"dqid","ruleid"};
	
	public String dqid;					//档期ID
	public String ruleid;				//规则ID
	public String rulename;				//规则描述
	public String ruletype;       		//促销类型(类型编码自定义用于区分不同的促销类型)(同档期内同一促销类型以最后的一个活动规则为准)
	public char summode;        		//参与规则的商品的累计方式(0-不累计/1-同规则累计/2-同规则同柜累计/3-同规则单品累计/4-同规则同品类累计/5-同规则同品牌累计)
	public char condtype;       		//消费条件类型(0-够满条件只参与一个阶梯/1-每满条件循环参与阶梯)
	public char condmode;       		//消费条件方式(0-无消费条件/1-达到数量条件/2-达到金额条件/3-同时达到数量金额条件/4-单品总价按照比例打折不参与阶梯)
	public char popmode;        		//促销结果形式(0-赠送形式/1-指定的折扣金额是总促销价格/2-指定的折扣金额是打折比率/3-指定的折扣金额总减价金额/4-指定的折扣金额是单个商品促销价格/5-指定的折扣金额是单个商品的打折比率/6-指定的折扣金额是单个商品的减价金额/7-取商品范围设置的折扣形式/N-不促销/C-后单无效取前单)
	public char popzsz;					//促销折上折
	public double maxpopje;     		//促销封顶金额(0-不限制封顶)
	public char isonegroup;				//参与该规则的所有商品范围都视为一个组,商品范围的组视为组内分组(Y/N)
	public String payexcp;				//除外付款(用|分隔)
	public String memo;					//备用([0]=Y-扫码后立即计算该商品累计方式的促销)
	public int pri;						//优先级
	public int rulegroup;				//规则组号(同组规则进行任选,不同组规则为必选)
	public char payexcpnopop;			//存在除外付款不促销(Y/N)
	public String spare;				//备用
	public String reserve;				//备用
	
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
