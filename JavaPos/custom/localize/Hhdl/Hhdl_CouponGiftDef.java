package custom.localize.Hhdl;

public class Hhdl_CouponGiftDef
{
	public static String[] ref = { "type", "code", "info", "sl", "je", "startdate", "enddate","starttime","endtime", "memo" };

	public String type; // 类型,1-会员返券 2-纸券 3-刷卡送券 99-券总金额，98-可返券余额
						// BCRM(1-银行送/2-商场送/3-礼品/4-电子券/11-买券礼券/89-积分换停车券/90-停车券)
	public String code; // 代码
	public String info; // 描述,BCRM(券种券名称:券金额,券种券名称:券金额)
	public double sl; // 数量
	public double je; // 金额
	public String startdate; // 券起始有效期
	public String enddate; // 券终止有效期
	public String starttime; //券起始时间段
	public String endtime;	//券截止时间段
	public String memo; // 备注（商品编码，商品名称，规格，数量）

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
