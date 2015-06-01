package custom.localize.Bxmx;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_NetService;

public class Bxmx_NetService extends Bstd_NetService
{
	public int sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		if (SellType.ISEARNEST(saleHead.djlb) || SellType.ISPREPARETAKE(saleHead.djlb))
		{
			if (SellType.ISPREPARETAKE(saleHead.djlb))
			{	
					for (int i = 0; i < salePayment.size(); i++)
					{
						SalePayDef sp = (SalePayDef) salePayment.get(i);
						if (sp.str2 != null && sp.str2.equals("Y"))
						{
							salePayment.removeElement(sp);
							i--;
						}
					}
			 }
			return sendSaleData(saleHead, saleGoods, salePayment, retValue, null, CmdDef.XMX_SENDPRESELL);
		}
		else
			return sendSaleData(saleHead, saleGoods, salePayment, retValue, null, CmdDef.SENDSELL);
	}

	public boolean updatePreSaleheadFlag(SaleHeadDef saleHead)
	{
		String[] srcinfo = saleHead.yfphm.split("#");

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { srcinfo[0], srcinfo[1], srcinfo[2] };
		String[] args = { "desmktcode", "syjh", "fphm" };

		try
		{
			// 查询退货小票头
			head = new CmdHead(CmdDef.XMX_UPDATEPRESALEHEAD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
				return false;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean getPopNew(String mktcode, Vector retvec)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { mktcode };
		String[] args = { "mktcode" };

		try
		{
			// 查询退货小票头
			head = new CmdHead(CmdDef.XMX_GETPOPNEWS);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
				return false;

			Vector vec = new XmlParse(line.toString()).parseMeth(0, new String[] { "news" });
			if (vec == null || vec.size() == 0)
				return false;

			String[] tmpinfo = (String[]) vec.get(0);
			retvec.add(tmpinfo);

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean checkMktcode(String mktcode, Vector retvec)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { mktcode };
		String[] args = { "desmktcode" };

		try
		{
			// 查询退货小票头
			head = new CmdHead(CmdDef.XMX_CHECKSHOPID);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
				return false;

			Vector vec = new XmlParse(line.toString()).parseMeth(0, new String[] { "mktname" });
			if (vec == null || vec.size() == 0)
				return false;

			String[] tmpinfo = (String[]) vec.get(0);
			retvec.add(tmpinfo);

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean getPreSaleHead(Vector saleHeadList)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode };
		String[] args = { "mktcode" };

		try
		{
			// 查询退货小票头
			head = new CmdHead(CmdDef.GETPRESALEHEADINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				// new MessageBox("预售小票头查询失败!");
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleHeadDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到预定销售小票!");
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				String[] row = (String[]) v.elementAt(i);

				SaleHeadDef shd = new SaleHeadDef();

				if (Transition.ConvertToObject(shd, row))
				{
					saleHeadList.add(shd);
				}
				else
				{
					saleHeadList.clear();
					saleHeadList = null;
					return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean getPreSaleGoods(String mktcode, String syjh, String fphm, Vector saleGoodsList)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { mktcode, syjh, fphm };
		String[] args = { "mktcode", "syjh", "fphm" };

		try
		{
			// 查询退货小票明细
			head = new CmdHead(CmdDef.GETPRESALEDETAILINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("预售小票明细查询失败!");
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到预售小票明细,预售小票不存在或已确认!");
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				String[] row = (String[]) v.elementAt(i);

				SaleGoodsDef sgd = new SaleGoodsDef();

				if (Transition.ConvertToObject(sgd, row))
				{
					saleGoodsList.add(sgd);
				}
				else
				{
					saleGoodsList.clear();
					saleGoodsList = null;
					return false;
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean getPreSalePay(String mktcode, String syjh, String fphm, Vector salePayList)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { mktcode, syjh, fphm };
		String[] args = { "mktcode", "syjh", "fphm" };

		try
		{ // 查询小票付款明细
			head = new CmdHead(CmdDef.GETPRESALEPAYINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("付款明细查询失败!");
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SalePayDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到预售小票付款明细,预售小票不存在或已确认!");
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				String[] row = (String[]) v.elementAt(i);
				SalePayDef spd = new SalePayDef();

				if (Transition.ConvertToObject(spd, row))
				{
					salePayList.add(spd);
				}
				else
				{
					salePayList.clear();
					salePayList = null;
					return false;
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	//goodstype 0为面值卡 1为券
	public boolean sellCardOrCoupon(String oprtype, String goodstype,String start, String end, String count, String zkinfo, Vector retvec)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { oprtype, goodstype,GlobalInfo.sysPara.mktcode, GlobalInfo.syjStatus.syyh, start, end, count, zkinfo };
		String[] args = { "oprtype","type", "mktcode", "syyh", "start", "end", "count", "zkinfo" };

		try
		{
			head = new CmdHead(CmdDef.XMX_SELLCOUPON);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("券卡交易查询失败!");
				return false;
			}

			if ((oprtype.equals("1") || oprtype.equals("3")) && result == 0)
				return true;

			Vector vec = new XmlParse(line.toString()).parseMeth(0, new String[] { "retinfo" });

			if (vec == null || vec.size() == 0)
				return false;

			String[] tmpinfo = (String[]) vec.get(0);
			retvec.add(tmpinfo);

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean findGoodsCoupon(String code, Vector info)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { code };
		String[] args = { "code" };

		try
		{
			head = new CmdHead(CmdDef.XMX_FINDGOODCOUPON);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
				return false;

			Vector retinfo = new XmlParse(line.toString()).parseMeth(0, new String[] { "code", "goodsname", "unit", "ye" });

			if (retinfo == null)
				return false;

			for (int i = 0; i < retinfo.size(); i++)
			{
				String[] tmpinfo = (String[]) retinfo.get(i);
				if (tmpinfo != null)
					info.add(tmpinfo);
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean getPreBackSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh, fphm };
		String[] args = { "syjh", "code" };

		try
		{
			// 查询退货小票头
			head = new CmdHead(CmdDef.XMX_GETPREBACKSALEHEAD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("退货小票头查询失败!");
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleHeadDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到退货小票头,退货小票不存在或已确认!");
				return false;
			}

			String[] row = (String[]) v.elementAt(0);

			if (!Transition.ConvertToObject(shd, row))
			{
				shd = null;
				new MessageBox("退货小票头转换失败!");
				return false;
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询退货小票明细
			head = new CmdHead(CmdDef.XMX_GETPREBACKSALEDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("退货小票明细查询失败!");
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到退货小票明细,退货小票不存在或已确认!");
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				SaleGoodsDef sgd = new SaleGoodsDef();

				if (Transition.ConvertToObject(sgd, row))
				{
					saleDetailList.add(sgd);
				}
				else
				{
					saleDetailList.clear();
					saleDetailList = null;
					return false;
				}
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询小票付款明细
			head = new CmdHead(CmdDef.XMX_GETPREBACKPAYSALEDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("付款明细查询失败!");
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SalePayDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到付款小票明细,退货小票不存在或已确认!");
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);
				SalePayDef spd = new SalePayDef();

				if (Transition.ConvertToObject(spd, row))
				{
					payDetail.add(spd);
				}
				else
				{
					payDetail.clear();
					payDetail = null;
					return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			shd = null;

			if (saleDetailList != null)
			{
				saleDetailList.clear();
				saleDetailList = null;
			}
			ex.printStackTrace();
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
	}

}
