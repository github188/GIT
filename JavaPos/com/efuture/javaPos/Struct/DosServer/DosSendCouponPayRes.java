package com.efuture.javaPos.Struct.DosServer;

/*
 * CmdCode      = 80
 * CmdMemo      = 券交易送网（电子券扣款）
*/


public class DosSendCouponPayRes 
{

	public static String refSocket[] =
	{
		"msg|S|251",      //返回提示信息，第1位为Y表示成功，其它表示失败，第二位开始为报错提示信息
		"cardno|S|21",    //卡号
		"name|S|41",      //持卡人姓名
		"pwd|S|21",       //密码
		"ispwd|C|1",         //是否输入密码
		"func|S|21",		//功能扩展
		"ye|D|8",            //余额
		"money|D|8",         //面额
		"value1|D|8",        //备用
		"value2|D|8",        //备用
		"value3|D|8",        //备用
		"memo|S|251"     //备用
	};
	
	public String msg;			//[250+1];      //返回提示信息，第1位为Y表示成功，其它表示失败，第二位开始为报错提示信息
	public String cardno;		//[20+1];       //卡号
	public String name;			//[40+1];       //持卡人姓名
	public String pwd;			//[20+1];       //密码
	public char ispwd;          				//是否输入密码
	public String func;		    //[20+1];		//功能扩展
	public double ye;             				//余额
	public double money;          				//面额
	public double value1;         				//备用
	public double value2;         				//备用
	public double value3;         				//备用
	public String memo;			//[250+1];      //备用
}
