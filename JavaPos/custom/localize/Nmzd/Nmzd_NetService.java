package custom.localize.Nmzd;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;


import custom.localize.Cmls.Cmls_NetService;

public class Nmzd_NetService extends Cmls_NetService
{
	public boolean FINDMOBILE2CARD(Vector v,String phoneno)
	{
		if (!GlobalInfo.isOnline)
		{
			new MessageBox("脱网情况下不能使用手机查询会员卡");
			return false;
		}
		
		if (phoneno == null)
		{
			return false;
		}



		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = {
							GlobalInfo.sysPara.mktcode,
							GlobalInfo.sysPara.jygs,
							phoneno };
		String[] args = {
							"mktcode",
							"jygs",
							"phoneno" };

		try
		{
			head = new CmdHead(CmdDef.java_FINDMOBILE2CARD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			//不显示错误信息
			result = HttpCall(NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP), line, "通过手机查询会员卡清单失败");

			if (result == 0)
			{
				Vector v1 = new XmlParse(line.toString()).parseMeth(0, new String[]{"cardno","cardname","curjf","curxf","type"});

				if (v1.size() > 0)
				{
					v.addAll(v1);
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
