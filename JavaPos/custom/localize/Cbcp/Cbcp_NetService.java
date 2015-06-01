package custom.localize.Cbcp;

import java.util.Iterator;
import java.util.Vector;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CheckGoodsDef;


public class Cbcp_NetService extends Cbcp_Crm_NetService//Bcrm_NetService
{
	 public static final int GETWCCRULES = 833;					//查询微信券的收券规则
	 
		
	 public boolean findWCCRules(String billno,Vector wccrules)
	 {
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			String[] values = { billno};

			String[] args = { "billno"};

			cmdHead = new CmdHead(GETWCCRULES);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "查询微信券收券规则数据失败!");
			
			Cbcp_WCCRuleDef rule;
			if (result == 0)
			{
				Vector data = new XmlParse(line.toString()).parseMeth(0, Cbcp_WCCRuleDef.ref);
				if (data.size() > 0)
				{
					for(Iterator it = data.iterator();it.hasNext();)
					{
						rule = new Cbcp_WCCRuleDef();
						String[] goodsline = (String [])it.next();
						if(Transition.ConvertToObject(rule, goodsline)) wccrules.add(rule);
					}
					return true;
				}
				
				return false;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	 }
	 
	public boolean sendCheckGoods(String djbh, CheckGoodsDef chkgd, StringBuffer checkgroupid, String checkcw, String checkrq, String isLastLine, String lineState)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			String[] values = { GlobalInfo.syjStatus.syjh, GlobalInfo.syjStatus.syyh, String.valueOf(chkgd.row), djbh, chkgd.code, chkgd.gz, chkgd.pdsl, chkgd.pdje, checkrq, lineState, chkgd.handInputcode };

			String[] args = { "syjh", "syyh", "rowno", "djbh", "code", "gz", "pdsl", "pdje", "pdrq", "oprtype", "inputcode" };

			cmdHead = new CmdHead(CmdDef.SENDCHECKGOODS);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "上传商品盘点数据失败!");

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "groupid" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					checkgroupid.append(row1[0]);
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
			return false;
		}
	}
}
