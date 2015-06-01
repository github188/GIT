package custom.localize.Njxb;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Cmls.Cmls_NetService;

public class Njxb_NetService extends Cmls_NetService
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
	
	public boolean getCustomerSellJf(String[] row, String mktcode, String syjh, String fphm, String hykh, String hytype, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { mktcode, syjh, fphm, GlobalInfo.sysPara.jygs, hykh, hytype };
		String[] args = { "mktcode", "syjh", "fphm", "jygs", "hykh", "hytype" };

		try
		{
			head = new CmdHead(CmdDef.GETCUSTSELLJF);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(http, line, Language.apply("计算本笔交易小票积分失败\n请到会员中心查询积分!"));

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "curJf", "Jf","memo", "num1","cjf" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					row[0] = row1[0];
					row[1] = row1[1];
					row[2] = row1[2];

					if (row.length > 3 && row1.length > 3)
					{
						row[3] = row1[3];
					}
					row[4] = row1[4];
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
	
	public boolean checkCouponSaleLimit(String pid, String ruleId, String cardNo, Vector limitList)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { pid, ruleId, cardNo };
		String[] args = { "pid", "ruleId", "cardNo" };

		try
		{
			// 检查买券上限信息
			head = new CmdHead(CmdDef.CHECKCOUPONSALELIMIT);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("检查买券上限信息失败!");
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "unitqje", "maxtimes", "maxmoney" });

			String[] row = (String[]) v.elementAt(0);
			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);
				limitList.add(row);
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;
			return true;
		}
		catch (Exception ex)
		{
			if (limitList != null)
			{
				limitList.clear();
				limitList = null;
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
	
	//查询卷
	public boolean sendFjkSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		
		//获取阿里会员卡号
		if(null !=req.track2 &&!"".equals(req.track2)&&req.track2.split(":").length>=3)
		{
			String value = req.track2.split(":")[2];
			req.track2 = value.substring(1, value.length());
		}
		
			
		

		try
		{
			head = new CmdHead(CmdDef.SENDFJK);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, Language.apply("返券卡交易失败!!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MzkResultDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(ret, row)) { return true; }
				}
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
		}

		return false;
	}
}
