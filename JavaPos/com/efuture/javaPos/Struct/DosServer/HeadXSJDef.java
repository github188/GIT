package com.efuture.javaPos.Struct.DosServer;

/**
 * 重百新世纪提货卡包头类
 * @author wangy
 *
 */
public class HeadXSJDef
{
	public static String refSocket[] =
	{
		"iSum|I|2",
		"sForegroundCode|S|5",
		"iCommandCode|I|2",
		"iCommandFirst|I|2",
		"iRecordNum|I|2"
	};
	
	/**
	 * 随机数
	 */
	public short iSum;
	
	/**
	 * 收银机号
	 */
	public String sForegroundCode;
	
	/**
	 * 命令代码(返回时，1表示成功，其它表示失败）
	 */
	public short iCommandCode;

	/**
	 * 命令优先级
	 */
	public short iCommandFirst;

	/**
	 * 包体的记录
	 */
	public short iRecordNum;
}
