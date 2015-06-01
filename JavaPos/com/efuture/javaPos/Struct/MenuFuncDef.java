package com.efuture.javaPos.Struct;

// 菜单功能定义
public class MenuFuncDef
{
	public static String[] ref = {"code","name","sjcode","level","mjflag","showflag",
		"enableflag","workflag","helptext"};
	
	public String code;				// 代码,主键						
	public String name;				// 描述						
	public String sjcode;			// 上级代码					
	public int level;				// 级次	
	public char mjflag;			// 末级标志
	public char showflag;			// 是否显示										
	public char enableflag;		// 有效标志					
	public char workflag;			// 是否记录工作日志			
	public String helptext;			// 帮助信息							
}
