/*
 * 创建日期 2004-6-20
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.gui.test;

import com.royalstone.pos.gui.DialogConfirm;

/**
 * @author liangxinbiao
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class DutyEndConfirmTest {

	public static void main(String[] args) {
		DialogConfirm dutyEndConfirm=new DialogConfirm();
		dutyEndConfirm.setMessage( "班结后不能再收银。确实要作班结吗？" );
		dutyEndConfirm.show();
		
		System.out.println(dutyEndConfirm.isConfirm());
	}
}
