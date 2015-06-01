package custom.localize.Jplm;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;

public class Jplm_CouponDef implements Cloneable
{

	public static String[] ref = { "type", "condition","cardno", "money", "rule", "memo" };

	public String type; // 券类型
	public String condition; // 条件
	public String cardno; // 券号
	public double money; // 券面值
	public String rule; // 收券规则
	public String memo; // 备注

	public double cardinalnumber; // 收券基数
	public double availablemoney; // 可用券金额
	public double mostmoney; // 最多可用券金额
	public String limitpaycode; // 限制的付款方式
	public double enablemoney; // 券可用金额

	// 辅助字段
	public static double totalmoney; // 券累计金额
	public double minlimitmoney; // 用于计算最低限额

	public static String goodsindex; // 此规则券一共是哪些商品可用
	public String allsaleindex; // 哪些商品都即可用全场又可用非全场
	public static int maxmoneyindex = -1; // 此规则券下最大金额的商品索引

	public boolean isused; // 券是否使用
	public double amount; // 付款金额
	public double excep; // 付款损溢
	
	public String paycardno;	//已扣款的券
	public String paycardexcep; //券损溢
	
	public boolean convertRatio()
	{
		if (rule != null && rule.length() > 0 && rule.indexOf(",") > 0)
		{
			String[] part = rule.split(",");

			if (part != null && part.length > 0)
				cardinalnumber = ManipulatePrecision.doubleConvert(Convert.toDouble(part[0].trim()), 2, 1);

			if (part != null && part.length > 1)
				availablemoney = ManipulatePrecision.doubleConvert(Convert.toDouble(part[1].trim()), 2, 1);

			if (part != null && part.length > 2)
				mostmoney = ManipulatePrecision.doubleConvert(Convert.toDouble(part[2].trim()), 2, 1);

			if (part != null && part.length > 3)
				limitpaycode = part[3].trim();
			else
				limitpaycode = "";
			
			// 默认可用金额为当前面值金额
			enablemoney = money;
			
			return true;
		}
		return false;
	}

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
