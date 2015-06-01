package com.efuture.javaPos.Test;

import java.io.Serializable;

// 商品基础资料定义
public class GoodsDef extends DebugBase implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2780562448787384123L;

	/**
	 * 
	 */

	public static String[] ref={"barcode","gz","code","uid","type","name","catid","ppcode","unit","specinfo","bzhl"
			,"lsj","hyj","hyjzkfd","pfj","pfjzkfd","xxtax","xjjg","jgjd","minplsl","isuid","isbatch"
			,"issqkzk","isvipzk","iszs","isdzc","ispj","iszt","maxzkl","ischgjg","maxzke","fxm","managemode","memo"
			,"str1","str2","str3","str4","str5","num1","num2","num3","num4","num5"
			,"attr01","attr02","attr03","attr04","attr05","attr06","attr07","attr08"
			,"isxh","kcsl","popdjbh","poptype","poplsj","pophyj","poppfj","poplsjzkl","pophyjzkl"
			,"poppfjzkl","poplsjzkfd","pophyjzkfd","poppfjzkfd"};

	public static String[] refLocal={"barcode","gz","code","uid","type","name","catid","ppcode","unit","specinfo","bzhl"
			,"lsj","hyj","hyjzkfd","pfj","pfjzkfd","xxtax","xjjg","jgjd","minplsl","isuid","isbatch"
			,"issqkzk","isvipzk","iszs","isdzc","ispj","iszt","maxzkl","ischgjg","maxzke","fxm","managemode","memo"
			,"str1","str2","str3","str4","str5","num1","num2","num3","num4","num5"
			,"attr01","attr02","attr03","attr04","attr05","attr06","attr07","attr08"};
	
	public static String[] key = {"barcode","gz"};
	
	public String barcode;			// 商品条码,主键				
	public String gz;				// 商品柜组,主键,唯一键			
	public String code;				// 商品编码,唯一键
	public String uid;				// 多单位码,唯一键
	public char type;				// 编码类型(P-配件商品/Z-赠品/T-特卖/1-单品码/6-母商品/7-服务费/8-以旧换新/A-一品多原印/B-一品多包装单位/C-子商品)			
	public String name;				// 商品名称					
	public String catid;			// 商品品类					
	public String ppcode;			// 商品品牌						
	public String unit;				// 商品单位						
	public String specinfo;			// 商品规格						
									// BHLS:满减满赠规则促销的促销属性码				
	public double bzhl;				// 包装含量					
	public double lsj;				// 零售价					
	public double hyj;				// 会员价
	public double hyjzkfd;			// 会员价折扣分担
	public double pfj;				// 批发价		
	public double pfjzkfd;			// 批发价折扣分担
	public double xxtax;			// 销项税						
									// BHLS:如果rxxtax>=1则打印促销联		
	public double xjjg;				// 削价价格，=0无削价	
	public double jgjd;				// 价格精度
	public int minplsl;				// 最底批量数量,0-无批量		
	public char isuid;				// 多单位标志（Y/N）		
	public char isbatch;			// 是否输入批号标志（Y/N）		
	public char issqkzk;			// 是否允许授权折扣（Y/N）		
	public char isvipzk;			// 是否允许VIP折扣（Y/N）		
	public char iszs;				// T-商品停售/N-特卖码是否允许销售 					
	public char isdzc;				// 是否电子秤商品		
	public char ispj;				// 是否有配件
	public char iszt;				// 是否自提商品
	public double maxzkl;			// 最底折扣率
	public char ischgjg;			// 是否允许议价
	public double maxzke;			// 最底限价价格
	public String fxm;				// 分析码							
	public char managemode;			// 编码管理方法(0-单品管理/1-金额码管理) BSTD:后单压前单通过当前标志确认
	public String memo;				// 备注							
									// BHLS:电子券收券规则(收券活动单号,收券规则码,收券规则单据号,B收券模式(6-满收/3-按比例收),B满收条件金额,A收券模式(6-满收/3-按比例收),A满收条件金额)
	public String str1;				// 备用字段
									// HZJB:判断是否输入KEY键 Y:必须输入K值
									// BJYS:判断商品是否参与积分
	public String str2;				// 备用字段
	public String str3;				// 备用字段
	public String str4;				// 备用字段
									// CCZZ:收券规则
	public String str5;				// 备用字段
	public double num1;				// 备用字段						
									// BHLS:电子券收A券比例/A券满收金额
	public double num2;				// 备用字段						
									// BHLS:电子券收B券比例/B券满收金额
	public double num3;				// 备用字段
									// BHLS:电子券收F券比例/F券满收金额
	public double num4;				// 备用字段
									// BCRM:是否折上折 0-不折上折 1-折上折
	public double num5;				// 备用字段
	public String attr01;			// 商品属性1
	public String attr02;			// 商品属性2
	public String attr03;			// 商品属性3
	public String attr04;			// 商品属性4
	public String attr05;			// 商品属性5
	public String attr06;			// 商品属性6
	public String attr07;			// 商品属性7
	public String attr08;			// 商品属性8
	
	// 本地数据库无以下字段
	public char isxh = 'Y';			// 是否允许销红				
	public double kcsl = 0;			// 库存数量
	public String popdjbh;			// 优惠单号					
	public char poptype ;			// 优惠类型	1-单品/2-柜组/3-品类/4-柜组品牌/5-品类品牌/6-品牌/7-电子秤				
	public double poplsj;			// 优惠零售价
	public double pophyj;			// 优惠会员价				
	public double poppfj;			// 优惠批发价				
	public double poplsjzkl;		// 优惠零售价折扣率			
	public double pophyjzkl;		// 优惠会员价折扣率
	public double poppfjzkl;		// 优惠批发价折扣率
	public double poplsjzkfd;		// 优惠零售价折扣分担			
	public double pophyjzkfd;		// 优惠会员价折扣分担           
	public double poppfjzkfd;		// 优惠批发价折扣分担
	
	// 暂存数据
	public String inputbarcode;		// 输入时的原始编码
	
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
