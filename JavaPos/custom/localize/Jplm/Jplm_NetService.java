package custom.localize.Jplm;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_NetService;

public class Jplm_NetService extends Bstd_NetService
{
	public int sendSaleBill(SaleHeadDef saleHead, Vector saleGoods)
	{
		SaleGoodsDef saleGoodsDef = null;

		if (!GlobalInfo.isOnline)
			return -1;

		try
		{
			CmdHead aa = null;
			aa = new CmdHead(CmdDef.XMX_SENDPRESELL);

			// 单头打XML
			String line = Transition.ItemDetail(saleHead, SaleHeadDef.ref, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } });
			line = Transition.closeTable(line, "SaleHeadDef", 1);

			// 小票明细
			String line1 = "";

			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				line1 += Transition.ItemDetail(saleGoodsDef, SaleGoodsDef.ref);
			}

			line1 = Transition.closeTable(line1, "saleGoodsDef", saleGoods.size());

			line = Transition.getHeadXML(line + line1);

			StringBuffer line3 = new StringBuffer();
			line3.append(aa.headToString() + line);

			return HttpCall(line3, "预上传小票失败!");

		}
		catch (Exception er)
		{
			er.printStackTrace();
			return -1;
		}
	}

	public boolean getSaleCoupon(String cardno, String paycode, CouponRet ret)
	{
		if (!GlobalInfo.isOnline)
			return false;

		StringBuffer line = new StringBuffer();
		int result = -1;

		CmdHead head = null;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.syjStatus.syjh, GlobalInfo.syjStatus.fphm + "", GlobalInfo.sysPara.jygs, cardno, paycode };
		String[] args = { "mktcode", "syjh", "fphm", "jygs", "couponno", "paycode" };

		try
		{
			head = new CmdHead(CmdDef.XMX_SELLCOUPON);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "计算券付款失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, CouponRet.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(ret, row))
						return true;
				}
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	public boolean findRabateCoupon(String coupon, String[] row)
	{
		if (!GlobalInfo.isOnline)
			return false;

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { coupon };
		String[] args = { "cutpricecode" };

		try
		{
			head = new CmdHead(CmdDef.GETSTAMPGOODS);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "goodscode", "goodsprice", "goodsdjbh" });

				if (v.size() > 0)
				{
					String[] tmp = (String[]) v.elementAt(0);

					if (tmp.length > 1)
					{
						row[0] = tmp[0];
						row[1] = tmp[1];
						row[2] = tmp[2];
					}
					return true;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
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

	public boolean sendFjkSale(MzkRequestDef req, Jplm_CouponDef coupon)
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
				Vector v = new XmlParse(line.toString()).parseMeth(0, Jplm_CouponDef.ref);

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
