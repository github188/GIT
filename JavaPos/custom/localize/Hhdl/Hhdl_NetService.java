package custom.localize.Hhdl;

import java.util.Vector;
import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_NetService;

public class Hhdl_NetService extends Bstd_NetService
{
	public int getGoodsShelfLife(String code)
	{
		if (!GlobalInfo.isOnline)
			return 0;

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { code };
		String[] args = { "code" };

		try
		{
			head = new CmdHead(CmdDef.HHDL_GETGOODSSHELFLIFE);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "获取保质期失败");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "date" });
				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);
					if (row1 != null && row1.length > 0)
						return Convert.toInt(row1[0].trim());
				}
			}

			return 0;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	public boolean getSaleTicketMSInfo(Vector v, String mktcode, String syjh, String fphm, String iscd, Http http, int cmdCode)
	{
		if (!GlobalInfo.isOnline)
			return false;

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { mktcode, GlobalInfo.sysPara.jygs, syjh, fphm, iscd };
		String[] args = { "mktcode", "jygs", "syjh", "fphm", "cdbz" };

		try
		{
			head = new CmdHead(cmdCode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(http, line, "查询赠送信息失败!\n");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, Hhdl_CouponGiftDef.ref);

				Hhdl_CouponGiftDef def = null;
				for (int i = 0; i < vi.size(); i++)
				{
					def = new Hhdl_CouponGiftDef();

					String[] row = (String[]) vi.elementAt(i);

					if (Transition.ConvertToObject(def, row))
					{
						v.add(def);
					}
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(getClass()).error(ex);
			return false;
		}
	}

	public boolean getBackSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		if (super.getBackSaleInfo(syjh, fphm, shd, saleDetailList, payDetail))
		{
			if (getBackSaleCoupon(syjh, fphm))
				new MessageBox("原始小票" + String.valueOf(fphm) + "有券产生");

			return true;
		}
		return false;
	}

	protected boolean getBackSaleCoupon(String syjh, String fphm)
	{
		if (!GlobalInfo.isOnline)
			return false;
		StringBuffer line = new StringBuffer();
		int result = -1;

		CmdHead head = null;
		String[] values = { GlobalInfo.sysPara.mktcode, syjh, fphm };
		String[] args = { "mktcode", "syjh", "fphm" };

		try
		{
			head = new CmdHead(CmdDef.GETBACKSALEISUSEDCOUPON);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "查询退货券付款信息失败");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "coupon" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (row[0].trim().equals("1"))
						return true;
				}
				return false;
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean sendFjkSale(MzkRequestDef req, Hhdl_CouponDef coupon)
	{
		if (!GlobalInfo.isOnline)
			return false;

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.SENDFJK);
			line.append(head.headToString() + Transition.ConvertToXML(req));

			result = HttpCall(getMemCardHttp(CmdDef.SENDFJK), line, "返券卡交易失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, Hhdl_CouponDef.ref);

				if (v.size() > 0)
				{
					String[] lines = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(coupon, lines))
					{
						if (coupon == null)
							return false;

						coupon.convertRatio();
						return true;
					}
				}
			}

			return false;
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			return false;
		}
	}

}
