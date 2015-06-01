package custom.localize.Bcsf;

public class DailyFeePayDef
{
	public static String[] ref = { "listno", "paytype", "paymoney", "cardno", "transno", "sdate", "cashierid", "posid" };

	// public int listno;// --支付流水
	public String paytype;// --支付方式(现金,银联等)
	public double paymoney;// --实收金额
	// public double givechange;// --找零
	public String cardno; // 付款卡号
	public String transno; // 第三方付款参考号
	public String sdate;// --收款日期
	public String cashierid;// --收银员号
	public String posid; // --POS机号
}
