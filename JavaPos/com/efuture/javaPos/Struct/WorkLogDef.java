package com.efuture.javaPos.Struct;

import java.io.Serializable;

// 工作日志定义
public class WorkLogDef implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static String[] ref={"seqno","netbz","rqsj","syjh","syyh","code","memo"};
	
	public long seqno;				// 日志序号,主键
	public char netbz;				// 是否已送网(Y/N)			
	public String rqsj;				// 日期时间(19位,1998/01/01 12:30:00)	
	public String syjh;				// 收银机号(4位)				
	public String syyh;				// 收银员号(8位)				
	public String code;				// 操作代码					
	public String memo;				// 工作备注(100位)	
}
