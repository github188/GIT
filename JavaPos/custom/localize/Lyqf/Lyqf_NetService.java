package custom.localize.Lyqf;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_NetService;

public class Lyqf_NetService extends Bstd_NetService
{
	public boolean sendCustTotalAmount(SaleHeadDef salehead, double money)
	{

		if (!GlobalInfo.isOnline)
			return false;

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		String[] values = { GlobalInfo.sysPara.mktcode, salehead.syjh, String.valueOf(salehead.fphm), salehead.hykh, String.valueOf(salehead.ysje) };
		String[] args = { "mktcode", "syjh", "fphm", "custno", "total" };

		try
		{
			head = new CmdHead(CmdDef.SENDTOTALAMOUNT);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			if (HttpCall(getCardHttp(), line, "同步会员累计消费金额失败!") != 0)
				return false;

			return true;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
