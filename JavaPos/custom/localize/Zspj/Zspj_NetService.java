package custom.localize.Zspj;

import java.io.FileWriter;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.CheckGoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

import custom.localize.Bstd.Bstd_NetService;

public class Zspj_NetService extends Bstd_NetService
{
	public boolean getMzkInfo(MzkRequestDef req, MzkResultDef ret)
	{
		if (req.track2.length() > 20)
		{
			// 集团卡
			return getMzkInfo(getCardHttp(), req, ret);
		}
		// 门店卡
		return getMzkInfo(GlobalInfo.localHttp, req, ret);
	}

	public int HttpCall(Http h, StringBuffer arg, String noMsg)
	{
		String[] lines = arg.toString().split(GlobalVar.divisionFlag1);
		String[] body = lines[0].split(GlobalVar.divisionFlag2);
		CmdHead head = new CmdHead(body);
		String cmdcode = head.getCmdCode();

		if (GlobalInfo.sysPara  !=null && GlobalInfo.sysPara.crmswitch=='Y')
		{
			if ((GlobalInfo.sysPara.cmdCustList != null) && (GlobalInfo.sysPara.cmdCustList.trim().length() > 0))
			{
				//若存在该命令，且参数为Y，则直接返回不继续查找
				String cmdlist = "," + GlobalInfo.sysPara.cmdCustList +",";
				if (CmdDef.GETSERVERTIME != Integer.parseInt(cmdcode) && cmdlist.indexOf(String.valueOf("," + cmdcode + ",")) != -1)
				{
					new MessageBox("系统与CRM通讯已关闭,该功能暂时无法使用");
					return -1;
				}
					
			}
		}

		return super.HttpCall(h, arg, noMsg);
	}

