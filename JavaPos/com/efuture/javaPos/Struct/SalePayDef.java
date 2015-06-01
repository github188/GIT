package com.efuture.javaPos.Struct;

import java.io.Serializable;

// 交易小票付款明细定义
public class SalePayDef implements Cloneable,Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static String[] ref = {"syjh","fphm","rowno","paycode","payname","flag","ybje","hl","je","payno","batch","kye","idno"
								,"bankno","memo","str1","str2","str3","str4","str5","num1","num2","num3","num4","num5","num6"};
	
	public String syjh;				// 收银机号,主键				
	public long fphm;				// 小票号,主键	
	public int rowno;				// 行号,主键
	public String paycode;			// 付款方式代码	
	public String payname;			// 付款方式名称
	public char flag;				// 标志,'1'-付款,'2'-找零,'3'-扣回
	public double ybje;				// 原币金额
	public double hl;				// 汇率
	public double je;				// 付款金额，原币金额*汇率	
	public String payno;			// 付款卡号(20位,支票号/信用卡号/内部卡号/面值卡号)
	public String batch;			// 交易批号
									// PaymentJfNew 积分类型
	public double kye;				// 卡上余额					
	public String idno;				// 证件号码(同付款多券种的券种标识)
									// PaymentJfNew 要扣的积分,规则中定义的积分数,规则中定义的折现金额,换购规则单号,商品编码，商品数量,积分类型,档期
	public String bankno;			// 银行代码
	public String memo;				// BJYS:提货日期		 
									// BCRM:积分消费付款方式时(1-积分消费/2-积分换购/3-零钞转存)
									// SZXW:定金单付款方式时(余额定金单,金额)
	public String str1;				// 银联流水号
									// HZJB,IC卡序列号
	public String str2;				// HZJB:商品行号,商品编码
	public String str3;				// 换货标记
	public String str4;				// SZXW:,原付款代码,原卡号，柜组
	public String str5;				// 面值卡回收标志,不能修改。
	public double num1;				// 纸券的损益金额,不能修改。
	public double num2;				// 备用
									// BXMX:预售提货时，标示为预付已经付款的付款方式。
	public double num3;				// 备用
	                                // KSBL:记录刷银联折扣金额  打印小票时需要
	public double num4;				// CCZZ:累计次数
	public double num5;				// 唯一序号
	
	public char ispx;				// 是否需要配现付款方式
	public char isused;				// 是否被使用（用于买券扣回时计算）
	public String str6;				// 备用字段，不保存本地 (NJXB 储值卡付款手续费费率) 
	public double num6;             // 用于记录银联接口中返回的优惠金额,小票上打印
	
	public Object clone()
	{
		try 
		{
			return super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			e.printStackTrace();
			return this;
		}
	}
}
