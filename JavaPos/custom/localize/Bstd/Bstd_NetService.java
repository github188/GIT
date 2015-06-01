package custom.localize.Bstd;

import java.util.ArrayList;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CmPopGiftsDef;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.CmPopRuleDef;
import com.efuture.javaPos.Struct.CmPopRuleLadderDef;
import com.efuture.javaPos.Struct.CmPopTitleDef;
import com.efuture.javaPos.Struct.GoodsBarcodeDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;

public class Bstd_NetService extends NetService
{
	public boolean findU51PopBillNo(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String ppcode, String spec, String time, String yhtime, String cardno, Http http, int cmdcode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, code, gz, catid, ppcode, spec, time, yhtime, cardno };
		String[] args = { "mktcode", "code", "gz", "catid", "ppcode", "spec", "time", "yhtime", "cardno" };

		try
		{
			head = new CmdHead(cmdcode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "查找超市促销规则单号失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, SuperMarketPopRuleDef.ref);

				if (v.size() > 0)
				{
					String[] lines = (String[]) v.elementAt(0);
					if (Transition.ConvertToObject(ruleDef, lines)) { return true; }
				}
				else
				{
					return false;
				}

				//
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(getClass()).error(er);
			return false;
		}
	}

	// 查找超市促销规则
	public boolean findU51PopRule(Vector ruleReqList, Vector rulePopList, SuperMarketPopRuleDef popRule, Http http, int cmdcode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		//M:CmdHead类用来管理包头
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
	
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, popRule.djbh };//xml中 的value
		String[] args = { "mktcode", "jygs", "billno" };//xml中 的key

		try
		{
			head = new CmdHead(cmdcode);

			line.append(head.headToString() + Transition.SimpleXML(values, args));
			
	
			result = HttpCall(line, "查找超市促销规则失败!");

			if (result == 0)
			{
				
				Vector v = new XmlParse(line.toString()).parseMeth(0, SuperMarketPopRuleDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);
						SuperMarketPopRuleDef retRule = new SuperMarketPopRuleDef();

						if (Transition.ConvertToObject(retRule, row))
						{
							if (retRule.yhdjlb == '8')
							{
								// 规则条件
								ruleReqList.add(retRule);
							}
							else
							{
								// 规则结果
								rulePopList.add(retRule);
							}
						}
						else
						{
							return false;
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
		catch (Exception er)
		{
			er.printStackTrace();
			PosLog.getLog(getClass()).error(er);
			return false;
		}
	}
	// 一条码对多编码处理
	public boolean findGoodsBarcodeList(String code, ArrayList codelist)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			int result = -1;

			cmdHead = new CmdHead(CmdDef.GETGOODSBARCODELIST);

			String[] value = { code };
			String[] arg = { "code" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, "未找到商品!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsBarcodeDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						GoodsBarcodeDef cc = new GoodsBarcodeDef();

						if (Transition.ConvertToObject(cc, row))
						{
							codelist.add(cc);
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

	public boolean checkInvoiceHC(String fphm, String syjh)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			int result = -1;

			cmdHead = new CmdHead(CmdDef.CHECKINVOHC);

			String[] value = { fphm, syjh };
			String[] arg = { "fphm", "syjh" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, "未找到此小票!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "value" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if ((row.length <= 0) || (row[0] == null)) { return false; }

					if (row[0].equals("Y")) { return true; }
				}
			}

			return false;
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

	public Vector findCMPOPGoods(String rqsj, GoodsDef goods, String cardno, String cardtype, int cmd)
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

	public Vector findCMPOPGoods(String rqsj, GoodsDef goods, String cardno, String cardtype)
	{
		return findCMPOPGoods(rqsj, goods, cardno, cardtype, CmdDef.FINDGOODSCMPOP);
	}

	public Vector findCMPOPGroup(String dqid, String ruleid, int group, int cmd)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		Vector popvec = null;

		cmdHead = new CmdHead(cmd);

		String[] value = { dqid, ruleid, String.valueOf(group) };
		String[] arg = { "dqid", "ruleid", "group" };

		line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

		result = HttpCall(line, "");

		if (result == 0)
		{
			// 与本地结构相同
			Vector v = new XmlParse(line.toString()).parseMeth(0, CmPopGoodsDef.refLocal);

			if (v.size() > 0)
			{
				popvec = new Vector();

				for (int i = 0; i < v.size(); i++)
				{
					String[] row = (String[]) v.elementAt(i);

					CmPopGoodsDef cg = new CmPopGoodsDef();

					if (Transition.ConvertToObject(cg, row, CmPopGoodsDef.refLocal))
					{
						popvec.add(cg);
					}
				}

				return popvec;
			}
		}

		return null;

	}

	public Vector findCMPOPGroup(String dqid, String ruleid, int group)
	{
		return findCMPOPGroup(dqid, ruleid, group, CmdDef.FINDPOPGROUP);
	}

	public Vector findCMPOPGift(String dqid, String ruleid, String ladderid,int cmd)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		Vector popvec = null;

		//cmdHead = new CmdHead(CmdDef.FINDPOPGIFT);
		cmdHead = new CmdHead(cmd);

		String[] value = { dqid, ruleid, ladderid };
		String[] arg = { "dqid", "ruleid", "ladderid" };

		line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

		result = HttpCall(line, "");

		if (result == 0)
		{
			Vector v = new XmlParse(line.toString()).parseMeth(0, CmPopGiftsDef.ref);

			if (v.size() > 0)
			{
				popvec = new Vector();

				for (int i = 0; i < v.size(); i++)
				{
					String[] row = (String[]) v.elementAt(i);

					CmPopGiftsDef cg = new CmPopGiftsDef();

					if (Transition.ConvertToObject(cg, row))
					{
						popvec.add(cg);
					}
				}

				return popvec;
			}
		}

		return null;
	}

	public Vector getYhList(String rqsj, GoodsDef goods, String cardno, String cardtype)
	{
		return findCMPOPGoods(rqsj, goods, cardno, cardtype);
	}

	// 查找商品是否存在换购规则
	public boolean getJfExchangeGoods(JfSaleRuleDef jsrd, String code, String cuscode, String type)
	{
		if (!GlobalInfo.isOnline)
			return false;

		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.GETGOODSEXCHANGE);

			String[] value = { GlobalInfo.sysPara.mktcode, cuscode, type, code, "" };

			String[] arg = { "mktcode", "track", "type", "code", "gz" };

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
}
