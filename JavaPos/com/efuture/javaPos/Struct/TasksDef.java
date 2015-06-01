package com.efuture.javaPos.Struct;

// 收银机任务类
public class TasksDef
{
	static public String[] ref={"seqno","type","keytext"};
	
	public long seqno;				// 任务序号,主键
	public char type;				// 任务类型
	public String keytext;			// 任务关键字
}
