package com.efuture.javaPos.Struct;

import java.util.Vector;

//计算促销单时需要的结构
public class CalcRulePopDef 
{
	public String code;						//商品编码
											//PaymentJfNEW 商品收积分规则
	public String gz;						//商品柜组
	public String uid;						//商品单位
											//BCRM：B券收券规则
	public String rulecode;					//参与规则
	public String catid;					//商品品类			
											//BCRM：B券满收条件
	public String ppcode;					//商品品牌
	public GoodsPopDef popDef;				//促销单
	public Vector row_set;					//此促销单对应的商品行号
	public double popje;					//参与计算该促销的合计金额
	public double mult_Amount;				//此类促销的总金额或数量
	
	public String str1;						//BCRM: A券收券规则
	public String str2;						//BCRM：A满收条件
	
	public String str3;						//SZXW: F券收券规则
	public String str4;						//SZXW：F满收条件
	
	
	public String jfrule;					//积分规则
	public double mk;						//门槛
	public double limit;					//限额
	public double zxjf;						//折现积分
	public double zxje;						//折现金额
	public double zxbl;						//折现比例
	public double hjje;						//合计金额
	public double yftjf;						//已收积分
	
}
