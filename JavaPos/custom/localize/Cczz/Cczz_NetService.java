package custom.localize.Cczz;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;

import custom.localize.Bcrm.Bcrm_NetService;

public class Cczz_NetService extends Bcrm_NetService
{
	public boolean getCustomerSellJf(String[] row, String mktcode, String syjh, String fphm, String hykh, String hytype, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { mktcode, syjh, fphm, GlobalInfo.sysPara.jygs, hykh, hytype };
		String[] args = { "mktcode", "syjh", "fphm", "jygs", "hykh", "hytype" };

		try
		{
			head = new CmdHead(CmdDef.GETCUSTSELLJF);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(http, line, Language.apply("计算本笔交易小票积分失败\n请到会员中心查询积分!"));

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "curJf", "Jf","memo", "num1","cjf" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					row[0] = row1[0];
					row[1] = row1[1];
					row[2] = row1[2];

					if (row.length > 3 && row1.length > 3)
					{
						row[3] = row1[3];
					}
					row[4] = row1[4];
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
	
	public boolean javaGetCustXF(String[] row, String cardno, String syjh, String string)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { cardno};
		String[] args = { "cardno" };

		try
		{
			head = new CmdHead(CmdDef.GETCUSTXF);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(CmdDef.GETCUSTXF), line, "查询会员信息\n请到会员中心查询积分!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "yearxf", "monthxf", "str1", "str2", "str3", "str4", "num1", "num2", "num3", "num4" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					row[0] = row1[0];
					row[1] = row1[1];
					row[2] = row1[2];
					row[3] = row1[3];
					row[4] = row1[4];
					row[5] = row1[5];
					row[6] = row1[6];
					row[7] = row1[7];
					row[8] = row1[8];
					row[9] = row1[9];
					
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
			new MessageBox(ex.getMessage());
			PosLog.getLog(getClass()).error(ex);
			return false;
		}
	}
}
