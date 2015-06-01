package com.efuture.javaPos.Struct;

import java.io.Serializable;

// 备用信息字典类
public class MemoInfoDef implements Cloneable,Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static String[] ref = {"code","type","text","memo"};
	
	public String code;				// 代码,20位	
									// HZJB:会员卡号
	public String type;				// 类型,4位
									// HZJB:'HYFZ'
									// 'YDCZ'		--移动充值
	public String text;				// 描述,100位	
									// BJYS:消费金额下限;消费金额上限	
									// HZJB:会员分组号
									// 'YDCZ' 		--移动找零充值付款代码
	public String memo;				// 备注,200位
									// BJYS:积分分数;积分比率
									// 'YDCZ'		--1-在线充值商品/2-离线充值商品/3-找零充值商品
	
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
