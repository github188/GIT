package com.efuture.javaPos.Struct;

// 收银班次类
public class PosTimeDef
{
	static public String[] ref = {"code","name","btime","etime"};
	
	public char code;				// 班次编码，主键						
	public String name;				// 班次名称						
	public String btime;			// 开始时间,00:00				
	public String etime;			// 结束时间,00:00		
}
