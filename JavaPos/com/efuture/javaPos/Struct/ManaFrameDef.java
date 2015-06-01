package com.efuture.javaPos.Struct;

// 管理柜组类
public class ManaFrameDef
{
	public static String[] ref = {"gz","sjcode","levelclass","name","mjflag","iscs","isxh","isqt1","isqt2","isqt3",
	                              "attr01","attr02","attr03","attr04","attr05","attr06","attr07","attr08","memo"};
	
	public String gz; 				// 柜组代码(管理架构的最末级),主键
	public String sjcode;			// 上级代码
	public int levelclass;			// 代码级次
	public String name;				// 柜组名称
	public char mjflag;				// 末级标志
	public String iscs;				// 是否超市柜
	public char isxh;				// 是否允许销红
	public char isqt1;				// 其他
	public char isqt2;				// 其他
	public char isqt3;				// 其他
	public String attr01;			// 属性1
	public String attr02;			// 属性2
	public String attr03;			// 属性3
	public String attr04;			// 属性4
	public String attr05;			// 属性5
	public String attr06;			// 属性6
	public String attr07;			// 属性7
	public String attr08;			// 属性8
	public String memo;				// 备注
}
