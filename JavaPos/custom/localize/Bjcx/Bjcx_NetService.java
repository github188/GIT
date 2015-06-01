package custom.localize.Bjcx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Cmls.Cmls_NetService;

public class Bjcx_NetService extends Cmls_NetService
{
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
							GlobalInfo.sysPara.mktcode,
							GlobalInfo.sysPara.jygs,
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
							"mktcode",
							"jygs",
							"code",
							"gz",
							"catid",
							"ppcode",
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
		    
			String[] retname = {"pmbillno","addrule","Zklist","pmrule","zkmode","seq","zkfd","bz","maxnum"};
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
}
