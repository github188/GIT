/*
 * 创建日期 2004-6-18
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.gui.test;

import com.royalstone.pos.gui.OilInput;

/**
 * @author liangxinbiao
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class OilInputTest {

	public static void main(String[] args) {
		OilInput oilInput=new OilInput(System.out);
		oilInput.show();
		//oilInput.setStep(0);
		oilInput.setStep(0);
		oilInput.setAmount("124");
		
	}
}
