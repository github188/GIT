/*
 * 创建日期 2006-1-18
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.royalstone.pos.data;

import java.util.*;
/**
 *  商品合计打印小票
 *  
 */
public class printGoodsVgno {
	
	public printGoodsVgno(){
		lst = new Vector();
		}
	
	
	public boolean add(String goodsVgno){
		for (int i = 0; i < lst.size(); i ++){
			if (lst.get(i).equals(goodsVgno)){
				return false;
				}
			}
		lst.add(goodsVgno);
		return true;
		}
	
	private Vector lst;

}
