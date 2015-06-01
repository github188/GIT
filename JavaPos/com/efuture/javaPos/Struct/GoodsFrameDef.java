package com.efuture.javaPos.Struct;

import java.io.Serializable;

// 商品例外资料定义
public class GoodsFrameDef implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static String[] ref={"barcode","gz","mkt"
								,"lsj","hyj","hyjzkfd","pfj","pfjzkfd","xxtax","xjjg","jgjd","minplsl"
								,"issqkzk","isvipzk","iszs","maxzkl","ischgjg","maxzke","memo"
								,"str1","str2","str3","num1","num2","num3"};

	public static String[] refLocal={"barcode","gz","mkt"
	                     			,"lsj","hyj","hyjzkfd","pfj","pfjzkfd","xxtax","xjjg","jgjd","minplsl"
	                    			,"issqkzk","isvipzk","iszs","maxzkl","ischgjg","maxzke","memo"
	                    			,"str1","str2","str3","num1","num2","num3"};
	
	public static String[] key = {"barcode","gz"};
	
	public String barcode;			// 商品条码,主键
	public String gz;				// 商品柜组,主键	
	public String mkt;				// 所属门店
	public double lsj;				// 零售价					
	public double hyj;				// 会员价
	public double hyjzkfd;			// 会员价折扣分担
	public double pfj;				// 批发价		
	public double pfjzkfd;			// 批发价折扣分担
	public double xxtax;			// 销项税
	public double xjjg;				// 削价价格，=0无削价	
	public double jgjd;				// 价格精度
	public int minplsl;				// 最小销售数量		
	public char issqkzk;			// 是否允许授权折扣（Y/N）		
	public char isvipzk;			// 是否允许VIP折扣（Y/N）	
	public char iszs;				// T-商品停售/N-特卖码是否允许销售
	public double maxzkl;			// 最底折扣率
	public char ischgjg;			// 是否允许议价
	public double maxzke;			// 最底限价价格
	public String memo;				// 备注							
	public String str1;				// 备用字段
	public String str2;				// 备用字段
	public String str3;				// 备用字段
	public double num1;				// 备用字段						
	public double num2;				// 备用字段						
	public double num3;				// 备用字段
}
