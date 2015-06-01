package com.efuture.javaPos.Struct;

public class BankLogDef 
{
	public static String[] ref = {"net_bz","rowcode","rqsj","syjh","fphm","syyh","type","je",
		"oldrq","oldtrace","typename","classname","cardno","trace","bankinfo","crc","retcode","retmsg","retbz",
		"allotje","memo","kye","authno","memo1","memo2","ylzk","tempstr","tempstr1"};
	
	public char net_bz;			/* 送网标志			*/
	public int  rowcode;			/* 行号			主键*/
	public String rqsj;				/* 交易时间		主键*/
	public String syjh;				/* 收银机号		主键*/
	public long fphm;				/* 小票号			*/
	public String syyh;				/* 收银员号			*/
	public String type;				/* 交易类型			*/
	public double je;				/* 交易金额			*/
	public String oldrq;			/* 原消费日期		*/
	public long oldtrace;			/* 原消费流水		*/
	public String typename;			/* 交易类型名称	*/
	public String classname;		/* 类名称*/
	
	public String cardno;			/* 卡号				*/
	public long trace;				/* 流水号			*/
	public String bankinfo;			/* 发卡银行			*/
									// Rkys:银行小票号
	public String crc;				/* CRC校验			*/
	public String retcode;			/* 返回码			*/
	public String retmsg;			/* 返回信息			*/
	public char retbz;				/* 成功标志			*/
	public double allotje;			/* 待分配的金额		*/
	public double  kye;				/* 卡余额				*/
	public String authno;			/* 系统参考号			*/
	public String memo;				/* 备用				*/
	public String memo1;			/* 备用				*/
	public String memo2;			/* 备用				*/
	public double ylzk;             /* 银联折扣金额		*/
	
	public String tempstr;			/* 临时参数*/
	public String tempstr1;			/* 临时参数*/
}
