package com.efuture.javaPos.Struct;

import java.util.Vector;

public class SaleMemoInfo
{
	public static String COMBOTYPE = "ComBox";
	public static String TEXTTYPE = "TextBox";
	
	public static String CMDGET = "cmd";
	public static String CFGGET = "cfg";
	
	public class ContentDef
	{
		public String type; //Combox内容类型 txt - 配置出来 | cmd - 通过命令获得
		public String code;
		public String name;
		public Vector vccontent = new Vector(); // 存放KeyValueDef
	}
	
	public String desc;				// 描述
	public String value;			// 插入字段
	public String type;				// 控件类型(Textbox,Combox)
	public char allowNull;          // 是否允许为空
	public int maxLength;		// 最大输入长度（当控件类型是Textbox时才存在）
	public ContentDef content = new ContentDef();			// 内容（当控件类型是Combox时才存在）
	public Vector associate = new Vector();		// 关联 存放(String)
	public String memo1;			// 预留1
	public String memo2;			// 预留2
	public String memo3;			// 预留3
	public String memo4;			// 预留4
	public String memo5;			// 预留5
	public String memo6;			// 预留6
	
	public Vector vcresult;     // 查询结果(命令模式才使用)
	public int curselindex = -1;           // 当前选择项 (Combox使用)
	public String curcontent = "";          // 当前内容 (Text使用)
}
