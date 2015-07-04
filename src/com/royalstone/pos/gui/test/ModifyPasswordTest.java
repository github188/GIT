/*
 * 创建日期 2004-5-31
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.gui.test;

import com.royalstone.pos.gui.ModifyPassword;

/**
 * @author liangxinbiao
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class ModifyPasswordTest {

	public static void main(String[] args) {
		ModifyPassword modifyPassword=new ModifyPassword();
		modifyPassword.show();
		
		if(modifyPassword.isConfirm()){
			System.out.println(modifyPassword.getOldPassword());
			System.out.println(modifyPassword.getNewPassword());
			System.out.println(modifyPassword.getConfirmPassword());
		}else{
			System.out.println("Cancel");
		}
	}
}
