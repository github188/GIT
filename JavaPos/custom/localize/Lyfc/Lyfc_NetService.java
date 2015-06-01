package custom.localize.Lyfc;

import java.util.Vector;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_NetService;

public class Lyfc_NetService extends Bstd_NetService
{
	public boolean findStampGoods(String cutpricecode, String[] row)
	{
		if (!GlobalInfo.isOnline)
			return false;

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { cutpricecode };
		String[] args = { "cutpricecode" };

		try
		{
			head = new CmdHead(CmdDef.GETSTAMPGOODS);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "goodscode", "goodsprice", "qtyflag", "qty" });

				if (v.size() > 0)
				{
					String[] tmp = (String[]) v.elementAt(0);

					if (tmp.length > 1)
					{
						row[0] = tmp[0];
						row[1] = tmp[1];
						row[2] = tmp[2];
						row[3] = tmp[3];
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
}
