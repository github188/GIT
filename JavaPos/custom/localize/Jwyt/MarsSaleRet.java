package custom.localize.Jwyt;

import java.io.Serializable;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;

public class MarsSaleRet implements Cloneable, Serializable
{
	private static final long serialVersionUID = 1L;

	public static String[] ref = { "mktcode", "fphm", "syyh", "syjh", "transseq", "method", "status", "assistcode", "timestamp", "couponname", 
		"availablenum", "couponid", "marketingid","isgift", "tcodeid", "coupontype", "price", "discountrate", "balancemoney", "moneycoupontype", "secrettype", "balancesum", "effectivedate", "expiredate", "fixedfee" };

	public String fphm;
	public String mktcode;
	public String syyh;
	public String syjh;

	public String transseq; // 同传递过来的值
	public String method; // //命令名称,同传递过来的值
	public String status; // //如果是00，表示成功，其它值为失败
	public String assistcode; // //凭证编码
	public String timestamp;//

	// 如果成功，则是/屏显内容，详情见上面表1
	public String couponid; // 凭证Id,券的唯一标识
	public String marketingid;// 促销编号 仅用于身份凭证，使用次数为无限制
	public String isgift;	//
	public String tcodeid;//
	public String couponname;// ”凭证名称” , //券的名称
	public String availablenum;// 可使用次数 数值大于10000为无限制
	public String coupontype;// ：”凭证类型”, //0：代金券；1：折扣券；2：身份认证券
	public String price;// ”面额”, //只针对代金券有效
	public String discountrate;// ”折扣”, //针对折扣券生效
	public String balancemoney;// "码余额", //代金券有效
	public String moneycoupontype;// ””, //代金券有效,0：一次性消费,1：固定金额消费，2：不固定金额消费
	public String secrettype;// ”, //0：无需密码 1： 随机密码 2:固定密码
	public String balancesum;// "11111", //剩余数量
	public String effectivedate;// "2013-05-09 00:46:00", //开始时间
	public String expiredate;// "2013-05-31 00:46:00", // 结束时间
	public String fixedfee;// "10.0", //固定扣费金额 只针对于固定金额消费 moneyCouponType=1

	public boolean isusing;

	/*
	 * 【营销编号】字段修改为【促销编号】：身份凭证，使用次数为无限制时显示。 【抵扣编号】： 代金凭证，类型为一次性消费时显示。
	 * 【领用编号】：身份凭证，使用次数为1的时候显示。
	 */

	public MarsSaleRet()
	{
		fphm = GlobalInfo.syjStatus.fphm + "";
		mktcode = GlobalInfo.sysPara.mktcode;
		syyh = GlobalInfo.syjStatus.syyh;
		syjh = GlobalInfo.syjStatus.syjh;
		isusing = false;
	}

	public void setUsing(boolean isuse)
	{
		isusing = isuse;
	}

	public boolean isUsing()
	{
		return isusing;
	}

	// 是否现金券
	public boolean isCashCoupon()
	{
		return coupontype.equals("0") ? true : false;
	}

	public boolean isMzkCash()
	{
		if (isCashCoupon())
			if (moneycoupontype.equals("2"))
				return true;

		return false;
	}

	// 是否折扣券
	public boolean isRebateCoupon()
	{
		return (coupontype.equals("2") && isgift.equals("0")) ? true : false;
	}

	// 是否礼品券
	public boolean isGiftCoupon()
	{
		return (coupontype.equals("2") && isgift.equals("1")) ? true : false;
	}

	// 是否抵扣券
	public boolean isDeductCoupon()
	{
		if (isCashCoupon())
			if (moneycoupontype.equals("0") || moneycoupontype.equals("1"))
				return true;

		return false;
	}

	public void showCashMsg()
	{
		if (!isCashCoupon())
			return;

		StringBuffer info = new StringBuffer();
		String str = "";
		info.append("凭  证Id: " + Convert.appendStringSize("", couponid, 1, 16, 16, 0) + "\n");
		info.append("凭证CODE: " + Convert.appendStringSize("", tcodeid, 1, 16, 16, 0) + "\n");
		info.append("凭证名称: " + Convert.appendStringSize("", couponname, 1, 16, 16, 0) + "\n");
		info.append("凭证类型: " + Convert.appendStringSize("", "代金券", 1, 16, 16, 0) + "\n");
		info.append("面    额: " + Convert.appendStringSize("", price, 1, 16, 16, 0) + "\n");
		info.append("余    额: " + Convert.appendStringSize("", balancemoney, 1, 16, 16, 0) + "\n");

		if (moneycoupontype.equals("0"))
			str = "一次性消费";
		else if (moneycoupontype.equals("1"))
			str = "固定金额消费";
		else if (moneycoupontype.equals("2"))
			str = "不固定金额消费";
		else
			str = "未知类型";

		info.append("消费类型: " + Convert.appendStringSize("", str, 1, 16, 16, 0) + "\n");
		info.append("开始时间: " + Convert.appendStringSize("", effectivedate.substring(0, 10), 1, 16, 16, 0) + "\n");
		info.append("结束时间: " + Convert.appendStringSize("", expiredate.substring(0, 10), 1, 16, 16, 0) + "\n");

		new MessageBox(info.toString());
	}

	public void showGiftMsg()
	{
		if (!isGiftCoupon())
			return;

		StringBuffer info = new StringBuffer();

		info.append("凭  证Id: " + Convert.appendStringSize("", couponid, 1, 16, 16, 0) + "\n");
		info.append("凭证CODE: " + Convert.appendStringSize("", tcodeid, 1, 16, 16, 0) + "\n");
		info.append("凭证类型: " + Convert.appendStringSize("", "身份认证券", 1, 16, 16, 0) + "\n");
		info.append("凭证名称: " + Convert.appendStringSize("", couponname, 1, 16, 16, 0) + "\n");

		info.append("剩余数量: " + Convert.appendStringSize("", balancesum.equals("32767") ? "无限次数" : balancesum, 1, 16, 16, 0) + "\n");
		info.append("开始时间: " + Convert.appendStringSize("", effectivedate.substring(0, 10), 1, 16, 16, 0) + "\n");
		info.append("结束时间: " + Convert.appendStringSize("", expiredate.substring(0, 10), 1, 16, 16, 0) + "\n");

		new MessageBox(info.toString());
	}

	public void showRebateMsg()
	{
		if (!isRebateCoupon())
			return;

		StringBuffer info = new StringBuffer();

		info.append("凭  证Id: " + Convert.appendStringSize("", couponid, 1, 16, 16, 0) + "\n");
		info.append("凭证CODE: " + Convert.appendStringSize("", tcodeid, 1, 16, 16, 0) + "\n");
		info.append("凭证名称: " + Convert.appendStringSize("", couponname, 1, 16, 16, 0) + "\n");
		info.append("凭证类型: " + Convert.appendStringSize("", "折扣券", 1, 16, 16, 0) + "\n");
		//info.append("折 扣 率: " + Convert.appendStringSize("", discountrate, 1, 16, 16, 0) + "\n");

		info.append("剩余数量: " + Convert.appendStringSize("", balancesum.equals("32767") ? "无限次数" : balancesum, 1, 16, 16, 0) + "\n");
		info.append("开始时间: " + Convert.appendStringSize("", effectivedate.substring(0, 10), 1, 16, 16, 0) + "\n");
		info.append("结束时间: " + Convert.appendStringSize("", expiredate.substring(0, 10), 1, 16, 16, 0) + "\n");

		new MessageBox(info.toString());

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
