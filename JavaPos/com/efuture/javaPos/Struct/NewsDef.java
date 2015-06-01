package com.efuture.javaPos.Struct;

// 收银机通知类
public class NewsDef
{
	public static String[] ref={"seqno","rqsj","syyh","title","text"};
	
	public long seqno;				// 通知序号,主键
	public String rqsj;				// 时间,YYYY/MM/DD 00:00:00
	public String syyh;				// 收银员				
	public String title;			// 通知主题					
	public String text;				// 通知内容			
}
