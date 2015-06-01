package custom.localize.Jdhx;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_NetService;

public class Jdhx_NetService extends Bstd_NetService
{
	public boolean getGroupBuyInfo(String billno, String djlb, Vector saleDetailList)
	{
		if (!GlobalInfo.isOnline)
		{
			new MessageBox("团购业务必须在联网状态下进行!");
			return false;
		}

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String type = "1";

		if (SellType.ISBACK(djlb))
			type = "2";

		String[] values = { GlobalInfo.sysPara == null ? "" : GlobalInfo.sysPara.mktcode, billno, type };
		String[] args = { "mktcode", "billno", "type" };

		try
		{
			// 查询团购信息
			head = new CmdHead(CmdDef.GETGROUPBUYINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("团购小票明细查询失败!"));
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到团购小票明细,团购小票不存在!"));
				return false;
			}

			String[] row = (String[]) v.elementAt(0);
			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				SaleGoodsDef sgd = new SaleGoodsDef();

				if (Transition.ConvertToObject(sgd, row))
				{
					saleDetailList.add(sgd);
				}
				else
				{
					saleDetailList.clear();
					saleDetailList = null;
					return false;
				}
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;
			return true;
		}
		catch (Exception ex)
		{
			if (saleDetailList != null)
			{
				saleDetailList.clear();
				saleDetailList = null;
			}
			ex.printStackTrace();
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
	}

	// 绑定会员和水费号
	public boolean waterFeebindCustomer(String custno, String yhh)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		try
		{
			if (!GlobalInfo.isOnline)
				return false;

			cmdHead = new CmdHead(CmdDef.GETCUSTXF);

			String[] value = { custno, yhh };
			String[] arg = { "custno", "yhh" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			return HttpCall(line, "水费用户号绑定会员卡号失败!") == 0 ? true : false;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}
	
	
	public boolean getU51Info(String posId, String mkt, SaleHeadDef saleHead)
	{
		CmdHead cmdHead = null;
		int result = -1;
		StringBuffer line = new StringBuffer();

		try
		{

			//这个命令用来获取促销信息
			cmdHead = new CmdHead(CmdDef.FINDGOODEWMSCMPOP);

			String[] value = { mkt};
			String[] arg = {"mkt"};

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, "");

			if (result != 0)
			{
//				new MessageBox(Language.apply("获取促销信息失败！"));
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, new String[]{"info"});

			if (v.size() >= 1)
			{
				String[] row = (String[]) v.get(0);
				String info = row[0];
				//将后台促销信息记录在 str5 备用字段
				saleHead.str5 = info.trim();
				
				return true;
			}
			else
			{
				new MessageBox("获取的促销信息有误!");
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
}
