package custom.localize.Bcsf;

public class DailyFeeItemDef
{
	public static String[] ref = { "incomeid", "name", "tenantid", "tenantname", "shopid", "flag", "moneyflag", "timetype", "payablemonth", "payablemoney", "paymoney", "editdate", "editor", "listno", "notes" };

	public String incomeid;// --项目编码
	public String name;// --项目名称
	public String tenantid;// --承租人编码
	public String tenantname;// --承租人名称
	public String shopid;// --门店编码
	public char flag;// --单据上传标志(0=未上传，=已上传)
	public char moneyflag;// --固定金额标志(0=固定金额，=非固定金额)
	public String timetype;// --收取时间类型（可选0=月、=季、=半年、=全年）
	public String payablemonth;// --应收月份
	public double payablemoney;// --应收金额
	public double paymoney; // 实付金额
	public String editdate;// --录入日期
	public String editor;// --录入人员
	public long listno; // 支付流水
	public String notes;// --备注
}
