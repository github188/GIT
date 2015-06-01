package com.efuture.javaPos.Struct;


// 会员卡VIP折扣率定义
public class CustomerVipZklDef
{
	public static String[] ref = {"seqno","ruleid","custtype","codemode","codeid","codegz","codeuid",
		"ksrq","jsrq","kssj","jssj","weeklist","iszsz","zkmode","zkl",
		"zklareadn","zklareaup","inareazkl","dnareazkl","upareazkl","maxslmode","maxsl","memo"};

	public static String[] key = {"seqno"};
	
	public long seqno;				// 序号
	public String ruleid;			// 策略ID
	public String custtype;			// 会员类别(ALL-所有会员和非会员/HALL-所有会员/NALL-所有非会员/#xxx-会员自定义分组/其他-会员类别)
	public char codemode;         	// 商品参与方式(0-全场/1-按单品/2-按柜组/3-按品牌/4-按品类/5-按柜组+品牌/6-按柜组+品类/7-按品牌+品类/8-按柜+品+类/9-按条码(子商品)/A-按属性1/B-按属性2/C-按属性3/D-按属性4/E-按属性5/F-按属性6/G-按属性7/H-按属性8)
	public String codeid;          	// 参与方式对应的编码(%表示全部)
	public String codegz;          	// 单品对应的柜组(%表示全部)
	public String codeuid;         	// 单品对应的单位码(%表示全部)
	public String ksrq;            	// 开始日期
	public String jsrq;            	// 结束日期
	public String kssj;            	// 开始时间
	public String jssj;            	// 结束时间
	public String weeklist;        	// 星期列表(1,2,3,4,5,6,7)
	public char iszsz;				// 折上折标志(Y-折上折/A-折上折,并有最大折扣率限制(ZKL)/N-不折上折)
	public char zkmode;				// 折扣形式(1-指定折扣价格/2-指定打折比率/3-指定减价金额)
	public double zkl;				// 非折上折折扣率
	public double zklareadn;		// 折扣区间下						
	public double zklareaup;		// 折扣区间上				
	public double inareazkl;		// 区间内折上折率
	public double dnareazkl;		// 区间下折上折率						
	public double upareazkl;		// 区间上折上折率
	public char maxslmode;			// 限量模式(0-不限量/1-单笔交易限量/2-促销期总限量/3-促销期日限量/4-会员促销期限量/5-会员日限量/9-检查单笔交易限量同时检查网络的限量)
	public double maxsl;			// 会员限量(0-不限量)
	public String memo;				// 备用
}
