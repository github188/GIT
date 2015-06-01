package custom.localize.Ytbh;

import java.util.Vector;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Cmls.Cmls_NetService;



public class Ytbh_NetService extends Cmls_NetService
{
	public boolean sendMemberInfo(CustomerDef cust,String name, String track2, String cardID, String bonus, String validBonus, String certificateType, String certificate, String sex, String phone, String memberLevel, String address, String email)
	{	
		return sendMemberInfo(getMemCardHttp(CmdDef.FINDCUSTOMER), cust, name,track2,cardID,bonus,validBonus,certificateType,certificate,sex,phone,memberLevel,address,email);
	}
	public boolean sendMemberInfo(Http h, CustomerDef cust,String name, String track2, String cardID, String bonus, String validBonus, String certificateType, String certificate, String sex, String phone, String memberLevel, String address, String email)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, track2,name,cardID,bonus,validBonus,certificateType,certificate,sex,phone,memberLevel,address,email,""};
		String[] args = { "mktcode", "track", "name","cardID","bonus","validBonus","certificateType","certificate","sex","phone","memberLevel","address","email","memo"};

		try
		{
			head = new CmdHead(133);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
            System.out.println(line);
			result = HttpCall(h, line, "找不到该顾客卡信息!");
			System.out.println(result);
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