	public boolean sendGongMaoMzkLog(MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		// ：机号，小票号，卡号，操作时间，金额，操作员，具体操作，标志，备注等。
		String[] values = { req.syjh, String.valueOf(req.fphm), req.track2, req.type, 
				String.valueOf(req.je),String.valueOf(ret.ye), req.str1,req.syyh, req.str2, req.memo };
		
		String[] args = { "syjh", "fphm", "cardno","type",  "je", "ye","oprdate","operuser","flag", "memo" };

		try
		{
			aa = new CmdHead(CmdDef.SENDGONGMAOMZKLOG);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "发送工贸面值卡日志失败!");

			if (result == 0)
				return true;

			return false;
		}

		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	// 取面值卡请求值
	private String getMzkReq(MzkRequestDef req)
	{
		String strreq = "";
		try
		{
			writeMzkLog("(MZK_IN) getMzkReq start");
			strreq = "type=[" + req.type + getMzkTypeStr(req.type) + "], " + "fphm=[" + req.fphm + "], " + "je=[" + req.je + "], " + "paycode=[" + req.paycode + "], " + "seqno=[" + req.seqno + "], " + "termno=[" + req.termno + "], " + "track2=[" + req.track2 + "], " + "syjh=[" + req.syjh + "], " + "syyh=[" + req.syyh + "].";
			writeMzkLog("strreq[" + strreq + "]");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return strreq;
	}

	// 取面值卡返回值
	private String getMzkRet(MzkResultDef ret)
	{
		String strret = "";
		try
		{
			strret = "cardname=[" + ret.cardname + "], " + "cardno=[" + ret.cardno + "], " + "func=[" + ret.func + "], " + "memo=[" + ret.memo + "], " + "money=[" + ret.money + "], " + "ye=[" + ret.ye + "], " + "status=[" + ret.status + "].";
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return strret;
	}

	// 取面值消费类型
	private String getMzkTypeStr(String strType)
	{
		String str = "";
		try
		{
			// 交易类型,'01'-消费,'02'-消费冲正,'03'-退货,'04'-退货冲正,'05'-查询,'06'-冻结
			if (strType.trim().equals("01"))
			{
				str = "消费";
			}
			else if (strType.trim().equals("02"))
			{
				str = "消费冲正";
			}
			else if (strType.trim().equals("03"))
			{
				str = "退货";
			}
			else if (strType.trim().equals("04"))
			{
				str = "退货冲正";
			}
			else if (strType.trim().equals("05"))
			{
				str = "查询";
			}
			else if (strType.trim().equals("06"))
			{
				str = "冻结";
			}
			else
			{
				str = "未知类型";
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return str;
	}

	public boolean sendMzkSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		// if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.SENDMZK);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));
			writeMzkLog("sendMzkSale.line=[" + line.toString() + "].");
			result = HttpCall(h, line, "储值卡交易失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MzkResultDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(ret, row)) { return true; }
				}
			}
			else
			{
				writeMzkLog("sendMzkSale.result=[" + String.valueOf(result) + "].");
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		boolean blnret = false;
		try
		{
			// String strcontent = getMzkReq(req);
			getMzkReq(req);
			// 记录:当为冲正时,TRACK2值(监测这个值是否丢失)
			if (req.type.equals("02") || req.type.equals("05"))
			{
				writeMzkLog("type=[" + req.type + "],track2=[" + req.track2 + "]");
			}
			if (req.track2 == null)
				req.track2 = req.track2.toString();
			if (req.track2.length() > 20)
			{
				// req.je = req.je * 100;
				// writeMzkLog("(JTK_IN)," + strcontent);
				// 集团卡
				blnret = sendMzkSale(getCardHttp(), req, ret);
				/*
				 * //测试数据 blnret=true; ret.cardname="***"; ret.cardno
				 * ="9912345678901234"; ret.cardpwd = "123"; ret.ispw = 'N';
				 * ret.money = 100; ret.ye = 500;
				 */

			}
			else
			{
				// writeMzkLog("(MDK_IN)," + strcontent);
				// 门店卡
				blnret = sendMzkSale(GlobalInfo.localHttp, req, ret);
			}
			if (blnret)
			{
				if (ret.cardno == null || ret.cardno.trim().length() <= 0)
				{
					writeMzkLog("过程返回成功,但cardno返回为空.");
					blnret = false;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			writeMzkLog("MZK_OUT=[" + (blnret == true ? 0 : 1) + "]," + getMzkRet(ret));
		}
		return blnret;
	}

	// 记录日志（追加）
	private void writeLog(String fileName, String content)
	{
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(fileName, true);
			writer.write("[" + ManipulateDateTime.getCurrentTime() + "] " + content + "\n");
			writer.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}
	}

	// 面值卡日志
	private void writeMzkLog(String content)
	{
		writeLog(ConfigClass.LocalDBPath + "\\Invoice\\" + new ManipulateDateTime().getDateByEmpty() + "\\MZK" + new ManipulateDateTime().getDateByEmpty() + ".log", content);
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

	public boolean sendCheckData(String gz, String pdrq)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { gz, pdrq, GlobalInfo.syjDef.syjh };
		String[] args = { "gz", "pdrq", "syjh" };

		try
		{
			head = new CmdHead(CmdDef.SENDCHKINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "验证盘点柜组和日期失败");

			if (result == 0)
			{
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

	public boolean getOldCheckInfo(Vector v, String djbh, String gz, String pdrq, String md, int cmdCode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { djbh, gz, pdrq, GlobalInfo.syjDef.syjh };
		String[] args = { "djbh", "gz", "pdrq", "syjh" };

		try
		{
			head = new CmdHead(cmdCode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "查找盘点单失败");

			if (result == 0)
			{
				Vector v1 = new XmlParse(line.toString()).parseMeth(0, new String[] { "djbh", "gz", "md", "pdrq", "syyh", "syjh", "rowno", "code", "name", "unit", "bzhl", "uid", "pdsl", "pdje", "retcode", "retmsg", "editflag", "newflag", "totalrow" });

				for (int i = 0; i < v1.size(); i++)
				{
					v.add(v1.get(i));
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

	public boolean sendJfToPos(long fphm, double ljjf, double bcjf, double bxjf, String hykh)
	{
		if (!GlobalInfo.isOnline)
			return false;

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { GlobalInfo.syjDef.syjh, String.valueOf(fphm), String.valueOf(ljjf), String.valueOf(bcjf), String.valueOf(bxjf), hykh };
		String[] args = { "syjh", "fphm", "ljjf", "bcjf", "bxjf", "hykh" };

		try
		{
			head = new CmdHead(CmdDef.SENDJFTOPOS);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "同步POS库积分失败");

			if (result == 0)
				return true;

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz, String uid, String rulecode, String catid, String ppcode, String time, String cardno, String cardtype, Http http)
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
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype };
		String[] args = { "mktcode", "jygs", "code", "gz", "uid", "rule", "catid", "ppcode", "rqsj", "cardno", "cardtype" };

		try
		{
			head = new CmdHead(CmdDef.FINDCRMPOP);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(http, line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsPopDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(popDef, row)) { return true; }
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}

	/*
	 * public int findVIPMaxSl(GoodsPopDef goods , String custcode, String
	 * custtype, String code, String gz, String uid) { //GoodsPopDef gd= new
	 * GoodsPopDef();
	 * 
	 * try { findPopRuleCRM(goods, code, gz, uid, null, null, null,
	 * ManipulateDateTime.getCurrentDateTime(), custcode, custtype,
	 * NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP));
	 * 
	 * if (goods.jsrq !=null && goods.jsrq.length()>0) { String[] retString =
	 * goods.jsrq.split(","); return Integer.parseInt(retString[2].trim()) -
	 * Integer.parseInt(retString[3].trim()); } return 0; } catch (Exception ex) {
	 * ex.printStackTrace(); return 0; } }
	 */
}
