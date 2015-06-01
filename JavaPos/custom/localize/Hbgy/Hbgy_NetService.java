package custom.localize.Hbgy;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_NetService;

public class Hbgy_NetService extends Bstd_NetService
{
	public boolean findRebateCard(String[] retinfo, int cardtype, String track)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { GlobalInfo.sysPara.mktcode, cardtype + "", track };
		String[] args = { "mktcode", "cardtype", "track" };

		try
		{
			head = new CmdHead(CmdDef.FINDREBATECARD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(CmdDef.FINDREBATECARD), line, "查找员工卡信息失败!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "cardno", "level", "rebate", "uplimit", "total", "prtinfo" });

				if (vi.size() > 0)
				{
					String[] row = (String[]) vi.elementAt(0);

					retinfo[0] = row[0];
					retinfo[1] = row[1];
					retinfo[2] = row[2];
					retinfo[3] = row[3];
					retinfo[4] = row[4];
					retinfo[5] = row[5];

				}
				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(getClass()).error(ex);
			return false;
		}
	}

	public boolean sendTotalAmount(SaleHeadDef saleHead, Vector saleGoods)
	{
		if (!GlobalInfo.isOnline)
			return false;

		int cardtype = -1;
		String cardno = "";
		double totalje = 0;

		if (saleHead.str3.length() > 1 || saleHead.str4.length() > 1)
		{
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);

				if (sgd.qtzke > 0 || sgd.qtzre > 0)
					totalje = totalje + sgd.num6;
			}
		}

		saleHead.num9 = ManipulatePrecision.doubleConvert(totalje, 2, 1);
		totalje = totalje + saleHead.num7;
		
		if (saleHead.str3.length() > 1)
		{
			cardtype = 0;
			cardno = saleHead.str3;
		}

		if (saleHead.str4.length() > 1)
		{
			cardtype = 1;
			cardno = saleHead.str4;
		}
		
		if(cardtype ==- 1)
			return true;

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { GlobalInfo.sysPara.mktcode, cardtype + "", cardno, totalje + "" };
		String[] args = { "mktcode", "cardtype", "cardno", "money" };

		try
		{
			head = new CmdHead(CmdDef.SENDTOTALAMOUNT);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(CmdDef.SENDTOTALAMOUNT), line, "同步员工卡销售金额失败!");

			if (result == 0)
				return true;
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(getClass()).error(ex);
			return false;
		}
	}

	public String getGwlInfo()
	{
		if (!GlobalInfo.isOnline)
			return null;

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.syjDef.syjh };
		String[] args = { "mktcode", "syjh" };

		try
		{
			head = new CmdHead(CmdDef.GETGWLINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(CmdDef.GETGWLINFO), line, "获取购物篮打印信息失败!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "info" });

				if (vi.size() > 0)
					return (String) vi.get(0);
			}
			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

}
