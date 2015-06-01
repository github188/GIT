package com.efuture.javaPos.Struct;

import java.io.Serializable;
import java.util.Vector;


// 付款方式收款规则
public class PayRuleDef implements Serializable,Cloneable
{
	private static final long serialVersionUID = -8101595748713759435L;

	public static String[] ref = {"seqno","ruleid","paycode","payflag","codemode","codeid","codegz","codeuid","joinmode",
					"ksrq","jsrq","kssj","jssj","weeklist","rulemode","condje","payje",
					"maxpayje","levelmode","levelcond","levelminus","memo"};
	
	public static String[] key = {"seqno"};
	
	public long seqno;					//序号
	public String ruleid;				//规则ID
	public String paycode;				//付款代码
	public String payflag;				//同付款代码下不同的付款标记
	public char codemode;           	//商品参与方式(0-全场/1-按单品/2-按柜组/3-按品牌/4-按品类/5-按柜组+品牌/6-按柜组+品类/7-按品牌+品类/8-按柜+品+类/9-按条码(子商品)/A-按属性1/B-按属性2/C-按属性3/D-按属性4/E-按属性5/F-按属性6/G-按属性7/H-按属性8)
	public String codeid;           	//参与方式对应的编码(%表示全部)
	public String codegz;           	//单品对应的柜组(%表示全部)
	public String codeuid;          	//单品对应的单位码(%表示全部)
	public char joinmode;           	//参加方式(Y-参与/N-例外不参加)
	public String ksrq;             	//开始日期
	public String jsrq;             	//结束日期
	public String kssj;             	//开始时间
	public String jssj;             	//结束时间
	public String weeklist;         	//星期列表(1,2,3,4,5,6,7)
	public char rulemode;           	//收款方式(0-满xx收yy/1-按比例收/2-按比例配现收/3-xx现搭配yy)
	public double condje;           	//满收条件
	public double payje;           		//收款金额/收款比例
	public double maxpayje;				//最大收款金额(0-不限制)
	public char levelmode;				//门槛方式
	public double levelcond;			//门槛条件
	public char levelminus;				//门槛除外(Y/N)
	public String memo;					//备用
	
	// 计算用字段
	public Vector goodslist;			//参与该规则的商品清单
	public double ruleje;				//规则允许金额
	public double gdleje;				//商品允许金额
	
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
