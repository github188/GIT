package com.efuture.javaPos.Struct;

public class CmPopGiftsDef
{
	public static String[] ref = {"giftseqno","dqid","ruleid","ladderid","giftgroup","giftgrouprow",
		"gifttype","giftname","giftsl","giftmaxsl",
		"codemode","codeid","codegz","codeuid","joinmode","popmode","poplsj","pophyj","poppfj","poplsjfdff","poplsjzkfd",
		"pophyjfdff","pophyjzkfd","poppfjfdff","poppfjzkfd","popmemo",
		"str1","str2","str3","num1","num2","num3"};
	
	public static String[] refLocal = {"giftseqno","dqid","ruleid","ladderid","giftgroup","giftgrouprow",
		"gifttype","giftname","giftsl","giftmaxsl",
		"codemode","codeid","codegz","codeuid","joinmode","popmode","poplsj","pophyj","poppfj","poplsjfdff","poplsjzkfd",
		"pophyjfdff","pophyjzkfd","poppfjfdff","poppfjzkfd","popmemo",
		"str1","str2","str3","num1","num2","num3"};
	
	public static String[] key = {"giftseqno"};
	
	public long giftseqno;				//赠品序号,主键
	public String dqid;             	//活动档期,唯一键
	public String ruleid;           	//活动规则,唯一键
	public String ladderid;           	//规则阶梯(%-所有阶梯),唯一键
	public int giftgroup;       		//赠品分组(同一规则的多个赠品顺号分组,AND方式)(同一分组的赠品为任选模式,OR方式),唯一键
	public int giftgrouprow;       		//赠品组内行号,唯一键
	public char gifttype;				//赠品类型
										//1-已销售的非条件商品,进行打折优惠(换购)
										//2-赠送正常商品,按指定价销售
										//3-赠送普通礼品,打印到赠品联
										//4-打印的电子券,款机进行打印
										//5-已销售的条件商品,进行打折优惠
	public String giftname;            	//赠品描述
	public double giftsl;				//赠品数量
	public double giftmaxsl;			//封顶数量
	public char codemode;           	//商品参与方式(0-全场/1-按单品/2-按柜组/3-按品牌/4-按品类/5-按柜组+品牌/6-按柜组+品类/7-按品牌+品类/8-按柜+品+类/9-按条码(子商品)/A-按属性1/B-按属性2/C-按属性3/D-按属性4/E-按属性5/F-按属性6/G-按属性7/H-按属性8)
	public String codeid;           	//参与方式对应的编码(%表示全部)
	public String codegz;           	//单品对应的柜组(%表示全部)
	public String codeuid;          	//单品对应的单位码(%表示全部)
	public char joinmode;           	//参加方式(Y-参与/N-例外不参加)
	public char popmode;            	//促销方式(0-直接指定价值/1-指定成交价格/2-指定减价比率/3-指定减价金额)
	public double poplsj;           	//促销零售价/促销零售价折扣率/赠品价值(折扣取值)
	public double pophyj;           	//促销会员价/促销会员价折扣率
	public double poppfj;           	//促销批发价/促销批发价折扣率/赠品价值范围
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
}
