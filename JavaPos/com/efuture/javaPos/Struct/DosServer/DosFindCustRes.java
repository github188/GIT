package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 10
 * CmdMemo      = 查找会员信息
*/

public class DosFindCustRes
{

	public static String[] refSocket = 
	{
		"msg|S|251",
		"cardno|S|21",
		"type|S|3", 
		"name|S|21", 
		"status|C|1", 
		"expdate|S|11",
	    "track|S|121", 
	    "memo|S|251", 
	    "valuememo|D|8", 
	    "ishy|C|1",
	    "iszk|C|1", 
	    "isjf|C|1",
	    "func|S|11", 
	    "zkl|D|8", 
	    "value1|D|8",
	    "value2|D|8", 
	    "value3|D|8", 
	    "value4|D|8",
		"value5|D|8"
	};
	
	public String msg;
	public String cardno;
	public String type;
	public String name;
	public char status;
	public String expdate;
	public String track;
	public String memo;
	public double valuememo;
	public char ishy;
	public char iszk;
	public char isjf;
	public String func;
	public double zkl;
	public double value1;
	public double value2;
	public double value3;
	public double value4;
	public double value5;
	
}
