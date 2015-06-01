package com.efuture.javaPos.Struct;

/**
 * @author yinl
 * @create 2010-12-3 上午10:38:05
 * @descri 文件说明
 */

public class GlobalParaDesc
{
	public String group;
	public String valdesc;
	public String valdef;
	public String[][] valdata;
	
	public GlobalParaDesc(String group,String desc,String defval,String[][] val)
	{
		this.group = group;
		this.valdesc = desc;
		this.valdef = defval;
		this.valdata = val;
	}
}
