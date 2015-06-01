package custom.localize.Jcgj;

import java.util.Vector;

import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Cmls.Cmls_NetService;

public class Jcgj_NetService extends Cmls_NetService
{
	public boolean getPfInfo(SaleHeadDef saleHead, double je, int cmdCode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, String.valueOf(saleHead.fphm), saleHead.syjh, String.valueOf(je) };
		String[] args = { "mktcode", "fphm", "syjh", "je" };
		try
		{
			head = new CmdHead(cmdCode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			//不显示错误信息
			result = HttpCall(line, "获取印花信息失败");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] {
																					"retmsg1",
																					"retmsg2"});

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (row.length == 2)
					{
						saleHead.buyerinfo = row[0] + ";" + row[1];
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
}
