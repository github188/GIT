package com.efuture.javaPos.Struct;

import java.io.Serializable;
import java.util.Vector;

public class CmPopGoodsDef implements Cloneable,Serializable
{
	private static final long serialVersionUID = 1L;

	public static String[] ref = {"cmpopseqno","dqid","ruleid","goodsgroup","goodsgrouprow","ruletype","codemode","codeid",
		"codegz","codeuid","joinmode","ksrq","jsrq","kssj","jssj","weeklist","custlist","condmode","condsl","condje","popmaxmode","popmax",
		"popmode","poplsj","pophyj","poppfj","poplsjfdff","poplsjzkfd","pophyjfdff","pophyjzkfd","poppfjfdff","poppfjzkfd","popmemo",
		"str1","str2","str3","num1","num2","num3",
		"strdqinfo","strruleinfo","strruleladder"};
	
	public static String[] refLocal = {"cmpopseqno","dqid","ruleid","goodsgroup","goodsgrouprow","ruletype","codemode","codeid",
		"codegz","codeuid","joinmode","ksrq","jsrq","kssj","jssj","weeklist","custlist","condmode","condsl","condje","popmaxmode","popmax",
		"popmode","poplsj","pophyj","poppfj","poplsjfdff","poplsjzkfd","pophyjfdff","pophyjzkfd","poppfjfdff","poppfjzkfd","popmemo",
		"str1","str2","str3","num1","num2","num3"};
	
	public static String[] key = {"cmpopseqno"};
	
	public long cmpopseqno;				//促销序号
	public String dqid;             	//活动档期
	public String ruleid;           	//活动规则
	public int goodsgroup;       		//商品分组号(同一规则的多个商品范围顺号分组)(同一分组的商品范围的条件要同时满足,实现A+B+C的组合)
	public int goodsgrouprow;       	//商品组内行号(0-分组内只有本商品/X-分组内有其他商品)
	public String ruletype;       		//促销类型(类型编码自定义用于区分不同的促销类型)(同档期内同一促销类型以最后的一个活动规则为准)
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
	public String custlist;				//会员类别列表(FULL-所有人/HALL-所有会员/NALL-所有非会员/#xxx-会员分组/xxxx-会员类)
	public char condmode;           	//同商品分组内商品达到条件的方式(0-无达到条件/1-达到数量条件/2-达到金额条件/3-同时达到数量金额条件)
	public double condsl;           	//同商品分组内商品要达到的数量条件(0-无条件)
	public double condje;           	//同商品分组内商品要达到的金额条件(0-无条件)
	public char popmaxmode;				//限量模式(0-不限量/1-单笔交易限量/2-促销期总限量/3-促销期日限量/4-会员促销期限量/5-会员日限量/9-检查单笔交易限量同时检查网络的限量)
	public double popmax;				//促销限量(0-不限量)
	public char popmode;            	//促销方式(1-指定促销价格/2-指定减价比率/3-指定减价金额/N-不促销/C-后单无效取前单)
	public double poplsj;           	//促销零售价/促销零售价折扣率
	public double pophyj;           	//促销会员价/促销会员价折扣率
	public double poppfj;           	//促销批发价/促销批发价折扣率
	public char poplsjfdff;         	//零售价分担方法(填入小票商品明细)
	public double poplsjzkfd;       	//零售价折扣分担(填入小票商品明细)
	public char pophyjfdff;         	//会员价分担方法(填入小票商品明细)
	public double pophyjzkfd;       	//会员价折扣分担(填入小票商品明细)
	public char poppfjfdff;         	//批发价分担方法(填入小票商品明细)
	public double poppfjzkfd;       	//批发价折扣分担(填入小票商品明细)
	public String popmemo;				//促销的备注信息(填入小票商品明细)
	public String str1;             	//备用
	public String str2;             	//备用
	public String str3;             	//备用
	public double num1;             	//备用
	public double num2;             	//备用
	public double num3;             	//备用
	
	// 活动规则相关信息
	public CmPopTitleDef dqinfo;		//活动档期信息
	public CmPopRuleDef ruleinfo;		//档期规则信息
	public Vector ruleladder;       	//促销规则阶梯
	
	// 联网用字符串返回,然后转换到相应对象
	public String strdqinfo;			//活动档期信息
	public String strruleinfo;			//档期规则信息
	public String strruleladder;       	//促销规则阶梯
	
	// 用于计算时判断
	public boolean used;				//是否被使用
	
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
