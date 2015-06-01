package custom.localize.Jwyt;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.CmPopRuleDef;
import com.efuture.javaPos.Struct.CmPopRuleLadderDef;
import com.efuture.javaPos.Struct.CmPopTitleDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.JfSaleRuleDef;

import custom.localize.Bstd.Bstd_NetService;

public class Jwyt_NetService extends Bstd_NetService
{
	public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz, String uid, String rulecode, String catid, String ppcode, String time, String cardno, String cardtype, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		if (cardno == null)
		{
			cardno = " ";
		}

		if (cardtype == null)
		{
			cardtype = " ";
		}

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype, "", "" };
		String[] args = { "mktcode", "jygs", "code", "gz", "uid", "rule", "catid", "ppcode", "rqsj", "cardno", "cardtype", "istempcard", "custgrp" };

		try
		{
			head = new CmdHead(CmdDef.FINDCRMPOP);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(http, line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsPopDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(popDef, row)) { return true; }
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;

	}

	public boolean findHYZK(GoodsPopDef popDef, String code, String custtype, String gz, String catid, String ppcode, String specialInfo, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { code, custtype, GlobalInfo.sysPara.mktcode, gz, catid, ppcode, specialInfo, GlobalInfo.sysPara.jygs };
		String[] args = { "code", "custtype", "mktcode", "gz", "catid", "ppcode", "specinfo", "jygs" };

		try
		{
			head = new CmdHead(CmdDef.GETCRMVIPZK);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(http, line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "zk", "zkmk", "num1", "num2", "str1", "str2", "memo" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					popDef.pophyj = Convert.toDouble(row[0]);

					popDef.num1 = Convert.toDouble(row[1]);

					popDef.num2 = Convert.toDouble(row[2]);

					popDef.num3 = Convert.toDouble(row[3]);

					popDef.str1 = row[4];

					popDef.str2 = row[5];

					popDef.memo = row[6];

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
	public boolean findGoodsCouponRule(GoodsDef goodsDef, String code, String gz, String uid, String catid, String ppcode, String time, String cardno, String cardtype, String djlb, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		if (cardno == null)
		{
			cardno = " ";
		}

		if (cardtype == null)
		{
			cardtype = " ";
		}

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, code, gz, uid, "rule", catid, ppcode, time, cardno, cardtype, "N", "" };
		String[] args = { "mktcode", "jygs", "code", "gz", "uid", "rule", "catid", "ppcode", "rqsj", "cardno", "cardtype", "istempcard", "custgrp" };

		try
		{
			head = new CmdHead(CmdDef.FINDCRMPOP);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(http, line, "");

			if (result == 0)
			{
				GoodsPopDef popDef = new GoodsPopDef();
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsPopDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(popDef, row))
					{
						goodsDef.str4 = popDef.mode;
						return true;
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}

	// 查找商品是否存在换购规则
	public boolean getJfExchangeGoods(JfSaleRuleDef jsrd, String code, String gz, String cuscode, String type)
	{
		if (!GlobalInfo.isOnline)
			return false;

		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.GETGOODSEXCHANGE);

			String[] value = { GlobalInfo.sysPara.mktcode, cuscode, type, code, GlobalInfo.sysPara.jygs, gz };

			String[] arg = { "mktcode", "track", "type", "code", "jygs", "gz" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(getMemCardHttp(CmdDef.GETGOODSEXCHANGE), line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, JfSaleRuleDef.ref);

				if (v.size() > 0)
				{
					String[] lines = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(jsrd, lines)) { return true; }
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			cmdHead = null;
			line = null;
		}

		return false;
	}

	public Vector findEWMCMPOPGoods(String rqsj, GoodsDef goods, String cardno, String cardtype, int cmd)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		Vector popvec = null;

		cmdHead = new CmdHead(cmd);

		String[] value = { cardno, cardtype, goods.code, goods.gz, goods.uid, goods.catid, goods.ppcode, goods.managemode == '\0' ? "" : String.valueOf(goods.managemode), rqsj };
		String[] arg = { "cardno", "cardtype", "code", "gz", "uid", "catid", "pp", "ruletype", "yhsj" };

		line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

		result = HttpCall(line, "");

		if (result == 0)
		{
			Vector v = new XmlParse(line.toString()).parseMeth(0, CmPopGoodsDef.ref);

			if (v.size() > 0)
			{
				popvec = new Vector();

				for (int i = 0; i < v.size(); i++)
				{
					String[] row = (String[]) v.elementAt(i);

					CmPopGoodsDef cg = new CmPopGoodsDef();

					if (Transition.ConvertToObject(cg, row))
					{
						// 分解活动档期信息
						cg.dqinfo = new CmPopTitleDef();
						String[] strdqinfo = cg.strdqinfo.split(",");
						if (!Transition.ConvertToObject(cg.dqinfo, strdqinfo))
							return null;

						// 分解档期规则信息
						cg.ruleinfo = new CmPopRuleDef();

						if (cg.strruleinfo.trim().equals(""))
							return null;
						String[] strruleinfo = cg.strruleinfo.split(",");
						if (!Transition.ConvertToObject(cg.ruleinfo, strruleinfo))
							return null;

						// 分解促销规则阶梯
						String[] strruleladders = cg.strruleladder.split(";");
						if (!cg.strruleladder.equals(""))
						{
							cg.ruleladder = new Vector();
							for (int j = 0; j < strruleladders.length; j++)
							{
								String strruleladder[] = strruleladders[j].split(",");
								CmPopRuleLadderDef cprld = new CmPopRuleLadderDef();
								if (!Transition.ConvertToObject(cprld, strruleladder))
									return null;

								cg.ruleladder.add(cprld);
							}
						}

						popvec.add(cg);
					}
				}

				return popvec;
			}

			return null;
		}

		return null;

	}

	public boolean findEWMGift(String ewm, Vector gift)
	{
		if (!GlobalInfo.isOnline)
			return false;

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { GlobalInfo.sysPara.mktcode, ewm };
		String[] args = { "mktcode", "coupon" };

		try
		{
			head = new CmdHead(CmdDef.WYT_FINDGIFT);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(CmdDef.WYT_FINDGIFT), line, "查找二维码礼品失败!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "code", "name", "qty", "price", "cost" });

				if (vi.size() > 0)
				{
					for (int i = 0; i < vi.size(); i++)
						gift.add(vi.get(i));
				}
				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	public boolean sendEWMGift(Jwyt_EwmGiftDef.EwmGiftHead giftHead, Vector giftDetail, Vector ret)
	{
		if (!GlobalInfo.isOnline)
			return false;

		Jwyt_EwmGiftDef.EwmGiftDetail gitfDetail = null;

		try
		{
			CmdHead aa = null;
			int result = -1;
			aa = new CmdHead(CmdDef.WYT_SENDGIFTBILL);

			// 单头打XML
			String line = Transition.ItemDetail(giftHead, Jwyt_EwmGiftDef.EwmGiftHead.ref);
			line = Transition.closeTable(line, "EwmGiftHeadDef", 1);

			// 小票明细
			String line1 = "";

			for (int i = 0; i < giftDetail.size(); i++)
			{
				gitfDetail = (Jwyt_EwmGiftDef.EwmGiftDetail) giftDetail.elementAt(i);
				line1 += Transition.ItemDetail(gitfDetail, Jwyt_EwmGiftDef.EwmGiftDetail.ref);
			}

			line1 = Transition.closeTable(line1, "EwmGiftDetailDef", giftDetail.size());

			String line2 = "";
			line2 = Transition.closeTable(Transition.ItemDetail(new String[] { GlobalInfo.syjStatus.syyh }, new String[] { "syyh" }), null, 1);

			// 合并
			line = Transition.getHeadXML(line + line1 + line2);

			StringBuffer line3 = new StringBuffer();
			line3.append(aa.headToString() + line);

			result = HttpCall(getMemCardHttp(CmdDef.WYT_SENDGIFTBILL), line3, "上传礼品领用单失败!");

			// 返回应答数据
			if (result == 0 && line3.toString().trim().length() > 0)
			{
				// 找第4个命令sendok过程的返回
				Vector v = new XmlParse(line3.toString()).parseMeth(2, new String[] { "retmemo" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (row != null && row.length>0 && row[0].trim().length() > 0)
					{
						ret.add(row[0]);
						return true;
					}
				}
			}
			return false;
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
	}

	public boolean getEWMDeductRule(String code, String coupon, Vector money)
	{
		if (!GlobalInfo.isOnline)
			return false;

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { GlobalInfo.sysPara.mktcode, code, coupon };
		String[] args = { "mktcode", "code", "coupon" };

		try
		{
			head = new CmdHead(CmdDef.WYT_FINDDEDUCT);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(CmdDef.WYT_FINDGIFT), line, "查找二维码礼品失败!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "money", "limitmoney" });

				if (vi.size() > 0)
				{
					String[] row = (String[]) vi.elementAt(0);
					money.add(row);

					return true;
				}
				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	public boolean sendEWMWorkLog(MarsSaleRet marsale)
	{
		return sendEWMWorkLog(String.valueOf(GlobalInfo.syjStatus.fphm), marsale);
	}

	public boolean sendEWMWorkLog(String sheetid, MarsSaleRet marsale)
	{
		if (!GlobalInfo.isOnline)
			return false;

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.WYT_SENDEWMLOG);
			line.append(head.headToString() + Transition.ConvertToXML(marsale, new String[][] { new String[] { "sheetid", sheetid } }));
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
}
