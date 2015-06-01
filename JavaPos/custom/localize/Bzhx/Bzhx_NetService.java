package custom.localize.Bzhx;

import java.util.Vector;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Cmls.Cmls_NetService;


public class Bzhx_NetService extends Cmls_NetService
{
	public boolean sendNewCustomer(CustomerDef cust, String track)
	{
		if (GlobalInfo.sysPara.iscardcode == 'Y')
		{
			int index = track.indexOf("=");

			if (index >= 0)
				track = track.substring(0, index);
		}

		return sendNewCustomer(getMemCardHttp(CmdDef.sendNewCustomer), cust, track);
	}

	public boolean sendNewCustomer(Http h, CustomerDef cust, String track)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, GlobalInfo.syjStatus.syjh,GlobalInfo.syjStatus.syyh,track };
		String[] args = {"mktcode", "jygs" ,"syjh","syyh", "track" };

		try
		{
			head = new CmdHead(CmdDef.sendNewCustomer);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(h, line, Language.apply("找不到该顾客卡信息!"));

			if (result == 0)
			{

				Vector v = new XmlParse(line.toString()).parseMeth(0, CustomerDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(cust, row)) { return true; }
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}

}
