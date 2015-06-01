package com.efuture.javaPos.Struct;

public class R5CouponDef
{
	public static String[] ref = { "flag", "type", "money", "disc", "amount", "paytype", "paymoney", "goodslist", "oldmoney","FanQuanTotal"};

	public String couponno; // 券号
	public int flag; /* 券类型标志：1=不定额代金券 2=定额代金券 4=折扣券 */
	public String type; /* 券类型 */
	public double money;// 折算后券面值
	public int disc;// 券折扣率 例子: 0.9=9折
	public double amount;// 可用券金额,为0表示不限制使用
	public String paytype; /* 券支付方式 */
	public double paymoney;/* 需满足支付金额 */
	public String goodslist;
	public double oldmoney;// 原始面值
	public double FanQuanTotal;  //参与返券的商品总金额


	public String getCouponName()
	{
		if (flag == 1)
			return "不定额代金券";
		else if (flag == 2)
			return "定额代金券";
		else if (flag == 4)
			return "折扣券";
		else
			return "未知类型券";
	}
}
