package custom.localize.Cjmx;


import java.util.ArrayList;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsStockDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Cmls.Cmls_NetService;

public class Cjmx_NetService extends Cmls_NetService
{
	public boolean getGoodsStockList(ArrayList listgoods, int codetype, String txtCode)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			int result = -1;

			cmdHead = new CmdHead(CmdDef.GETGOODSSTOCK);

			String[] value = {GlobalInfo.sysPara.mktcode,"","",""};
			value[codetype+1] = txtCode;
			String[] arg = { "market", "goodsbigcode", "goodsid","goodsno" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, "未找到此商品");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsStockDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						GoodsStockDef gsd = new GoodsStockDef();

						if (Transition.ConvertToObject(gsd, row))
						{
							listgoods.add(gsd);
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
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			cmdHead = null;
			line = null;
		}
	}
	
	public boolean findBatchRule(SpareInfoDef sid, String code, String gz, String uid, String gys, String catid, String ppcode, String time, String cardno, String cardtype, String isfjk, String grouplist, String djlb, Http http)
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
		String[] values = {
							GlobalInfo.sysPara.jygs,
							GlobalInfo.sysPara.mktcode,
							code,
							gz,
							catid,
							ppcode,
							gys,
							time,
							cardno,
							cardtype,
							isfjk,
							grouplist
							};
		String[] args = {
							"jygs",
							"mkt",
							"code",
							"gz",
							"catid",
							"pp",
							"gys",
							"rqsj",
							"cardno",
							"cardtype",
							"isfjk",
							"grouplist"
							};

		try
		{
			head = new CmdHead(CmdDef.BatchRebate);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			//不显示错误信息
			result = HttpCall(http, line, "");
		    
			String[] retname = {"pmbillno","addrule","zklist","pmrule","zkmode","seq","zkfd","bz","maxnum"};
			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, retname);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);
					sid.pmbillno = row[0];
					sid.addrule = row[1];
					sid.Zklist = row[2];
					sid.pmrule = row[3];
					sid.etzkmode2 = row[4];
					sid.seq = row[5];
					sid.zkfd = row[6];
					sid.bz = row[7] != null?row[7]:"";
					sid.maxnum = Convert.toDouble(row[8]);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	
	public boolean getCustSaleList(Vector listgoods, String custCode)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			int result = -1;

			cmdHead = new CmdHead(CmdDef.GETCUSTSALEINFO);

			String[] value = {custCode};
			String[] arg = { "custcode"};

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, "未找到会员消费信息");
			//测试数据
			result = 0;
			String aa = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><table><row><gz>[2010167001]NONO AJ</gz><goodscode>00122023</goodscode><goodsname>T恤</goodsname><goodspp>[0001]ARMANIJEANS</goodspp><goodscc>[B3]M</goodscc><goodscolor>[J8]经典格</goodscolor><jj>[1]春</jj><sl>1</sl><sj>1160</sj><xssr>1044</xssr><zk>0</zk><rq>2014-04-18 16:58:43.0</rq></row></table></root>";
			if (result == 0)
			{
				String[] ref={"gz","goodscode","goodsname","goodspp","goodscc","goodscolor","jj","sl","sj","xssr","zk","rq"};
				//Vector v = new XmlParse(line.toString()).parseMeth(0, ref);
				Vector v = new XmlParse(aa).parseMeth(0, ref);
				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);
						listgoods.add(row);
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
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			cmdHead = null;
			line = null;
		}
	}
}
