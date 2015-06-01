package custom.localize.Gzbh;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Gzbh_NetService extends NetService
{
	// 发送交易商品明细
	public boolean sendSaleGoodsByDzq(Vector saleGoods)
	{
		SaleGoodsDef saleGoodsDef = null;

		if (!GlobalInfo.isOnline) { return false; }

		try
		{
			CmdHead aa = null;
			int result = -1;
			aa = new CmdHead(CmdDef.SENDDZQXFMX);

			//商品明细
			String line1 = "";
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				line1 += Transition.ItemDetail(saleGoodsDef, SaleGoodsDef.ref);
			}
			line1 = Transition.closeTable(line1, "saleGoodsDef", saleGoods.size());

			//合并
			String line = Transition.getHeadXML(line1);

			StringBuffer line3 = new StringBuffer();
			line3.append(aa.headToString() + line);

			//
			result = HttpCall(this.getMemCardHttp(CmdDef.SENDDZQXFMX), line3, "上传交易商品明细失败!");

			if (result == 0)
			{
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
		finally
		{
			saleGoodsDef = null;
		}
	}

	public boolean getDzqCanUse(String dzqoperno, StringBuffer dzqcanuse)
	{
		if (!GlobalInfo.isOnline) { return false; }

		String[] values = { dzqoperno, GlobalInfo.sysPara.mktcode, GlobalInfo.syjDef.syjh };
		String[] arg = { "dzqoperno", "mkt", "syjh" };
		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETDZQCANUSE);
			line.append(aa.headToString() + Transition.SimpleXML(values, arg));

			result = HttpCall(line, "获得交易可用券失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "dzqcanuse" });

				if (v.size() > 0)
				{
					values = (String[]) v.elementAt(0);

					dzqcanuse.append(values[0]);
					return true;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}

	// 面值卡结算
	public boolean getDzkInfo(String info[], String code)
	{
		if (!GlobalInfo.isOnline) { return false; }

		Http cardHttp = getJfCardHttp(null, code);

		if (cardHttp == null) { return false; }

		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.syjDef.syjh, GlobalInfo.posLogin.gh };
		String[] arg = { "mkt", "syjh", "syyh" };
		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETDZKINFO);
			line.append(aa.headToString() + Transition.SimpleXML(values, arg));

			result = HttpCall(cardHttp, line, "面值卡结算失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] {
																					"jyje",
																					"jybs",
																					"czje",
																					"xzbs",
																					"rvalue1",
																					"rvalue2",
																					"rvalue3",
																					"rstr1",
																					"rstr2",
																					"rstr3",
																					"rmemo" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (row.length > 3)
					{
						info[0] = row[0];
						info[1] = row[1];
						info[2] = row[2];
						info[3] = row[3];

						return true;
					}

					return false;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;

	}

	// 补打面值卡
	public boolean rePrinttDzkInfo(String[] info, String code)
	{
		if (!GlobalInfo.isOnline) { return false; }

		Http cardHttp = getJfCardHttp(null, code);

		if (cardHttp == null) { return false; }

		String[] values = { GlobalInfo.syjDef.syjh, GlobalInfo.posLogin.gh, GlobalInfo.sysPara.mktcode };
		String[] arg = { "syjh", "syyh", "mkt" };
		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.REPRIDZKINFO);
			line.append(aa.headToString() + Transition.SimpleXML(values, arg));

			result = HttpCall(cardHttp, line, "获得交易可用券失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "record" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (row.length > 0)
					{
						info[0] = row[0];
						return true;
					}

					return false;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;

	}

	public boolean getCustomer(Http h, CustomerDef cust, String track)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, track };
		String[] args = { "mktcode", "track" };

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
			//连接crm服务器失败进行脱网查找
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

	public int sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue, Http http, int commandCode)
	{
		SaleGoodsDef saleGoodsDef = null;
		SalePayDef salePayDef = null;

		if (!GlobalInfo.isOnline) { return -1; }

		try
		{
			CmdHead aa = null;
			int result = -1;
			aa = new CmdHead(commandCode);

			//单头打XML
			String line = Transition.ItemDetail(saleHead, SaleHeadDef.ref);
			line = Transition.closeTable(line, "SaleHeadDef", 1);

			//小票明细
			String line1 = "";

			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				line1 += Transition.ItemDetail(saleGoodsDef, SaleGoodsDef.ref);
			}

			line1 = Transition.closeTable(line1, "saleGoodsDef", saleGoods.size());

			//付款明细
			String line2 = "";

			for (int i = 0; i < salePayment.size(); i++)
			{
				salePayDef = (SalePayDef) salePayment.elementAt(i);

				line2 += Transition.ItemDetail(salePayDef, SalePayDef.ref);
			}

			line2 = Transition.closeTable(line2, "salePayDef", salePayment.size());

			//合并
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

					// 如果是发送到crm的时候
					if (commandCode == CmdDef.SENDCRMSELL)
					{
						String[] memo = row[0].split(";");

						if (memo.length > 3 && memo[3] != null && memo[3].trim().length() > 0)
						{
							new MessageBox(memo[3]);
						}
					}
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

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		Http cardHttp = getJfCardHttp(req, "");

		if (cardHttp == null)
		{
			new MessageBox("面值卡发送的URL错误");
			return false;
		}
		return sendMzkSale(cardHttp, req, ret);
	}

	public boolean getMzkInfo(MzkRequestDef req, MzkResultDef ret)
	{
		Http cardHttp = getJfCardHttp(req, "");
		if (cardHttp == null)
		{
			new MessageBox("面值卡发送的URL错误");
			return false;
		}
		return super.getMzkInfo(cardHttp, req, ret);
	}

	public Http getJfCardHttp(MzkRequestDef req, String payCode)
	{
		Http cardHttp = null;
		// 新大新消费券只在新大新使用，不需要读取url配置文件
		if (req != null)
		{
			if ("0022".equals(req.paycode))
			{
				cardHttp = getCardHttp();
				cardHttp.init();
				cardHttp.setConncetTimeout(ConfigClass.ConnectTimeout); //连接超时
				cardHttp.setReadTimeout(ConfigClass.ReceiveTimeout); //处理超时    
				return cardHttp;
			}
		}

		if (PathFile.fileExist(GlobalVar.ConfigPath + "\\UrlConfig.ini"))
		{
			// 找卡，消费
			if (req != null)
			{
				if (req.str3 != null && req.str3.trim().length() > 0)
				{
					cardHttp = new Http(req.str3);
				}
				else
				{
					HashMap urlList = getUrlList(GlobalVar.ConfigPath + "\\UrlConfig.ini");

					if (urlList == null || urlList.size() < 1)
					{
						new MessageBox("UrlConfig.ini 配置有误!");
						return null;
					}
					if (req.paycode.trim().length() < 1)
					{
						String[] title = { "代码", "描述" };
						int[] width = { 60, 440 };
						String[] content = null;
						Vector contents = new Vector();

						for (Iterator it = urlList.keySet().iterator(); it.hasNext();)
						{
							String key = (String) it.next();
							content = new String[2];
							content[0] = key;
							content[1] = ((String[]) urlList.get(key))[1];
							contents.add(content);
						}

						String memo = "请选择积分卡的种类";
						int choice = new MutiSelectForm().open(memo, title, width, contents, true);
						if (choice >= 0)
						{
							String urlCode = ((String[]) contents.elementAt(choice))[0];
							String urlAddress = ((String[]) urlList.get(urlCode))[0];
							System.out.println(urlAddress);
							cardHttp = new Http(urlAddress);
							// 记录url地址
							if (req != null)
							{
								req.str3 = urlAddress;
							}
						}
						else
						{
							return null;
						}
					}
					else
					{
						String urlAddress = ((String[]) urlList.get(req.paycode.trim()))[0];
						if (urlAddress == null || urlAddress.trim().length() < 1)
						{
							return null;
						}
						else
						{
							System.out.println(urlAddress);
							cardHttp = new Http(urlAddress);
							// 记录url地址
						}
					}
				}
			}
			// 结算，补打，查询
			else
			{
				HashMap urlList = getUrlList(GlobalVar.ConfigPath + "\\UrlConfig.ini");
				String urlAddress = ((String[]) urlList.get(payCode))[0];
				if (urlAddress == null || urlAddress.trim().length() < 1)
				{
					return null;
				}
				else
				{
					System.out.println(urlAddress);
					cardHttp = new Http(urlAddress);
					// 记录url地址
				}
			}
			cardHttp.init();
			cardHttp.setConncetTimeout(ConfigClass.ConnectTimeout); //连接超时
			cardHttp.setReadTimeout(ConfigClass.ReceiveTimeout); //处理超时     
		}
		else
		{
			cardHttp = getCardHttp();
		}
		return cardHttp;
	}

	public HashMap getUrlList(String filePath)
	{
		HashMap urlList;
		BufferedReader br = null;

		try
		{
			br = CommonMethod.readFileGBK(filePath);

			if (br == null)
			{
				new MessageBox("读取 UrlConfig.ini 文件失败!");
				return null;
			}

			urlList = new HashMap();

			String line = null;
			String[] urlinfo = null;
			String[] urldetail = null;

			while ((line = br.readLine()) != null)
			{
				if (line.indexOf("=") > -1 && line.trim().split("=").length == 2)
				{
					urlinfo = new String[] { line.split("=")[0].trim(), line.split("=")[1].trim() };

					if (urlinfo[1].indexOf("&&") > -1 && urlinfo[1].trim().split("&&").length == 2)
					{
						urldetail = new String[] { urlinfo[1].split("&&")[0].trim(), urlinfo[1].split("&&")[1].trim() };
					}

					urlList.put(urlinfo[0], urldetail);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return urlList;
	}

	public int HttpCall(Http h, StringBuffer arg, String noMsg)
	{
		// 参数长度为4K倍数时，POSSERVER读到为空串
		if (arg.length() % 4096 == 0) arg.insert(0, '0');

		// 替换掉换行符
		while (arg.indexOf("\n") >= 0)
		{
			arg.setCharAt(arg.indexOf("\n"), ' ');
		}

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
			if (h != GlobalInfo.localHttp) msg = "定向发送 ";
			msg += cmdcode + " 号请求,请等待服务器应答......";
			GlobalInfo.statusBar.setHelpMessage(msg);
		}

		if (GlobalInfo.sysPara != null && GlobalInfo.sysPara.disableCmd != null && GlobalInfo.sysPara.disableCmd.length() > 0 && cmdcode.length() > 0)
		{
			if (("," + GlobalInfo.sysPara.disableCmd + ",").indexOf("," + cmdcode + ",") > -1) { return -500; }
		}

		h.setRequestCmdCode(cmdcode);

		// 调试模式显示通讯时长
		long start = System.currentTimeMillis();

		// 发出请求
		String result = h.execute();

		// 调试模式显示通讯时长 
		long end = System.currentTimeMillis();

		// 显示命令请求描述
		if (h != GlobalInfo.timeHttp)
		{
			// 恢复提示
			String msg = "网络响应 ";
			if (h != GlobalInfo.localHttp) msg = "远程响应 ";
			if (h.isSendOtherHttp()) msg = "转发响应 ";
			msg += cmdcode + " 号请求耗时: " + (end - start) + " ms";
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

		// 判断应答
		if (Integer.parseInt(head.getBackCode()) == 0 || (Integer.parseInt(head.getBackCode()) == 500 && !ConfigClass.DebugMode))
		{
			if (Integer.parseInt(head.getBackCode()) == 0)
			{
				if (lines.length >= 2)
				{
					arg.delete(0, arg.length());

					//如果出现& xml 解析错误
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
				return -500;
			}
		}
		else
		{
			// 查询时间的命令不显示报错信息,因为后台线程连接网络失败不能显示错误
			if ((Integer.parseInt(head.getCmdCode()) != CmdDef.GETSERVERTIME) && (Integer.parseInt(head.getCmdCode()) != CmdDef.GETTASK)
					&& (Integer.parseInt(head.getCmdCode()) != CmdDef.GETNEWS) && bShowError)
			{

				String s = head.getErrorMessage();

				//            	new MessageBox(s);

				s = s.replaceAll("\n", "");
				s = s.replaceAll("\r", "");

				//            	new MessageBox("new" + s);

				if (s.length() > 0)
				{
					String errorMsg = "";

					if ("101".equals(s))
					{
						errorMsg = "本卡券异常，找不到数据";
					}
					else if ("102".equals(s))
					{
						errorMsg = "本卡券错误，非一条记录数";
					}
					else if ("103".equals(s))
					{
						errorMsg = "本卡券属于黑名单";
					}
					else if ("104".equals(s))
					{
						errorMsg = "本卡券被禁止使用";
					}
					else if ("105".equals(s))
					{
						errorMsg = "本卡券未到启用期";
					}
					else if ("106".equals(s))
					{
						errorMsg = "本卡券未到启用期";
					}
					else if ("107".equals(s))
					{
						errorMsg = "本卡券已到回收金额";
					}
					else if ("108".equals(s))
					{
						errorMsg = "本卡券未发售";
					}
					else if ("109".equals(s))
					{
						errorMsg = "本卡不能作退货处理!";
					}
					else
					{
						errorMsg = head.getErrorMessage();
					}

					if (errorMsg.length() > 0)
					{
						new MessageBox(errorMsg, null, false);
					}
					/*                    
					 // POSSERVER返回该异常说明POSSERVER还在运行，但数据库无法连接,切换到脱网
					 if (s.indexOf("POSSERVER访问数据库异常") >= 0)
					 {
					 if (h != GlobalInfo.timeHttp && h.getSvrURL() == null)
					 {
					 // 主线程的HTTP对象才进行设置为脱网的操作
					 GlobalInfo.isOnline = false;

					 // 刷新状态栏
					 GlobalInfo.statusBar.setNetStatus();

					 // 记录脱网日志
					 new MessageBox("POSSERVER访问数据库异常,系统进入脱网状态", null, false);
					 AccessDayDB.getDefault().writeWorkLog("POSSERVER访问数据库异常,系统进入脱网状态",StatusType.WORK_SENDERROR);
					 }
					 }
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

	/*
	 public boolean sendMzkSale(Http h, MzkRequestDef req, MzkResultDef ret)
	 {
	 if (!GlobalInfo.isOnline) { return false; }

	 CmdHead head = null;
	 StringBuffer line = new StringBuffer();
	 int result = -1;

	 try
	 {
	 head = new CmdHead(CmdDef.SENDMZK);
	 line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

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
	 
	 // seqno重复，重置seqno.ini文件
	 if (result > 9999)
	 {
	 new MessageBox("Seqno重复，现重置为" + result);
	 ProgressBox pb = null;
	 try
	 {
	 pb = new ProgressBox();
	 pb.setText("正在重写Seqno.ini,请等待...");
	 MzkSeqNoResetBS resetBS = new MzkSeqNoResetBS();
	 resetBS.resetSeqNo(String.valueOf(result));
	 new MessageBox("重置成功");
	 return false;
	 }
	 catch (Exception e) {
	 e.printStackTrace();
	 return false;
	 }
	 finally
	 {
	 if (pb != null)
	 {
	 pb.close();
	 pb = null;
	 }
	 }
	 }
	 }
	 catch (Exception er)
	 {
	 er.printStackTrace();
	 }

	 return false;
	 }
	 */
	public boolean sendMzkSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.SENDMZK);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, "储值卡交易失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MzkResultDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(ret, row))
					{
						if ("05".equals(req.type))
						{
							Gzbh_MzkSeqNoResetBS resetBS = new Gzbh_MzkSeqNoResetBS();
							String newSeq = ManipulatePrecision.doubleToString(ret.value1, 0, 0, false);
							if (!resetBS.getSeqNo(req.paycode).equals(newSeq)) resetBS.resetSeqNo(newSeq, req.paycode);
						}
						return true;
					}
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}

	// 根据商品查找超市促销规则单号
	public boolean findSuperMarketPopBillNo(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String ppcode, String spec, String time, String yhtime, String cardno, Http http, int cmdcode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, code, gz, catid, ppcode, spec, time, yhtime, cardno };
		String[] args = { "mktcode", "jygs", "code", "gz", "catid", "ppcode", "spec", "time", "yhtime", "cardno" };

		try
		{
			head = new CmdHead(cmdcode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "查找超市促销规则单号失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, SuperMarketPopRuleDef.ref);

				if (v.size() > 0)
				{
					String[] lines = (String[]) v.elementAt(0);
					if (Transition.ConvertToObject(ruleDef, lines)) { return true; }
				}
				else
				{
					return false;
				}

				//
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
			PosLog.getLog(getClass()).error(er);
			return false;
		}
	}

	// 查找超市促销规则
	public boolean findSuperMarketPopRule(Vector ruleReqList, Vector rulePopList, SuperMarketPopRuleDef popRule, Http http, int cmdcode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, popRule.djbh };
		String[] args = { "mktcode", "jygs", "billno" };

		try
		{
			head = new CmdHead(cmdcode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "查找超市促销规则失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, SuperMarketPopRuleDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);
						SuperMarketPopRuleDef retRule = new SuperMarketPopRuleDef();

						if (Transition.ConvertToObject(retRule, row))
						{
							if (retRule.yhdjlb == '8')
							{
								// 规则条件
								ruleReqList.add(retRule);
							}
							else
							{
								// 规则结果
								rulePopList.add(retRule);
							}
						}
						else
						{
							return false;
						}
					}
				}
				else
				{
					return false;
				}

				//
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
			PosLog.getLog(getClass()).error(er);
			return false;
		}
	}

	// 获取赠品信息
	public boolean getGift(Vector giftList, SaleHeadDef salehead)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { salehead.syjh, String.valueOf(salehead.fphm) };
		String[] args = { "syjh", "fphm" };

		try
		{
			head = new CmdHead(820);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "获取赠品信息失败");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "code", "name" });

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						giftList.add((String[])v.get(i));
					}
				}
				else
				{
					return false;
				}

				//
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
			PosLog.getLog(getClass()).error(er);
			return false;
		}
	}
	
	// 获取换购信息
	public boolean getHGGift(Vector hgList, SaleHeadDef salehead)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { salehead.syjh, String.valueOf(salehead.fphm) };
		String[] args = { "syjh", "fphm" };

		try
		{
			head = new CmdHead(821);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "获取换购信息失败");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "code", "name" });

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						hgList.add((String[])v.get(i));
					}
				}
				else
				{
					return false;
				}

				//
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
			PosLog.getLog(getClass()).error(er);
			return false;
		}
	}
}
