/*
 * 创建日期 2004-5-25
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.cyber;

import java.sql.Connection;

import org.jdom.Element;

/**
 * @author liangxinbiao
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public interface CyberCmd {
	
	public Element excute(Element xinput);
	public void setDBConnection( Connection con);

}
