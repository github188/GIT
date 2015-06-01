package custom.localize.Zsbh;

import java.io.FileWriter;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_NetService;
import custom.localize.Gzbh.Gzbh_AccessBaseDB;

public class Zsbh_NetService extends Bcrm_NetService
{
	// 判断是否CRM命令
	private boolean isSendCrm(String cmdcode)
	{
		try
		{
			// 前提条件判断syspara!=null, cmdCustList 有值
			if (GlobalInfo.sysPara != null && GlobalInfo.sysPara.cmdCustList != null && (GlobalInfo.sysPara.cmdCustList.trim().length() > 0))
			{
				String cmdlist = "," + GlobalInfo.sysPara.cmdCustList + ",";
				if (CmdDef.GETSERVERTIME != Integer.parseInt(cmdcode) && (cmdlist.indexOf(String.valueOf("," + cmdcode + "|")) != -1) || cmdlist.indexOf(String.valueOf("," + cmdcode + ",")) != -1)
					return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(cmdcode + "查找匹配项异常");
			return false;
		}
	}

	private String[] getCmdAry(String cmdcode, boolean flag)
	{
		String cmdlist = null;
		try
		{
			if (flag)
				cmdlist = "," + GlobalInfo.sysPara.cmdCustList + ",";
			else
				cmdlist = "," + GlobalInfo.sysPara.showerrorcmd + ",";

			if (cmdlist.indexOf(String.valueOf("," + cmdcode + "|")) < 0)
				return null;

			if (flag)
				return GlobalInfo.sysPara.cmdCustList.split(",");
			else
				return GlobalInfo.sysPara.showerrorcmd.split(",");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	private int getSender(String[] para, String cmdcode, boolean flag)
	{
		try
		{
			// 若是CRM命令，且禁用了CRM通讯功能则返回,放过获取系统参数的命令
			if (flag && GlobalInfo.sysPara.crmswitch == 'Y' && !cmdcode.equals("812"))
			{
				new MessageBox("系统与CRM通讯已关闭,该功能暂时无法使用");
				return 5;
			}

			// 对于旧参数，para始终为null
			if (flag && para == null)
			{
				// 兼容之前的命令
				String cmdlist = "," + GlobalInfo.sysPara.cmdCustList + ",";
				// 存在旧参，则直接返回1，让其发往会员POSSERVER
				if (cmdlist.indexOf(String.valueOf("," + cmdcode + ",")) != -1)
					return 1;

				return 0;
			}

			for (int i = 0; i < para.length; i++)
			{
				if (para[i].indexOf(cmdcode + "|") != -1)
				{
					String[] tmpItem = para[i].split("\\|");
					if (tmpItem.length > 1)
					{
						// 发送CRM
						if (tmpItem[1].equals("C"))
							return 1;
						else if (tmpItem[1].equals("P")) // 发送至POS库
							return 2;
						else if (tmpItem[1].equals("S")) // CRM失败再发POS库
							return 3;
						else if (tmpItem[1].equals("D"))
							return 4;
						else
							break;// 跳出直接采用CRM发送
					}
					else
						break;
				}
			}
			return 0;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	private int sendCrmOrPos(String cmdcode)
	{
		return getSender(getCmdAry(cmdcode, true), cmdcode, true);
	}

	private int showError(String cmdcode)
	{
		return getSender(getCmdAry(cmdcode, false), cmdcode, true);
	}

	/*
	 * public int sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector
	 * salePayment, Vector retValue) { if (SellType.ISBACK(saleHead.djlb))
	 * isRefund = true;
	 * 
	 * int ret = sendSaleData(saleHead, saleGoods, salePayment, retValue,
	 * getMemCardHttp(CmdDef.SENDCRMSELL), CmdDef.SENDCRMSELL);
	 * 
	 * isRefund = false;
	 * 
	 * return ret; }
	 */

	// CRM促销门店化
	// 参数形式: ,23|C,10|C,114|C
	public Http getMemCardHttp(int cmdcode)
	{
		if ((GlobalInfo.sysPara.memcardsvrurl == null) || (GlobalInfo.sysPara.memcardsvrurl.trim().length() <= 0))
		{
			return GlobalInfo.localHttp;
		}
		else
		{
			if ((GlobalInfo.sysPara.cmdCustList != null) && (GlobalInfo.sysPara.cmdCustList.trim().length() > 0))
			{
				String cmdlist = "," + GlobalInfo.sysPara.cmdCustList + ",";
				// 先检查新参，在检查旧参
			    if (cmdlist.indexOf(String.valueOf("," + cmdcode + "|")) < 0)
				    if (cmdlist.indexOf(String.valueOf("," + cmdcode + ",")) < 0)
						return GlobalInfo.localHttp;
			}

			// 解析URL
			String url = GlobalInfo.sysPara.memcardsvrurl;
			boolean bool = false;

			if (url.split("\\|").length >= 2)
			{
				String urls[] = url.split("\\|");

				String cmdcodes[] = urls[1].split(",");

				for (int i = 0; i < cmdcodes.length; i++)
				{
					if (Integer.parseInt(cmdcodes[i]) == cmdcode)
					{
						bool = true;
						break;
					}
				}

				if (bool)
				{
					bool = false;

					if (urls[0].equals("MZKURL")) { return getCardHttp(); }
				}
				else
				{
					return GlobalInfo.localHttp;
				}
			}
			else if (url.equals("MZKURL") && (cmdcode == CmdDef.FINDCUSTOMER || cmdcode == CmdDef.SENDHYK)) { return getCardHttp(); }

			if (url.trim().indexOf("LOCALLOOP:") == 0)
			{
				url = "Http://" + ConfigClass.ServerIP.trim() + ":" + ConfigClass.ServerPort + url.substring(10).trim();
			}
			else
			{
				String[] ipaddr = GlobalInfo.sysPara.memcardsvrurl.split(",");
				for (int i = 0; ipaddr.length >= 2 && i < ipaddr.length; i++)
				{
					if (ipaddr[i].substring(0, ipaddr[i].indexOf("-")).trim().equals(ConfigClass.ServerIP.trim()))
					{
						url = ipaddr[i].substring(ipaddr[i].indexOf("-") + 1);
						break;
					}
				}
			}

			// 发送请求到独立卡服务器
			if (GlobalInfo.memcardHttp == null || (GlobalInfo.memcardHttp != null && !GlobalInfo.memcardHttp.isSameHttp(url)))
			{
				GlobalInfo.memcardHttp = new Http(url);
				GlobalInfo.memcardHttp.init();
				GlobalInfo.memcardHttp.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
				GlobalInfo.memcardHttp.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
			}

			return GlobalInfo.memcardHttp;
		}
	}

	public int HttpCall(Http h, StringBuffer arg, String noMsg)
	{
		String[] lines = arg.toString().split(GlobalVar.divisionFlag1);
		String[] body = lines[0].split(GlobalVar.divisionFlag2);
		CmdHead head = new CmdHead(body);
		String cmdcode = head.getCmdCode();

		// 首先判断是否为CRM命令
		if (isSendCrm(cmdcode))
		{
			// 然后再判断该命令发往何处
			int sender = sendCrmOrPos(cmdcode);
			
			if (sender == 1 && GlobalInfo.memcardHttp != null)
			{
				return HttpCall(GlobalInfo.memcardHttp, arg, noMsg, false);
			}
			else if (sender == 2 && GlobalInfo.localHttp != null)
			{
				return HttpCall(GlobalInfo.localHttp, arg, noMsg, false);
			}
			else if (sender == 3 && (GlobalInfo.memcardHttp != null || GlobalInfo.localHttp != null))
			{
				StringBuffer posArg = new StringBuffer();
				posArg.append(arg.toString());

				int retValue = HttpCall(GlobalInfo.memcardHttp, arg, noMsg, false);

				if (retValue != 0)
				{
					retValue = HttpCall(GlobalInfo.localHttp, posArg, noMsg, false);

					arg.delete(0, arg.length());
					arg.append(posArg.toString());
				}

				return retValue;
			}
			else if (sender == 4 && (GlobalInfo.memcardHttp != null || GlobalInfo.localHttp != null))
			{
				// CRM ,POS两者均发，若有一方失败，则认为失败，方便之后的小票重传
				StringBuffer posArg = new StringBuffer();
				posArg.append(arg.toString());

				int crmRet = -1;
				int posRet = -1;

				crmRet = HttpCall(GlobalInfo.memcardHttp, arg, noMsg, false);
				if (crmRet != 0 && crmRet != 2)
					new MessageBox("小票发送至总部CRM失败，请稍候在当日小票列表中选择重传");
				
				posRet = HttpCall(GlobalInfo.localHttp, posArg, noMsg, false);
				if (posRet != 0 && posRet != 2)
					new MessageBox("小票发送至门店CRM失败，请稍候在当日小票列表中选择重传");

				arg.delete(0, arg.length());
				arg.append(posArg.toString());

				// 45号命令若有一个失败，则认为失败
				if (cmdcode.equals(String.valueOf(CmdDef.SENDCRMSELL)))
				{
					if (crmRet == 1 || posRet == 1)
						return -1;

					// 其他情况则认为成功
					return 0;
				}

				if (crmRet == 0 && posRet == 0)
					return 0;
				else
					return -1;
			}
			else if (sender == 5)
				return -1;
		}

		// 若不是CRM命令，则采用默认传入的h发送
		return HttpCall(h, arg, noMsg, false);
	}

	// =======================================================================

	// 查找会员卡
	public boolean getCustomer(Http h, CustomerDef cust, String track)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, track };
		String[] args = { "mktcode", "jygs", "track" };

		try
		{
			head = new CmdHead(CmdDef.FINDCUSTOMER);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(h, line, "找不到该顾客卡信息!");

			if (result == 0)
			{

				Vector v = new XmlParse(line.toString()).parseMeth(0, CustomerDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(cust, row)) { return true; }
				}
			}
			// 连接crm服务器失败进行脱网查找
			else if (result == -1)
			{
				if (Gzbh_AccessBaseDB.getDefault().getCustomer(cust, track)) { return true; }
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
				// 测试数据
				// blnret=true;
				// ret.cardname="***";
				// ret.cardno ="9912345678901234";
				// ret.cardpwd = "123";
				// ret.ispw = 'N';
				// ret.money = 100;
				// ret.ye = 500;

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

	public int HttpCall(Http h, StringBuffer arg, String noMsg, boolean append)
	{
		// 参数长度为4K倍数时，POSSERVER读到为空串
		if (arg.length() % 4096 == 0)
			arg.insert(0, '0');

		// 参数长度为12116时, POSSERVER读到为空串
		if (arg.length() == 12116)
			arg.insert(0, '0');

		// 根据配置文件长度时,POSSERVER读到为空串
		if (ConfigClass.httpErrorLength != null && ConfigClass.httpErrorLength.length > 0)
		{
			for (int i = 0; i < ConfigClass.httpErrorLength.length; i++)
			{
				if (Integer.parseInt(ConfigClass.httpErrorLength[i].trim()) == arg.length())
				{
					arg.insert(0, '0');
					break;
				}
			}
		}

		// 递归调用加入长度
		if (append)
			arg.insert(0, '0');

		// 替换掉换行符
		while (arg.indexOf("\n") >= 0)
		{
			arg.setCharAt(arg.indexOf("\n"), ' ');
		}

		int arglen = arg.length();

		// 设置请求命令
		h.setRequestString(arg.toString());

		// 显示命令请求描述
		String cmdcode = "";
		if (h != GlobalInfo.timeHttp)
		{
			// 解析请求包头
			String[] lines = arg.toString().split(GlobalVar.divisionFlag1);
			String[] body = lines[0].split(GlobalVar.divisionFlag2);
			CmdHead head = new CmdHead(body);
			cmdcode = head.getCmdCode();

			// 显示提示
			String msg = "正在发送 ";
			if (h != GlobalInfo.localHttp)
				msg = "定向发送 ";
			msg += cmdcode + " 号请求,请等待服务器应答......";

			if (GlobalInfo.statusBar != null)
				GlobalInfo.statusBar.setHelpMessage(msg);
		}
		h.setRequestCmdCode(cmdcode);

		// 调试模式显示通讯时长
		long start = System.currentTimeMillis();

		// 发出请求
		String result = h.execute();

		// 如果是面值卡通讯命令,则记录返回值 wangyong add 2011.1.12
		if (cmdcode.equals("43"))
			writeMzkLog("HttpCall.result=[" + result + "]");

		stopService = h.stopService;

		// 调试模式显示通讯时长
		long end = System.currentTimeMillis();

		// 显示命令请求描述
		if (h != GlobalInfo.timeHttp)
		{
			// 恢复提示
			String msg = "网络响应 ";
			if (h != GlobalInfo.localHttp)
				msg = "远程响应 ";
			if (h.isSendOtherHttp())
				msg = "转发响应 ";
			msg += cmdcode + " 号请求耗时: " + (end - start) + " ms";

			if (GlobalInfo.statusBar != null)
				GlobalInfo.statusBar.setHelpMessage(msg);

			// 记录响应时间跟踪日志备查分析
			PublicMethod.traceDebugLog(msg);
		}

		// 解析应答包头
		String[] lines = result.split(GlobalVar.divisionFlag1);
		String[] body = lines[0].split(GlobalVar.divisionFlag2);

		if (body.length < 6)
		{
			PublicMethod.DEBUG_MSG(lines[0]);

			return -1;
		}

		CmdHead head = new CmdHead(body);

		// 判断应答,POSSERVER未定义的命令在开发模式时才进行提示
		if (Integer.parseInt(head.getBackCode()) == 0 || (Integer.parseInt(head.getBackCode()) == 500 && !ConfigClass.isDeveloperMode()))
		{
			if (Integer.parseInt(head.getBackCode()) == 0)
			{
				if (lines.length >= 2)
				{
					arg.delete(0, arg.length());

					// 如果出现& xml 解析错误
					lines[1] = lines[1].replace('&', '_');

					arg.append(lines[1]);

					return 0;
				}
				else
				{
					PublicMethod.DEBUG_MSG(lines[0]);

					return -2;
				}
			}
			else
			{
				int showCode = showError(head.getCmdCode());
				if (h.getSvrURL().equals(GlobalInfo.sysPara.memcardsvrurl) && showCode == 1)
				{
					// this.stopService = false;
				}
				// 往POS发送的命令且参数配置中明确定义不进行信息提示
				else if (h.getSvrURL().equals("http://" + ConfigClass.ServerIP + ":" + ConfigClass.ServerPort + ConfigClass.ServerPath) && showCode == 2)
				{

				}

				return -500;
			}
		}
		else
		{
			// 查询时间的命令不显示报错信息,因为后台线程连接网络失败不能显示错误
			if ((Integer.parseInt(head.getCmdCode()) != CmdDef.GETSERVERTIME) && (Integer.parseInt(head.getCmdCode()) != CmdDef.GETTASK) && (Integer.parseInt(head.getCmdCode()) != CmdDef.GETNEWS) && bShowError)
			{
				String s = head.getErrorMessage();
				s = s.replaceAll("\n", "");
				s = s.replaceAll("\r", "");

				if (s.length() > 0)
				{
					// POS发送的请求数据包为空串
					if (!append && s.indexOf("POS发送的请求数据包为空串") >= 0)
					{
						AccessDayDB.getDefault().writeWorkLog("发生了POS发送的请求数据包为空串", StatusType.WORK_SENDERROR);
						return HttpCall(h, arg, noMsg, true);
					}
					else
					// 客户端暂停POSSERVER访问，则不进行提示,直接命令失败
					if (s.indexOf("客户端暂停POSSERVER访问") >= 0)
					{
					}
					else
					{
						int showCode = showError(head.getCmdCode());
						// 往CRM发送的命令且参数配置中明确定义不进行信息提示
						if (h.getSvrURL().equals(GlobalInfo.sysPara.memcardsvrurl) && showCode == 1 && head.getCmdCode().equals("10")==false)
						{
							// this.stopService = false;// 根据参数设置CRM不提示错误信息
						}
						// 往POS发送的命令且参数配置中明确定义不进行信息提示
						else if (h.getSvrURL().equals("http://" + ConfigClass.ServerIP + ":" + ConfigClass.ServerPort + ConfigClass.ServerPath) && showCode == 2)
						{
							// 根据参数设置POS不提示错误信息
						}
						else
							new MessageBox("#" + arglen + " " + head.getErrorMessage(), null, false);
					}
					/*
					 * // POSSERVER返回该异常说明POSSERVER还在运行，但数据库无法连接,切换到脱网 if
					 * (s.indexOf("POSSERVER访问数据库异常") >= 0) { if (h !=
					 * GlobalInfo.timeHttp && h.getSvrURL() == null) { //
					 * 主线程的HTTP对象才进行设置为脱网的操作 GlobalInfo.isOnline = false; //
					 * 刷新状态栏 GlobalInfo.statusBar.setNetStatus(); // 记录脱网日志 new
					 * MessageBox("POSSERVER访问数据库异常,系统进入脱网状态", null, false);
					 * AccessDayDB
					 * .getDefault().writeWorkLog("POSSERVER访问数据库异常,系统进入脱网状态"
					 * ,StatusType.WORK_SENDERROR); } }
					 */
				}
				else
				{
					if ((noMsg != null) && (noMsg.length() > 0))
					{
						new MessageBox(noMsg, null, false);
					}
				}
			}

			return Integer.parseInt(head.getBackCode());
		}
	}

	public int sendSaleDataToRefund(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue, Http http, int commandCode)
	{
		SaleGoodsDef saleGoodsDef = null;
		SalePayDef salePayDef = null;

		if (!GlobalInfo.isOnline) { return -1; }

		try
		{
			CmdHead aa = null;
			int result = -1;
			aa = new CmdHead(commandCode);

			// 单头打XML
			String line = Transition.ItemDetail(saleHead, SaleHeadDef.ref, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } });
			line = Transition.closeTable(line, "SaleHeadDef", 1);

			// 小票明细
			String line1 = "";

			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				line1 += Transition.ItemDetail(saleGoodsDef, SaleGoodsDef.ref);
			}

			line1 = Transition.closeTable(line1, "saleGoodsDef", saleGoods.size());

			// 付款明细
			String line2 = "";

			for (int i = 0; i < salePayment.size(); i++)
			{
				salePayDef = (SalePayDef) salePayment.elementAt(i);

				line2 += Transition.ItemDetail(salePayDef, SalePayDef.ref);
			}

			line2 = Transition.closeTable(line2, "salePayDef", salePayment.size());

			// 合并
			line = Transition.getHeadXML(line + line1 + line2);

			StringBuffer line3 = new StringBuffer();
			line3.append(aa.headToString() + line);

			if (http == null)
			{
				result = HttpCall(line3, "上传小票失败!");
			}
			else
			{
				result = HttpCall(http, line3, "上传小票失败!");
			}
			// 返回应答数据
			if (result == 0 && retValue != null && line3.toString().trim().length() > 0)
			{
				// 找第4个命令sendok过程的返回
				Vector v = new XmlParse(line3.toString()).parseMeth(3, new String[] { "memo", "value" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					retValue.add(row[0]);
					retValue.add(row[1]);
				}
			}

			//
			return result;
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return -1;
		}
		finally
		{
			saleGoodsDef = null;
			salePayDef = null;
		}

	}

	public int sendRefundSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		return sendSaleDataToRefund(saleHead, saleGoods, salePayment, retValue, getMemCardHttp(CmdDef.SENDBILLTOREFUND), CmdDef.SENDBILLTOREFUND);
	}

	// 获取CRM私有参数
	public boolean getCrmSysPara(Http http, int ID)
	{

		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs };
		String[] args = { "mktcode", "jygs" };

		try
		{
			aa = new CmdHead(ID);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(http, line, "获取系统参数失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "code", "name", "value" });

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writeSysPara(v, false))
				{
					new MessageBox("保存系统参数失败!");
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
	}

	public boolean sendFjkSale(MzkRequestDef req, MzkResultDef ret)
	{
		boolean retValue = sendFjkSale(getMemCardHttp(CmdDef.SENDFJK), req, ret);
		// sendFlqSale(getMemCardHttp(CmdDef.SENDFLQFORZSBH), req, ret);
		return retValue;
	}

	// 返利券查询
	public boolean sendFlqSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.SENDFLQFORZSBH);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, "返利券交易失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MzkResultDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);
					MzkResultDef tmp = new MzkResultDef();

					if (Transition.ConvertToObject(tmp, row))
					{
						{
							ret.memo += tmp.memo;
							return true;
						}
					}
				}
			}
			return false;
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			return false;
		}
	}

	public boolean sendFjkSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.SENDFJK);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, "返券卡交易失败!!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MzkResultDef.ref);

				if (v.size() > 0)
				{

					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(ret, row)) { return true; }
				}
			}
			return false;
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			return false;
		}
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

	// 面值卡日志
	private void writeMzkLog(String content)
	{
		writeLog(ConfigClass.LocalDBPath + "\\Invoice\\" + new ManipulateDateTime().getDateByEmpty() + "\\MZK" + new ManipulateDateTime().getDateByEmpty() + ".log", content);
	}
	
	public boolean sendGongMaoMzkLog(MzkRequestDef req, MzkResultDef ret)
	{

		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		// ：机号，小票号，卡号，操作时间，金额，操作员，具体操作，标志，备注等。
		String[] values = { req.syjh, String.valueOf(req.fphm), req.track2, req.type, String.valueOf(req.je), String.valueOf(ret.ye), req.str1, req.syyh, req.str2, req.memo };

		String[] args = { "syjh", "fphm", "cardno", "type", "je", "ye", "oprdate", "operuser", "flag", "memo" };

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
}
