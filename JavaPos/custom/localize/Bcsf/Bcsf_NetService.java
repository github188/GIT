package custom.localize.Bcsf;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_NetService;

public class Bcsf_NetService extends Bstd_NetService
{
	public boolean getSaleStamp(String[] row)
	{
		if (!GlobalInfo.isOnline)
			return false;

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { GlobalInfo.sysPara.mktcode + "", GlobalInfo.syjStatus.syjh + "", GlobalInfo.syjStatus.fphm - 1 + "" };
		String[] args = { "mktcode", "syjh", "fphm", };

		try
		{

			head = new CmdHead(CmdDef.GETSTAMPCOUNT);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("计算本笔小票印花派送数量失败!"));

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "basestamp", "otherstamp", "memo" });

				if (vi.size() > 0)
				{
					String[] rows = (String[]) vi.elementAt(0);

					if (rows.length > 2)
					{
						row[0] = rows[0];
						row[1] = rows[1];
						return true;
					}
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

	public boolean getBackSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		if (!super.getBackSaleInfo(syjh, fphm, shd, saleDetailList, payDetail))
			return false;

		// 由于之前调用过广众和知而行增加消费的方法,做过标志,把以取回来的时候要清空
		shd.memo = "";
		shd.str2 = "";

		return true;
	}

	// 查询印花换购规则
	public boolean findStampGoods(GoodsDef goodsDef, String[] row)
	{
		if (!GlobalInfo.isOnline)
			return false;

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { goodsDef.barcode };
		String[] args = { "barcode" };

		try
		{
			head = new CmdHead(CmdDef.GETSTAMPGOODS);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "price", "stamp" });

				if (v.size() > 0)
				{
					String[] tmp = (String[]) v.elementAt(0);

					if (tmp.length > 1)
					{
						row[0] = tmp[0];
						row[1] = tmp[1];
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

	// 超市用于查找商品用券规则
	public boolean findGoodsCouponRule(GoodsDef goodsDef, String code, String rqsj, Http http)
	{
		if (!GlobalInfo.isOnline)
			return false;

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.syjStatus.syjh, code, rqsj };
		String[] args = { "mktcode", "syjh", "code", "rqsj" };

		try
		{
			head = new CmdHead(CmdDef.XMX_FINDGOODCOUPON);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "istoken" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					goodsDef.couponrule = row[0].trim();
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

	public boolean getDailyFeeBill(String tenantid, String payablemonth, Vector bills)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		try
		{
			int result = -1;

			cmdHead = new CmdHead(CmdDef.GETDAILYFEEBILL);

			String[] value = { tenantid, payablemonth, GlobalInfo.sysPara.mktcode };
			String[] arg = { "tenantid", "payablemonth", "shopid" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, "未找到单据!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, DailyFeeItemDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						DailyFeeItemDef fee = new DailyFeeItemDef();

						if (Transition.ConvertToObject(fee, row))
						{
							bills.add(fee);
						}
					}
				}
				else
				{
					return false;
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

			return false;
		}
		finally
		{
			cmdHead = null;
			line = null;
		}

	}

	public boolean sendDailyFeeBill(long billno, Vector item, Vector pay)
	{
		// if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.SENDDAILYFEEBILL);

			String[] values = { String.valueOf(billno) };
			String[] args = { "billno" };

			String headItem = "";
			headItem = Transition.ItemDetail(values, args);
			headItem = Transition.closeTable(headItem, "DailyFeeHeadDef", 1);

			String feeItem = "";
			for (int i = 0; i < item.size(); i++)
			{
				DailyFeeItemDef tmpItem = (DailyFeeItemDef) item.get(i);
				feeItem += Transition.ItemDetail(tmpItem, tmpItem.ref);
			}
			feeItem = Transition.closeTable(feeItem, "DailyFeeItemDef", item.size());

			String payItem = "";
			for (int i = 0; i < pay.size(); i++)
			{
				DailyFeePayDef tmpPay = (DailyFeePayDef) pay.get(i);
				payItem += Transition.ItemDetail(tmpPay, tmpPay.ref);
			}
			payItem = Transition.closeTable(payItem, "DailyFeePayDef", 1);

			String cmdLine = Transition.getHeadXML(headItem + feeItem + payItem);

			line.append(aa.headToString() + cmdLine);

			result = HttpCall(line, "单据提交失败");

			if (result == 0)
				return true;
			return false;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}

	public boolean sendMzkSale(Http h, MzkRequestDef req, MzkResultDef ret, int cmdCode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(cmdCode);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, "储值卡交易失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MzkResultDef.ref);

				if (req.type.equals("02") || req.type.equals("04"))
					return true;

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(ret, row))
						return true;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;

	}

	public boolean saleMzk(String type, String track, Vector info)
	{
		return saleMzk(type, track, 0, 0, 0, info,"");
	}

	public boolean saleMzk(String type, String track, double cash, double bank, double cardfee, Vector info,String memo)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, String.valueOf(GlobalInfo.syjStatus.fphm), GlobalInfo.syjStatus.syyh, GlobalInfo.syjStatus.syjh, type, track, String.valueOf(cash), String.valueOf(bank), String.valueOf(cardfee), memo };
		String[] args = { "mktcode", "fphm", "syyh", "syjh", "type", "track", "cash", "bank", "cardfee", "memo" };

		try
		{
			head = new CmdHead(CmdDef.XMX_SELLCOUPON);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(getCardHttp(),line, "");

			if (result != 0)
			{
				new MessageBox("售卡交易失败!");
				return false;
			}

			if (type.equals("02") && result == 0)
				return true;

			Vector vec = new XmlParse(line.toString()).parseMeth(0, new String[] { "cardno", "cardtype", "money", "cost", "memo" });

			if (vec == null || vec.size() == 0)
				return false;

			String[] tmpinfo = (String[]) vec.get(0);
			info.add(tmpinfo);

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
