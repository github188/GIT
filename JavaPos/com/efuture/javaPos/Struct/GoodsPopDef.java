package com.efuture.javaPos.Struct;

import java.io.Serializable;

// 商品促销定义
public class GoodsPopDef implements Cloneable,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2283172094633014792L;

	public static String[] ref={"seqno","djbh","type","rule","mode","code","gz","uid","catid","ppcode",
							"sl","yhspace","ksrq","jsrq","kssj","jssj","poplsj","pophyj","poppfj",
							"poplsjzkl","pophyjzkl","poppfjzkl","poplsjzkfd","pophyjzkfd","poppfjzkfd",
							"memo","str1","str2","str3","str4","str5","str6","str7","str8","str9","str10","num1","num2","num3","num4","num5","num6","num7","num8","num9","num10"};
	
	public static String[] refLocal={"seqno","djbh","type","rule","mode","code","gz","uid","catid","ppcode",
		"sl","yhspace","ksrq","jsrq","kssj","jssj","poplsj","pophyj","poppfj",
		"poplsjzkl","pophyjzkl","poppfjzkl","poplsjzkfd","pophyjzkfd","poppfjzkfd",
		"memo","str1","str2","str3","str4","str5","str6","str7","str8","str9","str10","num1","num2","num3","num4","num5","num6","num7","num8","num9","num10"};
	
	public static String[] key = {"seqno"};
	
	public long seqno;				// 序号,主键				
	public String djbh;				// 优惠单号						
									// BCRM:规则促销的促销活动单号
	public char type;				// 优惠类型,1-单品/2-柜组/3-品类/4-柜组品牌/5-品类品牌/6-品牌/7-电子秤
	public String rule;				// 优惠规则,1-分期促销/RJG-规则促销结果/RMJ-满减规则促销/RMS-满增编码商品规则促销/RMF-满增非编码商品规则促销/NMJ-新满减/DMJ-新满减
									// BHLS:规则促销的促销属性码
	public String mode;				// 优惠方式,1-分期促销/ME-满商品金额/MS-满商品数量/MX-满小票金额
	public String code;				// 商品编码						
	public String gz;				// 柜组							
									// BHLS:规则促销的方式(1-满减/2-减现)		
	public String uid;				// 多单位代码，'00'-基础信息,'AL'-所有单位都优惠,'xx'-指定单位优惠 
	public String catid;			// 商品品类						
									// BHLS:规则促销的除开券付款算满条件金额标志(Y/N)					
	public String ppcode;			// 商品品牌						
									// BHLS:规则促销的跨柜组计算满条件标志(Y/N/1)
	public int sl;					// 销售数量
									// 满减限额
	public int yhspace;			// 电子秤商品的优惠的时间间隔(秒) 
									// BCRM:组合码（1010,打折，满减，返券，返礼）
	public String ksrq;				// 开始日期,YYYY/MM/DD
	public String jsrq;				// 结束日期,YYYY/MM/DD
									// BCRM:商品会员价促销单号,商品促销价，限量数量 ，已享受数量，积分方式（0:正常积分 ,1:不积分 2:特价积分） 
	public String kssj;				// 开始时间,00:00
									// BCRM：满减规则描述
	public String jssj;				// 结束时间,00:00			
	public double poplsj;			// 优惠零售价					
									// BHLS:规则促销的满的条件金额
	public double pophyj;			// 优惠会员价					
									// BHLS:规则促销的满减的金额
	public double poppfj;			// 优惠批发价
									// BCRM:档期
	public double poplsjzkl;		// 优惠零售价折扣率				
									// BHLS:规则促销的减现比例				
	public double pophyjzkl;		// 优惠会员价折扣率				
									// BHLS:促销折上折标志
									// BCRM:是否享用VIP折扣 1 为享用 0 为不享用
	public double poppfjzkl;		// 优惠批发价折扣率
									// BCRM:忽略其他积分优惠（1是，0否）  
	public double poplsjzkfd;		// 优惠零售价折扣分担	
									// BCRM:满减折扣分担	
	public double pophyjzkfd;		// 优惠会员价折扣分担
									// BCRM:规则促销折扣控制
	public double poppfjzkfd;		// 优惠批发价折扣分担
									// BCRM:积分倍率
	public String memo;				// 备用							
									// BHLS:规则促销的促销活动单号
									// BCRM:满减规则,返券规则,返礼规则(逗号分格)
									// JNYZ:/* 满减规则,返券规则,返礼规则,抽奖规则,价随量变规则,价随量变折扣分担  */
	public String str1;				// 备用							
									// BCRM:ALL:会员与非会员/@1:全部会员/@2:非会员/首字符为#:卡分组/其他:卡类型
	public String str2;				// 备用
									// BCRM:备注
	public String str3;				// 备用
	public String str4;				// 备用
									// ZSBH:分组码
	public String str5;				// 备用
	                                // KSSS:满抵标志 A：满减/B:满抵
	public String str6;				// CMLS 折扣单号
	public String str7;				// 备用
	public String str8;				// 备用
	public String str9;				// 备用
	public String str10;			// 备用
	
	public double num1;			// 备用
									// A券
									// BCRM:折扣门槛
	public double num2;			// 备用
									// BCRM：备注
	public double num3;			// 备用
									// BCRM：备注
	public double num4;			// 备用
									// BCRM：备注
	public double num5;			// 备用
									// BCRM：备注
	public double num6;			// CMLS 起点折扣率
	                                
	public double num7;			// CMLS 折上折折扣率
									
	public double num8;			// CMLS 折扣分担
									
	public double num9;			// 备用
	public double num10;			// 备用
	
	public Object clone()
	{
		try 
		{
			return super.clone();
		} catch (CloneNotSupportedException e) 
		{
			e.printStackTrace();
			return this;
		}
	}
}
