package com.efuture.javaPos.Struct;

import java.io.Serializable;

// 商品基础资料定义
public class GoodsStockDef implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static String[] ref={"goodsbigcode","goodsno","goodsbarcode","goodsyear","goodsquarter","goodscolor","goodscm","goodskc"};

	public String goodsbigcode;			// 商品大码					
	public String goodsno;				// 厂商货号			
	public String goodsbarcode;		    // 商品编码
	public String goodsyear;			// 年份
	public String goodsquarter;			// 季节			
	public String goodscolor;			// 颜色					
	public String goodscm;			    // 尺码					
	public String goodskc;			    // 库存						
		
}
