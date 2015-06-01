package custom.localize.Bszm;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_NetService;

public class Bszm_NetService extends Bstd_NetService
{
	public boolean getSellRealFQ(String[] row, String mktcode, String syjh, String fphm, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { mktcode, syjh, fphm };
		String[] args = { "mktcode", "syjh", "fphm" };

		try
		{
			head = new CmdHead(CmdDef.GETSELLREALFQ);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(http, line, "计算本笔交易小票返券失败");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "info","limitje" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					row[0] = row1[0];
					row[1] = row1[1];
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
	
	public boolean getCustomerSellJf(String[] curJf, String mktcode, String hykh, String ysje,String goods,String pay)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { mktcode,hykh,ysje,"",goods,pay};
		String[] args = { "mktcode","hykh","ysje","space","goods","pay"};

		try
		{
			head = new CmdHead(CmdDef.GETCUSTSELLJF);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "计算本笔交易小票积分失败\n请到会员中心查询积分!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "curJf" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);
					
					if (row1 !=null && row1.length>0)
						curJf[0] = row1[0];
	
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
	public boolean sendPopCoupon(String fphm, String cardlist)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

	
		String[] values = { GlobalInfo.sysPara.mktcode, fphm,cardlist };
		String[] args = { "mktcode", "fphm", "couponno"};

		try
		{
			head = new CmdHead(CmdDef.SENDCOUPONLIST);
			line.append(head.headToString() +

			Transition.SimpleXML(values, args));

			result = HttpCall(line, "活动券上传失败");

			// 返回0表示存在于黑名单中
			if (result == 0) return true;
			else return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
}
