package com.efuture.javaPos.Communication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.WebServiceConfigClass;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.BuyerInfoDef;
import com.efuture.javaPos.Struct.CallInfoDef;
import com.efuture.javaPos.Struct.CheckGoodsDef;
import com.efuture.javaPos.Struct.CommonResultDef;
import com.efuture.javaPos.Struct.ContentItemForTouchScrn;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.CustomerTypeDef;
import com.efuture.javaPos.Struct.CustomerVipZklDef;
import com.efuture.javaPos.Struct.DzcModeDef;
import com.efuture.javaPos.Struct.FjkInfoDef;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.GoodsAmountDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsJFRule;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.GoodsUnitsDef;
import com.efuture.javaPos.Struct.InvoiceInfoDef;
import com.efuture.javaPos.Struct.ManaFrameDef;
import com.efuture.javaPos.Struct.MemoInfoDef;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.NewsDef;
import com.efuture.javaPos.Struct.OperRoleDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.PayRuleDef;
import com.efuture.javaPos.Struct.PayinDetailDef;
import com.efuture.javaPos.Struct.PayinHeadDef;
import com.efuture.javaPos.Struct.PayinModeDef;
import com.efuture.javaPos.Struct.PaymentLimitDef;
import com.efuture.javaPos.Struct.PosTimeDef;
import com.efuture.javaPos.Struct.R5CouponDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleAppendDef;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.ShopPreSaleDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;
import com.efuture.javaPos.Struct.SyjGrangeDef;
import com.efuture.javaPos.Struct.SyjMainDef;
import com.efuture.javaPos.Struct.SyjStatusDef;
import com.efuture.javaPos.Struct.TasksDef;
import com.efuture.javaPos.Struct.WorkLogDef;
import com.efuture.javaPos.Struct.ZqInfoRequestDef;

import custom.localize.Jplm.CouponRet;

public class NetService extends NetServiceDosServer
{
	public static NetService currentNetService = null;

	protected boolean bShowError = true;
	public boolean stopService = false;

	public Vector cardhttplist = null;
	// private String errorMessage = "";
	private String cmdcode = "";

	public static NetService getDefault()
	{
		if (NetService.currentNetService == null)
		{
			NetService.currentNetService = CustomLocalize.getDefault().createNetService();
		}

		return NetService.currentNetService;
	}

	public boolean setErrorMsgEnable(boolean b)
	{
		bShowError = b;

		return bShowError;
	}

	/**
	 * 错误信息（POSSERVER返回）
	 * 
	 * @return
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}

	public String getCmdCode()
	{
		return cmdcode;
	}

	//
	public boolean createHttpConnection()
	{
		Http h;

		try
		{
			h = new Http(ConfigClass.ServerIP, ConfigClass.ServerPort, ConfigClass.ServerPath);
			h.init();
			h.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
			h.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时

			// 释放原全局HTTP对象
			if (GlobalInfo.localHttp != null)
			{
				GlobalInfo.localHttp.disconncet();
				GlobalInfo.localHttp = null;
			}

			// 设置新的全局HTTP对象
			GlobalInfo.localHttp = h;
			GlobalInfo.ipAddr = h.getIPAddress();
			System.out.println((GlobalInfo.localHttp == null) + " -----------111111111111111");
		}
		catch (Exception ex)
		{
			return false;
		}

		return true;
	}

	public boolean createWebServerConn()
	{
		try
		{
			if (WebServiceConfigClass.getDefault().getEndPoint() == null || WebServiceConfigClass.getDefault().getEndPoint().equals(""))
				return false;

			if (GlobalInfo.axis == null)
			{
				GlobalInfo.axis = new AxisWebService();

				((AxisWebService) GlobalInfo.axis).createWebServerConn();
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean createPlugin()
	{
		try
		{
			if (!EBill.getDefault().init())
				return false;
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean isStopService()
	{
		return stopService;
	}

	// 发起请求并等待应答
	public int HttpCall(StringBuffer arg, String noMsg)
	{
		return HttpCall(GlobalInfo.localHttp, arg, noMsg);
	}
	
	//传入：h 通讯地址 arg发送的数据 nomsg 报错信息
	//传出：int 成功和失败的标志 0=成功  arg返回得到的数据
	public int HttpCall(Http h, StringBuffer arg, String noMsg)
	{
		return HttpCall(h, arg, noMsg, false);
	}

	public int HttpCall(Http h, StringBuffer arg, String noMsg, boolean append)
	{
		errorMessage = noMsg;
		// 控制不执行的命令

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
			this.cmdcode = cmdcode;

			// 显示提示
			String msg = Language.apply("正在发送 ");
			if (h != GlobalInfo.localHttp)
				msg = Language.apply("定向发送 ");
			msg += cmdcode + Language.apply(" 号请求,请等待服务器应答......");

			if (GlobalInfo.statusBar != null)
				GlobalInfo.statusBar.setHelpMessage(msg);
		}

		// 如果本次命令被定义为禁用的命令则直接返回-500
		if (GlobalInfo.sysPara != null && GlobalInfo.sysPara.disableCmd != null && GlobalInfo.sysPara.disableCmd.length() > 0 && cmdcode.length() > 0)
		{
			if (("," + GlobalInfo.sysPara.disableCmd + ",").indexOf("," + cmdcode + ",") > -1) { return -500; }
		}

		h.setRequestCmdCode(cmdcode);
		// 调试模式显示通讯时长
		long start = System.currentTimeMillis();

		// 发出请求
		String result = h.execute();
		stopService = h.stopService;
		// 调试模式显示通讯时长
		long end = System.currentTimeMillis();

		// 显示命令请求描述
		if (h != GlobalInfo.timeHttp)
		{
			String syjh = "";
			String fphm = "";
			if (GlobalInfo.syjStatus != null)
			{
				syjh = GlobalInfo.syjStatus.syjh;
				fphm = String.valueOf(GlobalInfo.syjStatus.fphm);
			}

			// 恢复提示
			String msg = Language.apply("网络响应 ");
			if (h != GlobalInfo.localHttp)
				msg = Language.apply("远程响应 ");
			if (h.isSendOtherHttp())
				msg = Language.apply("转发响应 ");
			if (syjh.length() == 0 || fphm.length() == 0)
				msg += Language.apply("命令号[{0}] 请求耗时: ", new Object[] { cmdcode }) + (end - start) + " ms";
			else
				msg += Language.apply("收银机号[{0}] 小票号[{1}] 命令号[{2}] 请求耗时: ", new Object[] { syjh, fphm, cmdcode }) + (end - start) + " ms";
			// msg += "收银机号[" + syjh + "] 小票号[" + fphm + "] 命令号[" + cmdcode +
			// "] 请求耗时: " + (end - start) + " ms";

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
				return -500;
			}
		}
		else
		{

			String s = head.getErrorMessage();
			s = s.replaceAll("\n", "");
			s = s.replaceAll("\r", "");
			if (s.length() > 0)
			{
				errorMessage = "#" + arglen + " " + head.getErrorMessage();
			}

			// 查询时间的命令不显示报错信息,因为后台线程连接网络失败不能显示错误
			if ((Integer.parseInt(head.getCmdCode()) != CmdDef.GETSERVERTIME) && (Integer.parseInt(head.getCmdCode()) != CmdDef.GETTASK) && (Integer.parseInt(head.getCmdCode()) != CmdDef.GETNEWS) && bShowError)
			{
				/*
				 * String s = head.getErrorMessage(); s = s.replaceAll("\n",
				 * ""); s = s.replaceAll("\r", "");
				 */

				if (s.length() > 0)
				{
					// POS发送的请求数据包为空串
					if (!append && s.indexOf("POS发送的请求数据包为空串") >= 0)
					{
						AccessDayDB.getDefault().writeWorkLog(Language.apply("发生了POS发送的请求数据包为空串"), StatusType.WORK_SENDERROR);
						return HttpCall(h, arg, noMsg, true);
					}
					else
					// 客户端暂停POSSERVER访问，则不进行提示,直接命令失败
					if (s.indexOf("客户端暂停POSSERVER访问") >= 0)
					{
					}
					else
					{
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

	public String[] findoldqpaydet(String ysyjh, String yfphm, String paycode, String payno, String str1, String str2, String str3, String str4, String str5, Http http)
	{

		CmdHead aa = null;
		String[] values = { GlobalInfo.sysPara.jygs, GlobalInfo.sysPara.mktcode, ysyjh, yfphm, paycode, payno, str1, str2, str3, str4, str5 };
		String[] arg = { "jygs", "mkt", "ysyjh", "yfphm", "paycode", "payno", "str1", "str2", "str3", "str4", "str5" };
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETOLDQPAYDET);
			line.append(aa.headToString() + Transition.SimpleXML(values, arg));

			result = HttpCall(http, line, Language.apply("没有查询到券益余信息"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "retsjpay", "retpayyy", "rettype", "retmemo1", "retmemo2", "retnum1", "retnum2" });

				values = (String[]) v.elementAt(0);

				return values;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return null;
	}

	// 得到网络时间
	public boolean getServerTime(Http h, TimeDate time)
	{
		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETSERVERTIME);
			line.append(aa.headToString() + Transition.buildEmptyXML());
			result = HttpCall(h, line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, TimeDate.ref);
				String[] time1 = (String[]) v.elementAt(0);

				if (Transition.ConvertToObject(time, time1))
				{
					time.split();

					return true;
				}
				else
				{
					return false;
				}
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
			aa = null;
			line = null;
		}
	}

	public boolean getServerTime(TimeDate time)
	{
		return getServerTime(GlobalInfo.localHttp, time);
	}

	public NewsDef getNews()
	{
		return getNews(GlobalInfo.localHttp);
	}

	// 得到消息
	public NewsDef getNews(Http a)
	{
		if (!GlobalInfo.isOnline) { return null; }

		CmdHead aa = null;
		String[] values = { ConfigClass.CashRegisterCode };
		String[] arg = { "syjh" };
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETNEWS);
			line.append(aa.headToString() + Transition.SimpleXML(values, arg));

			result = HttpCall(a, line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, NewsDef.ref);

				values = (String[]) v.elementAt(0);

				NewsDef news = new NewsDef();

				if (Transition.ConvertToObject(news, values)) { return news; }
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return null;
	}

	public boolean deleteNews(long index)
	{
		return deleteNews(GlobalInfo.localHttp, index);
	}

	// 删除通知
	public boolean deleteNews(Http a, long index)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		String[] values = { String.valueOf(index) };
		String[] arg = { "seqno" };
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.DELNEWS);
			line.append(aa.headToString() + Transition.SimpleXML(values, arg));

			result = HttpCall(a, line, "");

			if (result == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
	}

	public TasksDef getTask()
	{
		return getTask(GlobalInfo.localHttp);
	}

	// 得到任务
	public TasksDef getTask(Http a)
	{
		CmdHead aa = null;
		String[] values = { ConfigClass.CashRegisterCode };
		String[] arg = { "syjh" };
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETTASK);
			line.append(aa.headToString() + Transition.SimpleXML(values, arg));

			result = HttpCall(a, line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, TasksDef.ref);

				values = (String[]) v.elementAt(0);

				TasksDef t = new TasksDef();

				if (Transition.ConvertToObject(t, values)) { return t; }
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return null;
	}

	public boolean deleteTask(long index)
	{
		return deleteTask(GlobalInfo.localHttp, index);
	}

	// 删除任务
	public boolean deleteTask(Http a, long index)
	{
		CmdHead aa = null;
		String[] values = { String.valueOf(index) };
		String[] arg = { "seqno" };
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.DELTASK);
			line.append(aa.headToString() + Transition.SimpleXML(values, arg));

			result = HttpCall(a, line, "");

			if (result == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
	}

	public boolean sendSyjStatus(SyjStatusDef status)
	{
		return sendSyjStatus(GlobalInfo.localHttp, status);
	}

	// 发送收银状态
	public boolean sendSyjStatus(Http a, SyjStatusDef status)
	{
		if (!GlobalInfo.isOnline) { return false; }

		char old_status = status.status;

		if (((old_status == StatusType.STATUS_LOGIN) || (old_status == StatusType.STATUS_SALEING)) && (GlobalInfo.sysPara.maxxj > 0) && (GlobalInfo.syjStatus.xjje > GlobalInfo.sysPara.maxxj))
		{
			status.status = StatusType.STATUS_MORECASH;
		}

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.SENDSYJSTATUS);
			line.append(aa.headToString() + Transition.ConvertToXML(status));

			result = HttpCall(a, line, "");

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
		}

		return false;
	}

	// 得到商品批量信息
	public boolean findAmountDef(GoodsAmountDef pl, String code, String gz, String uid, double sl)
	{
		if (!GlobalInfo.isOnline) { return false; }

		String[] values = { code, gz, uid, String.valueOf(sl) };
		String[] arg = { "code", "gz", "spec", "sl" };
		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.FINDGOODSAMOUNT);
			line.append(aa.headToString() + Transition.SimpleXML(values, arg));

			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsAmountDef.ref);

				if (v.size() > 0)
				{
					values = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(pl, values)) { return true; }
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}

	public Vector findChargeFee(String code, String yyyh, String syyh, String saletype, String memo1, String memo2, String memo3, String memo4, String memo5)
	{
		if (!GlobalInfo.isOnline) { return null; }

		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.FINDCHARGEFEE);

			String[] value = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, ConfigClass.CashRegisterCode, yyyh, syyh, code + " ", saletype, memo1, memo2, memo3, memo4, memo5 };
			String[] arg = { "mktcode", "jygs", "syjh", "yyyh", "syyh", "code", "djlb", "memo1", "memo2", "memo3", "memo4", "memo5" };
			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(getMemCardHttp(CmdDef.GETCOUPON), line, Language.apply("未找到结算列表"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "code", "desc", "je", "memo1", "memo2", "memo3" });
				return v;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			cmdHead = null;
			line = null;
		}

		return null;
	}

	public Vector findCoupon(String code, String saletype)
	{
		if (!GlobalInfo.isOnline) { return null; }

		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.GETCOUPON);

			String[] value = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, ConfigClass.CashRegisterCode, code + " ", saletype };
			String[] arg = { "mktcode", "jygs", "syjh", "code", "djlb" };
			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(getMemCardHttp(CmdDef.GETCOUPON), line, Language.apply("未找到此券规则号"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "code", "desc", "je" });
				return v;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			cmdHead = null;
			line = null;
		}

		return null;
	}

	// 超市用于查找商品用券规则
	public boolean findGoodsCouponRule(GoodsDef goodsDef, String code, String gz, String uid, String catid, String ppcode, String time, String cardno, String cardtype, String djlb, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, code, gz, uid, catid, ppcode, time };
		String[] args = { "mktcode", "code", "gz", "uid", "catid", "ppcode", "rqsj" };

		try
		{
			head = new CmdHead(CmdDef.FINDCRMPOP);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "rule", "memo" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					goodsDef.couponrule = row[0].trim();
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

	// 获得商品
	public int getGoodsDef(GoodsDef goodsDef, String searchFlag, String code, String gz, String scsj, String yhsj, String djlb)
	{
		if (!GlobalInfo.isOnline) { return -1; }

		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.FINDGOODS);

			String[] value = { ConfigClass.CashRegisterCode, searchFlag, code, gz, scsj, yhsj, djlb };
			String[] arg = { "syjh", "searchflag", "code", "gz", "scsj", "yhsj", "djlb" };
			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, Language.apply("未找到此商品"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsDef.ref);

				if (v.size() > 0)
				{
					String[] lines = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(goodsDef, lines)) { return 0; }
				}

				return -1;
			}
			else
			{
				return result;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			cmdHead = null;
			line = null;
		}

		return -1;
	}

	// 得到触屏各分组最大页数
	public long getGoodsOrCategoryMaxCount(boolean searchflag, long cateid, int level)
	{
		if (!GlobalInfo.isOnline) { return 0; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		String[] values = { (searchflag ? "1" : "0"), String.valueOf(cateid), String.valueOf(level) };
		String[] args = { "searchflag", "cateid", "level" };

		try
		{
			head = new CmdHead(CmdDef.GETGOODSCATECOUNT);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("查找品类失败"));

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "maxcount" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);
					if (row1 != null && row1.length > 0)
						return Long.parseLong(row1[0].trim());
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

	// 得到触屏各分组各分页数据
	public boolean getGoodsOrCategoryPages(Vector listgoods, boolean searchflag, long startpos, long endpos, long cateid, int level)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		String[] values = { (searchflag ? "1" : "0"), String.valueOf(startpos), String.valueOf(endpos), String.valueOf(cateid), String.valueOf(level) };
		String[] args = { "searchflag", "startpos", "endpos", "cateid", "level" };

		try
		{
			head = new CmdHead(CmdDef.GETGOODSCATEPAGES);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("获取商品信息失败"));

			if (result != 0)
				return false;

			Vector v = new XmlParse(line.toString()).parseMeth(0, (searchflag ? ContentItemForTouchScrn.refGoods : ContentItemForTouchScrn.refCate));

			if (v.size() > 0)
			{
				for (int i = 0; i < Math.min(listgoods.size(), v.size()); i++)
				{
					String[] row = (String[]) v.elementAt(i);
					ContentItemForTouchScrn goods = new ContentItemForTouchScrn();

					if (Transition.ConvertToObject(goods, row, (searchflag ? ContentItemForTouchScrn.refGoods : ContentItemForTouchScrn.refCate)))
						listgoods.setElementAt(goods, i);
				}
			}
			else
			{
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			listgoods = null;
			return false;
		}
	}

	// 得到人员信息
	public boolean getOperUser(String gh, OperUserDef staff)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { gh };
		String[] args = { "gh" };

		try
		{
			head = new CmdHead(CmdDef.FINDOPERUSER);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("该工号不存在!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, OperUserDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(staff, row)) { return true; }
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}

	public boolean getCustomer(CustomerDef cust, String track)
	{
		if (GlobalInfo.sysPara.iscardcode == 'Y')
		{
			int index = track.indexOf("=");

			if (index >= 0)
				track = track.substring(0, index);
		}

		return getCustomer(getMemCardHttp(CmdDef.FINDCUSTOMER), cust, track);
	}

	public boolean getCustomer(Http h, CustomerDef cust, String track)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, track, GlobalInfo.sysPara.jygs };
		String[] args = { "mktcode", "track", "jygs" };

		try
		{
			head = new CmdHead(CmdDef.FINDCUSTOMER);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(h, line, Language.apply("找不到该顾客卡信息!"));

			if (result == 0)
			{

				Vector v = new XmlParse(line.toString()).parseMeth(0, CustomerDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(cust, row)) { return true; }
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}

	// 得到班次定义
	public boolean getPosTime()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETPOSTIME);
			line.append(aa.headToString() + Transition.buildEmptyXML());

			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, PosTimeDef.ref);

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writePosTime(v))
				{
					new MessageBox(Language.apply("保存班次定义失败!"));
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
		finally
		{
			aa = null;
			line = null;
		}
	}

	// 得到付款模板
	public boolean getPayModeDef()
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { ConfigClass.CashRegisterCode };
		String[] args = { "syjh" };

		try
		{
			head = new CmdHead(CmdDef.GETSYJPAYMODE);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("获取收银机付款模版失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, PayModeDef.ref);

				if (!AccessLocalDB.getDefault().writePayMode(v))
				{
					new MessageBox(Language.apply("保存付款模版失败!"));
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

	// 获得菜单信息定义
	public boolean getMenuFunc()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.GETMENUINFO);
			line.append(cmdHead.headToString() + Transition.buildEmptyXML());

			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MenuFuncDef.ref);

				if (v.size() > 0)
				{
					// 写入本地数据库
					if (!AccessLocalDB.getDefault().writeMenuFunc(v))
					{
						new MessageBox(Language.apply("保存菜单信息失败!"));
					}
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
		finally
		{
			cmdHead = null;
			line = null;
		}
	}

	// 得到收银机信息
	public boolean getSyjMain(String syjh, String ipaddr, String cdkey, String version)
	{
		if (!GlobalInfo.isOnline) { return false; }

		PublicMethod.DEBUG_MSG(ipaddr);

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { syjh, ipaddr, cdkey, version };
		String[] args = { "syjh", "ipaddr", "cdkey", "version" };

		try
		{
			head = new CmdHead(CmdDef.FINDSYJ);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("获取网络收银机定义失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, SyjMainDef.ref);

				if (v.size() > 0)
				{
					// 写入本地数据库
					String[] arg = (String[]) v.elementAt(0);

					if (!AccessLocalDB.getDefault().writeSyjDef(arg))
					{
						new MessageBox(Language.apply("保存收银机定义失败!"));
					}

					return true;
				}
				else
				{
					new MessageBox(Language.apply("获取网络收银机定义失败!"), null, false);

					return false;
				}
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

	// 得到网络系统参数
	public boolean getSysPara()
	{
		return getSysPara(GlobalInfo.localHttp, true, CmdDef.GETSYSPARA);
	}

	// 得到网络系统参数
	public boolean getSysPara(Http http, boolean done, int ID)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		String mktcode = null;
		if (ConfigClass.ck == 'N')
			mktcode = GlobalInfo.sysPara == null ? "" : GlobalInfo.sysPara.mktcode;
		else
			mktcode = "#" + ConfigClass.Market;
		String[] values = { mktcode, GlobalInfo.sysPara == null ? "" : GlobalInfo.sysPara.jygs, GlobalInfo.syjDef.syjh };
		String[] args = { "mktcode", "jygs", "syjh" };

		try
		{
			aa = new CmdHead(ID);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));

			if (http != null)
				result = HttpCall(http, line, Language.apply("获取系统参数失败!"));
			else
				result = HttpCall(line, Language.apply("获取系统参数失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "code", "name", "value" });

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writeSysPara(v, done))
				{
					new MessageBox(Language.apply("保存系统参数失败!"));
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

	// 得到收银机收银范围
	public boolean getSyjGrange()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { ConfigClass.CashRegisterCode };
		String[] args = { "syjh" };

		try
		{
			aa = new CmdHead(CmdDef.GETSYJGRANGE);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("获取收银机收银范围失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, SyjGrangeDef.ref);

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writeSyjGrange(v))
				{
					new MessageBox(Language.apply("保存收银机收银范围失败!"));
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

	// 得到用户角色
	public boolean getOperRole()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETOPERROLE);
			line.append(aa.headToString() + Transition.buildEmptyXML());

			result = HttpCall(line, Language.apply("获取系统用户角色失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, OperRoleDef.ref);

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writeOperRole(v))
				{
					new MessageBox(Language.apply("保存用户角色失败!"));
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

	// 得到会员卡类型
	public boolean getCustomerType()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETCUSTOMERTYPE);
			line.append(aa.headToString() + Transition.buildEmptyXML());

			result = HttpCall(getMemCardHttp(CmdDef.GETCUSTOMERTYPE), line, Language.apply("获取顾客卡类型失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, CustomerTypeDef.ref);

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writeCustomerType(v))
				{
					new MessageBox(Language.apply("保存顾客卡类型失败!"));
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

	// 得到顾客采集信息
	public boolean getBuyerInfo()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETBUYERINFO);
			line.append(aa.headToString() + Transition.buildEmptyXML());

			result = HttpCall(line, Language.apply("获取顾客采集信息失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, BuyerInfoDef.ref);

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writeBuyerInfo(v))
				{
					new MessageBox(Language.apply("保存顾客采集信息失败!"));
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

	// 得到呼叫信息
	public boolean getCallInfo()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETCALLINFO);
			line.append(aa.headToString() + Transition.buildEmptyXML());

			result = HttpCall(line, Language.apply("获取呼叫信息字典失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, CallInfoDef.ref);

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writeCallInfo(v))
				{
					new MessageBox(Language.apply("保存呼叫信息字典失败!"));
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

	public boolean doCommonMethod(String type, String para1, String para2, String para3, String para4, String para5, String para6, String para7, String para8, String para9, String para10, Vector vcresult)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { type, para1, para2, para3, para4, para5, para6, para7, para8, para9, para10 };
		String[] args = { "type", "para1", "para2", "para3", "para4", "para5", "para6", "para7", "para8", "para9", "para10" };

		try
		{
			// 执行通用方法
			head = new CmdHead(CmdDef.COMMONCOMMAND);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0) { return false; }

			Vector v = new XmlParse(line.toString()).parseMeth(0, CommonResultDef.ref);

			for (int i = 0; i < v.size(); i++)
			{
				String[] row = (String[]) v.elementAt(i);

				CommonResultDef crd = new CommonResultDef();

				if (Transition.ConvertToObject(crd, row))
				{
					vcresult.add(crd);
				}
				else
				{
					vcresult.clear();

					return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			vcresult.clear();

			ex.printStackTrace();
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
	}

	public boolean doWhtBlackList(String oprtype, String cardno, String termflag, String oprtime, String logicno, String physicno, String primtype, String subtype, String memo)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		// 0 - 查找黑名单 1-上传黑名单
		String[] values = { oprtype, cardno, termflag, oprtime, logicno, physicno, primtype, subtype, memo };
		String[] args = { "oprtype", "cardno", "termflag", "oprtime", "logicno", "physicno", "primtype", "subtype", "memo" };

		try
		{
			head = new CmdHead(CmdDef.FINDWHTBLACKLIST);
			line.append(head.headToString() +

			Transition.SimpleXML(values, args));

			result = HttpCall(line, "");

			// 返回0表示存在于黑名单中
			if (result == 0)
				return true;
			else
				return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean getMemoInfo()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETMEMOINFO);
			line.append(aa.headToString() + Transition.buildEmptyXML());

			result = HttpCall(line, Language.apply("获取备用信息字典失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MemoInfoDef.ref);

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writeMemoInfo(v))
				{
					new MessageBox(Language.apply("保存备用信息字典失败!"));
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

	// 得到电子秤模版
	public boolean getDzcMode()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETDZCMODE);
			line.append(aa.headToString() + Transition.buildEmptyXML());

			result = HttpCall(line, Language.apply("获取系统电子秤模版失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, DzcModeDef.ref);

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writeDzcMode(v))
				{
					new MessageBox(Language.apply("保存电子秤模版失败!"));
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

	// 得到缴款模版
	public boolean getPayinMode()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETWITHDRAWMODE);
			line.append(aa.headToString() + Transition.buildEmptyXML());

			result = HttpCall(line, Language.apply("获取收银机缴款模版失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, PayinModeDef.ref);

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writePayinMode(v))
				{
					new MessageBox(Language.apply("保存缴款模版失败!"));
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

	// 得到系统管理架构
	public boolean getManaFrame()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETMANAGEFRAME);
			line.append(aa.headToString() + Transition.buildEmptyXML());

			result = HttpCall(line, Language.apply("获取系统管理架构失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, ManaFrameDef.ref);

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writeManaFrame(v))
				{
					new MessageBox(Language.apply("保存系统管理架构失败!"));
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

	// 得到付款上限
	public boolean getPaymentLimit()
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		String[] values = { ConfigClass.CashRegisterCode };
		String[] arg = { "syjh" };
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.GETPAYMENTLIMIT);
			line.append(aa.headToString() + Transition.SimpleXML(values, arg));
			result = HttpCall(line, Language.apply("获取付款上限失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, PaymentLimitDef.ref);

				// 写入本地数据库
				if (!AccessLocalDB.getDefault().writePaymentLimit(v))
				{
					new MessageBox(Language.apply("保存付款上限失败!"));
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

	// 发送呼叫信息
	public boolean sendCallInfo(CallInfoDef info)
	{
		if (!GlobalInfo.isOnline || info == null) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { ConfigClass.CashRegisterCode, GlobalInfo.posLogin == null ? "0000" : GlobalInfo.posLogin.gh, info.code, info.text };
		String[] args = { "syjh", "gh", "code", "text" };

		try
		{
			aa = new CmdHead(CmdDef.SENDCALLINFO);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("发送呼叫信息失败!"));

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

	// 修改收银员密码
	public boolean setPassWord(String password)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.posLogin.gh, password };
		String[] args = { "gh", "password" };

		try
		{
			aa = new CmdHead(CmdDef.CHANGEPASS);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("密码修改失败!"));

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

	// 获得交易小票信息
	public boolean getInvoiceInfo(InvoiceInfoDef inv)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { ConfigClass.CashRegisterCode, GlobalInfo.balanceDate };
		String[] args = { "syjh", "jzrq" };

		try
		{
			aa = new CmdHead(CmdDef.GETINVOICE);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("获取交易小票信息失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, InvoiceInfoDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(inv, row)) { return true; }
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}

	// 发送工作日志
	public boolean sendWorkLog(WorkLogDef work)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.SENDWORK);
			line.append(aa.headToString() + Transition.ConvertToXML(work));

			result = HttpCall(line, "");

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
		}

		return false;
	}

	// 上传交款单信息
	public boolean sendPayin(PayinHeadDef phd, ArrayList payListMode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		try
		{
			CmdHead aa = null;

			int result = -1;

			aa = new CmdHead(CmdDef.SENDPAYIN);

			// 单头打XML
			String line = Transition.ItemDetail(phd, PayinHeadDef.ref);
			line = Transition.closeTable(line, "PayinHeadDef", 1);

			// 单体打XML
			String line1 = "";

			for (int i = 0; i < payListMode.size(); i++)
			{
				PayinDetailDef pdd = (PayinDetailDef) payListMode.get(i);
				line1 = line1 + Transition.ItemDetail(pdd, PayinDetailDef.ref);
			}

			line1 = Transition.closeTable(line1, "PayinDetailDef", payListMode.size());

			// 合并
			line = Transition.getHeadXML(line + line1);

			StringBuffer line2 = new StringBuffer();
			line2.append(aa.headToString() + line);

			//
			result = HttpCall(line2, Language.apply("上传缴款单失败!"));

			if (result == 0 || result == 2)
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

	public int sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		return sendSaleData(saleHead, saleGoods, salePayment, retValue, null, CmdDef.SENDSELL);
	}

	public int sendExtendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		return sendSaleData(saleHead, saleGoods, salePayment, retValue, getMemCardHttp(CmdDef.SENDCRMSELL), CmdDef.SENDCRMSELL);
	}

	/**
	 * 中免
	 * 
	 * @param saleHead
	 * @param saleGoods
	 * @param salePayment
	 * @param saleCust
	 *            顾客信息
	 * @param retValue
	 * @return
	 */
	public int sendSaleDataCust(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, SaleCustDef saleCust, Vector retValue)
	{
		return sendSaleDataCust(saleHead, saleGoods, salePayment, saleCust, retValue, null, CmdDef.SENDSELL);
	}

	/**
	 * 中免(需要客户化实现)
	 * 
	 * @param saleHead
	 * @param saleGoods
	 * @param salePayment
	 * @param saleCust
	 *            顾客信息
	 * @param retValue
	 * @param http
	 * @param commandCode
	 * @return
	 */
	public int sendSaleDataCust(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, SaleCustDef saleCust, Vector retValue, Http http, int commandCode)
	{
		// 需要客户化实现
		return -1;
	}

	// 发送销售小票
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
				result = HttpCall(line3, Language.apply("上传小票失败!"));
			}
			else
			{
				result = HttpCall(http, line3, Language.apply("上传小票失败!"));
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

	public boolean getPreSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh, fphm, GlobalInfo.sysPara.jygs, GlobalInfo.sysPara.mktcode };
		String[] args = { "syjh", "code", "jygs", "mktcode" };

		try
		{
			// 查询退货小票头
			head = new CmdHead(CmdDef.GETPRESALEHEADINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("预售小票头查询失败!"));
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleHeadDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到预售销售小票头,预售小票不存在或已确认!"));
				return false;
			}

			String[] row = (String[]) v.elementAt(0);

			if (!Transition.ConvertToObject(shd, row))
			{
				shd = null;
				new MessageBox(Language.apply("预售销售小票头转换失败!"));
				return false;
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询退货小票明细
			head = new CmdHead(CmdDef.GETPRESALEDETAILINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("预售小票明细查询失败!"));
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到预售小票明细,预售小票不存在或已确认!"));
				return false;
			}

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

			// 查询小票付款明细
			head = new CmdHead(CmdDef.GETPRESALEPAYINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("付款明细查询失败!"));
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SalePayDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到预售小票付款明细,预售小票不存在或已确认!"));
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);
				SalePayDef spd = new SalePayDef();

				if (Transition.ConvertToObject(spd, row))
				{
					payDetail.add(spd);
				}
				else
				{
					payDetail.clear();
					payDetail = null;
					return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			shd = null;

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

	public boolean getBackSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh, fphm };
		String[] args = { "syjh", "code" };

		try
		{
			// 查询退货小票头
			head = new CmdHead(CmdDef.GETBACKSALEHEAD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("退货小票头查询失败!"));
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleHeadDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到退货小票头,退货小票不存在或已确认!"));
				return false;
			}

			String[] row = (String[]) v.elementAt(0);

			if (!Transition.ConvertToObject(shd, row))
			{
				shd = null;
				new MessageBox(Language.apply("退货小票头转换失败!"));
				return false;
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询退货小票明细
			head = new CmdHead(CmdDef.GETBACKSALEDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("退货小票明细查询失败!"));
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到退货小票明细,退货小票不存在或已确认!"));
				return false;
			}

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

			// 查询小票付款明细
			head = new CmdHead(CmdDef.GETBACKPAYSALEDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("付款明细查询失败!"));
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SalePayDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到付款小票明细,退货小票不存在或已确认!"));
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);
				SalePayDef spd = new SalePayDef();

				if (Transition.ConvertToObject(spd, row))
				{
					payDetail.add(spd);
				}
				else
				{
					payDetail.clear();
					payDetail = null;
					return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			shd = null;

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

	/**
	 * //会员卡HTTP public Http getMemCardHttp() { return getMemCardHttp(0); }
	 */

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

	// 面值卡HTTP
	public Http getCardHttp()
	{
		if ((GlobalInfo.sysPara.cardsvrurl == null) || (GlobalInfo.sysPara.cardsvrurl.trim().length() <= 0))
		{
			return GlobalInfo.localHttp;
		}
		else
		{
			String url = GlobalInfo.sysPara.cardsvrurl;
			if (url.trim().indexOf("LOCALLOOP:") == 0)
			{
				url = "Http://" + ConfigClass.ServerIP.trim() + ":" + ConfigClass.ServerPort + url.substring(10).trim();
			}
			else
			{
				String[] ipaddr = GlobalInfo.sysPara.cardsvrurl.split(",");
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
			if (GlobalInfo.cardHttp == null || (GlobalInfo.cardHttp != null && !GlobalInfo.cardHttp.isSameHttp(GlobalInfo.sysPara.cardsvrurl)))
			{
				GlobalInfo.cardHttp = new Http(url);
				GlobalInfo.cardHttp.init();
				GlobalInfo.cardHttp.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
				GlobalInfo.cardHttp.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
			}

			return GlobalInfo.cardHttp;
		}
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (new File(GlobalVar.ConfigPath + "\\mzklist.ini").exists())
		{
			if (cardhttplist == null || cardhttplist.size() <= 0)
			{
				cardhttplist = new Vector();
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\mzklist.ini");
				String line = null;
				try
				{
					while ((line = br.readLine()) != null)
					{
						if (line.trim().length() < 1)
							continue;

						if (line.trim().charAt(0) == ';')
							continue;

						// eg:0400 =
						// http://172.17.6.193:8080/PosServerPos/PosServer=45
						if (line.trim().indexOf("=") > 0)
						{
							Object[] obj = null;
							String[] iplist = line.trim().split("=");
							Http a = new Http(iplist[1]);
							a.init();
							a.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
							a.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时

							if (iplist.length > 2)
								obj = new Object[] { iplist[0], a, iplist[2] };
							else
								obj = new Object[] { iplist[0], a, String.valueOf(CmdDef.SENDMZK) };

							cardhttplist.add(obj);
						}
					}
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// 0400=Http:11111=49
			if (cardhttplist != null && cardhttplist.size() > 0)
			{
				for (int i = 0; i < cardhttplist.size(); i++)
				{
					Object[] row = (Object[]) cardhttplist.elementAt(i);
					String list = (String) row[0];
					int cmdCode;
					if (row.length > 2)
					{
						cmdCode = Integer.parseInt((String) row[2]);
					}
					else
					{
						cmdCode = CmdDef.SENDMZK;
					}

					int x = ("," + list.trim() + ",").indexOf("," + req.paycode + ",");

					if (x > -1)
					{
						Http htp = (Http) row[1];
						return sendMzkSale(htp, req, ret, cmdCode);
					}
				}
			}
		}
		return sendMzkSale(getCardHttp(), req, ret);
	}

	public boolean sendMzkSale(Http h, MzkRequestDef req, MzkResultDef ret, int cmdCode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(cmdCode);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, Language.apply("储值卡交易失败!"));

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
			er.printStackTrace();
		}

		return false;

	}
	
	/**
	 * 	
	 * <configure CmdCode="136">
		<CmdType>HttpCmd</CmdType>
		<CmdMemo>面值卡使用范围查询</CmdMemo>
		<CmdTran>1</CmdTran>
		<Cmd_01_Mode>MemoryCourse</Cmd_01_Mode>
		<Sql_01_Type>*</Sql_01_Type>
		<Tran_01_Sql>{call java_findmzkscope(?,?,?,?,?,?,?,?,?,?)}</Tran_01_Sql>
		<Tran_01_ParaName>syjh,mktcode,jygs,cardno,vgoodsstr</Tran_01_ParaName>
		<Tran_01_ParaType>s,s,s,s,s</Tran_01_ParaType>
		<Tran_01_ColName>recode,remsg,scope,str1,str2</Tran_01_ColName>
		<Tran_01_ColType>i,s,s,s,s</Tran_01_ColType>
	</configure>
	 * @param cardno
	 * @param vgoodsstr
	 * @param ret
	 * @return
	 */
	public boolean findmzkscope( String cardno,String vgoodsstr, String[] ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.findmzkscope);
			
			String[] value = { GlobalInfo.syjDef.syjh,GlobalInfo.sysPara.mktcode,GlobalInfo.sysPara.jygs,cardno,vgoodsstr};
			String[] arg = { "syjh","mktcode", "jygs", "cardno", "vgoodsstr"};

			line.append(head.headToString() + Transition.SimpleXML(value, arg));
			
			//line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(getCardHttp(), line, Language.apply("储值卡交易范围查询失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[]{"scope","str1","str2"});

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					ret[0] = row[0];
					ret[1] = row[1];
					ret[2] = row[2];
				}
				
				return true;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;

	}

	public boolean sendMzkSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		return sendMzkSale(h, req, ret, CmdDef.SENDMZK);
	}

	public boolean getMzkInfo(MzkRequestDef req, MzkResultDef ret)
	{
		if (new File(GlobalVar.ConfigPath + "\\mzklist.ini").exists())
		{
			if (cardhttplist == null || cardhttplist.size() <= 0)
			{
				cardhttplist = new Vector();
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\mzklist.ini");
				String line = null;
				try
				{
					while ((line = br.readLine()) != null)
					{
						if (line.trim().length() < 1)
							continue;

						if (line.charAt(0) == ';')
							continue;

						if (line.trim().indexOf("=") > 0)
						{
							// eg:0400 =
							// http://172.17.6.193:8080/PosServerPos/PosServer=45
							Object[] obj = null;
							String[] iplist = line.trim().split("=");
							Http a = new Http(iplist[1]);
							a.init();
							a.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
							a.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时

							if (iplist.length > 2)
								obj = new Object[] { iplist[0], a, iplist[2] };
							else
								obj = new Object[] { iplist[0], a, String.valueOf(CmdDef.FINDMZKINFO) };

							cardhttplist.add(obj);
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			if (cardhttplist != null && cardhttplist.size() > 0)
			{
				for (int i = 0; i < cardhttplist.size(); i++)
				{
					Object[] row = (Object[]) cardhttplist.elementAt(i);
					String list = (String) row[0];
					int cmdCode;
					if (row.length > 2)
					{
						cmdCode = Integer.parseInt((String) row[2]);
					}
					else
					{
						cmdCode = CmdDef.FINDMZKINFO;
					}
					int x = ("," + list.trim() + ",").indexOf("," + req.paycode + ",");

					if (x > -1)
					{
						Http htp = (Http) row[1];
						return getMzkInfo(htp, req, ret, cmdCode);
					}
				}
			}
		}

		return getMzkInfo(getCardHttp(), req, ret);
	}

	public boolean getMzkInfo(Http h, MzkRequestDef req, MzkResultDef ret, int cmdCode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(cmdCode);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, Language.apply("查询储值卡失败!"));

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
			er.printStackTrace();
		}

		return false;

	}

	public boolean getMzkInfo(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		return getMzkInfo(h, req, ret, CmdDef.FINDMZKINFO);
	}

	// ----------------DZQ START

	public boolean sendDzqSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (new File(GlobalVar.ConfigPath + "\\dzqlist.ini").exists())
		{
			if (cardhttplist == null || cardhttplist.size() <= 0)
			{
				cardhttplist = new Vector();
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\dzqlist.ini");
				String line = null;
				try
				{
					while ((line = br.readLine()) != null)
					{
						if (line.charAt(0) == ';')
							continue;

						if (line.trim().indexOf("=") > 0)
						{
							String[] iplist = line.trim().split("=");
							Http a = new Http(iplist[1]);
							a.init();
							a.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
							a.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时

							Object[] obj = null;
							if (iplist.length > 2)
								obj = new Object[] { iplist[0], a, iplist[2] };
							else
								obj = new Object[] { iplist[0], a, String.valueOf(CmdDef.SENDDZQ) };

							cardhttplist.add(obj);
						}
					}
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (cardhttplist != null && cardhttplist.size() > 0)
			{
				for (int i = 0; i < cardhttplist.size(); i++)
				{
					Object[] row = (Object[]) cardhttplist.elementAt(i);
					String list = (String) row[0];
					int x = ("," + list.trim() + ",").indexOf("," + req.paycode + ",");

					if (x > -1)
					{
						Http htp = (Http) row[1];
						return sendDzqSale(htp, req, ret);
					}
				}
			}
		}
		return sendDzqSale(getCardHttp(), req, ret);
	}

	public boolean sendDzqSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.SENDDZQ);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, Language.apply("电子券交易失败!"));

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
			er.printStackTrace();
		}

		return false;
	}

	public boolean getDzqInfo(MzkRequestDef req, MzkResultDef ret)
	{
		if (new File(GlobalVar.ConfigPath + "\\dzqlist.ini").exists())
		{
			if (cardhttplist == null || cardhttplist.size() <= 0)
			{
				cardhttplist = new Vector();
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\dzqlist.ini");
				String line = null;
				try
				{
					while ((line = br.readLine()) != null)
					{
						if (line.charAt(0) == ';')
							continue;

						if (line.trim().indexOf("=") > 0)
						{
							String[] iplist = line.trim().split("=");
							Http a = new Http(iplist[1]);
							a.init();
							a.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
							a.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
							// Object[] obj = new Object[] { iplist[0], a };
							Object[] obj = null;
							if (iplist.length > 2)
								obj = new Object[] { iplist[0], a, iplist[2] };
							else
								obj = new Object[] { iplist[0], a, String.valueOf(CmdDef.SENDDZQ) };

							cardhttplist.add(obj);
						}
					}
				}
				catch (IOException e)
				{
					PosLog.getLog(getClass()).error(e);
					e.printStackTrace();
				}
			}

			if (cardhttplist != null && cardhttplist.size() > 0)
			{
				for (int i = 0; i < cardhttplist.size(); i++)
				{
					Object[] row = (Object[]) cardhttplist.elementAt(i);
					String list = (String) row[0];
					int x = ("," + list.trim() + ",").indexOf("," + req.paycode + ",");

					if (x > -1)
					{
						Http htp = (Http) row[1];
						return sendDzqSale(htp, req, ret);
					}
				}
			}
		}

		return getDzqInfo(getCardHttp(), req, ret);
	}

	public boolean getDzqInfo(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.FINDDZQINFO);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, Language.apply("查询电子券失败!"));

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

	// ----------------DZQ END

	// 赠券
	public boolean saveZqInfo(ZqInfoRequestDef zqinfo)
	{
		return saveZqInfo(getCardHttp(), zqinfo);
	}

	// 赠券
	public boolean saveZqInfo(Http h, ZqInfoRequestDef zqinfo)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.ZSFQINFO);
			line.append(head.headToString() + Transition.ConvertToXML(zqinfo, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, Language.apply("赠券失败!"));

			if (result == 0) { return true; }
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
		}

		return false;
	}

	public boolean getFjkInfo(MzkRequestDef req, ArrayList fjklist)
	{
		return getFjkInfo(getMemCardHttp(CmdDef.FINDFJKINFO), req, fjklist);
	}

	public boolean getJfInfo(MzkRequestDef req, ArrayList jfList)
	{
		return getJfInfo(getMemCardHttp(CmdDef.FINDJFINFO), req, jfList);
	}

	// 积分明细查询
	public boolean getJfInfo(Http h, MzkRequestDef req, ArrayList jfList)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		if (!GlobalInfo.isOnline) { return false; }

		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.FINDJFINFO);

			String[] value = { req.mktcode, GlobalInfo.sysPara.jygs, req.track2, req.track1, req.track3 };
			String[] arg = { "mktcode", "jygs", "cardno", "track1", "track3" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(h, line, Language.apply("未查询到积分信息明细"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, FjkInfoDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						FjkInfoDef fjd = new FjkInfoDef();

						if (Transition.ConvertToObject(fjd, row))
						{
							jfList.add(fjd);
						}
						else
						{
							return false;
						}
					}
				}
				else
				{
					new MessageBox(Language.apply("没有查询到当前积分明细信息!"), null, false);
					return false;
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
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			cmdHead = null;
			line = null;
		}
	}

	// 返券卡查询
	public boolean getFjkInfo(Http h, MzkRequestDef req, ArrayList fjklist)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		if (!GlobalInfo.isOnline) { return false; }

		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.FINDFJKINFO);

			String[] value = { req.mktcode, GlobalInfo.sysPara.jygs, req.track2, req.track1, req.track3 };
			String[] arg = { "mktcode", "jygs", "cardno", "track1", "track3" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(h, line, Language.apply("未查询到返券卡信息明细"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, FjkInfoDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						FjkInfoDef fjd = new FjkInfoDef();

						if (Transition.ConvertToObject(fjd, row))
						{
							fjklist.add(fjd);
						}
						else
						{
							return false;
						}
					}
				}
				else
				{
					new MessageBox(Language.apply("没有查询到当前返券卡信息!"), null, false);
					return false;
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
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			cmdHead = null;
			line = null;
		}
	}

	// 获得返券卡规则信息
	public boolean getFjkRuleInfo(Http h, MzkRequestDef req, ArrayList fjklist)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		if (!GlobalInfo.isOnline) { return false; }

		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.GETACCEPTFJKRULE);

			String[] value = { req.mktcode, req.track1, req.track2, req.track3 };
			String[] arg = { "mktcode", "track1", "track2", "track3" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(h, line, Language.apply("未查询到返券卡规则信息"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, FjkInfoDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						FjkInfoDef fjd = new FjkInfoDef();

						if (Transition.ConvertToObject(fjd, row))
						{
							fjklist.add(fjd);
						}
						else
						{
							return false;
						}
					}
				}
				else
				{
					new MessageBox(Language.apply("没有查询到当前返券卡规则信息!"), null, false);
					return false;
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
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			cmdHead = null;
			line = null;
		}
	}

	public boolean getFjkRuleInfo(MzkRequestDef req, ArrayList fjklist)
	{
		return getFjkRuleInfo(getMemCardHttp(CmdDef.GETACCEPTFJKRULE), req, fjklist);
	}

	// 发送返券卡
	public boolean sendFjkSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (new File(GlobalVar.ConfigPath + "\\mzklist.ini").exists())
		{
			if (cardhttplist == null || cardhttplist.size() <= 0)
			{
				cardhttplist = new Vector();
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\mzklist.ini");
				String line = null;
				try
				{
					while ((line = br.readLine()) != null)
					{
						if (line.trim().length() < 1)
							continue;

						if (line.trim().charAt(0) == ';')
							continue;

						if (line.trim().indexOf("=") > 0)
						{
							String[] iplist = line.trim().split("=");
							Http a = new Http(iplist[1]);
							a.init();
							a.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
							a.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
							// Object[] obj = new Object[] { iplist[0], a };
							Object[] obj = null;
							if (iplist.length > 2)
								obj = new Object[] { iplist[0], a, iplist[2] };
							else
								obj = new Object[] { iplist[0], a, String.valueOf(CmdDef.SENDFJK) };

							cardhttplist.add(obj);
						}
					}
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (cardhttplist != null && cardhttplist.size() > 0)
			{
				for (int i = 0; i < cardhttplist.size(); i++)
				{
					Object[] row = (Object[]) cardhttplist.elementAt(i);
					String list = (String) row[0];
					int x = ("," + list.trim() + ",").indexOf("," + req.paycode + ",");

					if (x > -1)
					{
						Http htp = (Http) row[1];
						return sendFjkSale(htp, req, ret);
					}
				}
			}
		}

		return sendFjkSale(getMemCardHttp(CmdDef.SENDFJK), req, ret);
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

	// 会员卡交易
	public boolean sendHykSale(MzkRequestDef req, MzkResultDef ret)
	{
		return sendHykSale(getMemCardHttp(CmdDef.SENDHYK), req, ret);
	}

	public boolean sendHykSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.SENDHYK);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(h, line, Language.apply("会员卡交易失败!"));

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

	public boolean sendHykSaleNew(MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(CmdDef.SENDHYKNEW);
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));

			result = HttpCall(getMemCardHttp(CmdDef.SENDHYKNEW), line, Language.apply("会员卡交易失败!"));

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

	public boolean getBackGoodsDetail(Vector backgoods, String oldsyj, String oldfphm, String code, String gz, String uid)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { oldsyj, oldfphm, code, gz, uid };
		String[] args = { "oldsyj", "oldfphm", "code", "gz", "uid" };

		try
		{
			head = new CmdHead(CmdDef.GETBACKGOODSINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("原退货小票上未找到此商品!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

				for (int j = 0; j < v.size(); j++)
				{
					String[] row = (String[]) v.elementAt(j);
					SaleGoodsDef saleGoodsDef = new SaleGoodsDef();
					if (Transition.ConvertToObject(saleGoodsDef, row))
					{
						backgoods.add(saleGoodsDef);
					}
				}

				if (backgoods.size() <= 0)
				{
					new MessageBox(Language.apply("原退货小票上未找到此商品!"));
				}
				else
					return true;
			}
		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
		}

		return false;
	}

	// 检查收银员是否允许登录
	public boolean getCheckLogin(String syjh, String syyh)
	{
		if (!GlobalInfo.isOnline) { return true; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh, syyh };
		String[] args = { "syjh", "syyh" };

		try
		{
			head = new CmdHead(CmdDef.CHECKLOGIN);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("该工号已经在其他收银机登录\n\n请用其他工号登录!"));

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
			PosLog.getLog(getClass()).error(er);
		}

		return true;
	}

	public boolean getMutiUnit(Vector unit, String code)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { code };
		String[] args = { "code" };

		try
		{
			head = new CmdHead(CmdDef.GETSGOODSUNITSLIST);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("多单位信息查询失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsUnitsDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);
						GoodsUnitsDef unitDef = new GoodsUnitsDef();

						if (Transition.ConvertToObject(unitDef, row))
						{
							unit.add(unitDef);
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

	public boolean getSubGoodsDef(Vector subGoods, String fxm, String gz, char type)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { fxm, gz, String.valueOf(type) };
		String[] args = { "fxm", "gz", "type" };

		try
		{
			head = new CmdHead(CmdDef.GETCHILDGOODSLIST);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("子母商品信息查询失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "barcode", "name" });

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						subGoods.add(v.elementAt(i));
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

	public boolean getCheckGoodsList(Vector ckgd, String djbh, String code, String gz)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { djbh, code, gz };
		String[] args = { "djbh", "code", "gz" };

		try
		{
			head = new CmdHead(CmdDef.GETCHECKGOODS);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("查询盘点单失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, CheckGoodsDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						CheckGoodsDef chkgd = new CheckGoodsDef();
						if (Transition.ConvertToObject(chkgd, row))
						{
							ckgd.add(chkgd);
						}
					}
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
			PosLog.getLog(getClass()).error(er);
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

			String[] values = { GlobalInfo.syjStatus.syjh, GlobalInfo.syjStatus.syyh, String.valueOf(chkgd.row), djbh, chkgd.code, chkgd.gz, chkgd.pdsl, chkgd.pdje, checkcw,// 盘点仓位
			checkrq,// 盘点日期
			chkgd.uid, String.valueOf(chkgd.bzhl), isLastLine, lineState };

			String[] args = { "syjh", "syyh", "rowno", "djbh", "code", "gz", "pdsl", "pdje", "checkcw", "checkrq", "uid", "bzhl", "isLastLine", "lineState" };

			cmdHead = new CmdHead(CmdDef.SENDCHECKGOODS);
			line.append(cmdHead.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("上传商品盘点数据失败!"));

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
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
	}

	public boolean getShopPreSaleGoods(ArrayList listgoods, String billid)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		try
		{
			if (!GlobalInfo.isOnline)
				return false;

			int result = -1;

			cmdHead = new CmdHead(CmdDef.GETSHOPPRESALE);

			String[] value = { GlobalInfo.sysPara.mktcode, billid };
			String[] arg = { "mktcode", "billid" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, Language.apply("未找到此单据"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, ShopPreSaleDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						ShopPreSaleDef gd = new ShopPreSaleDef();

						if (Transition.ConvertToObject(gd, row))
						{
							listgoods.add(gd);
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

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			cmdHead = null;
			line = null;
		}
	}

	/*
	 * 新的商品查询函数 修改说明:添加SQL参数 wangyong add by 2010.5.28
	 */
	public boolean getGoodsList(ArrayList listgoods, char codetype, String txtCode, String sql)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			int result = -1;

			cmdHead = new CmdHead(CmdDef.GETGOODSLIST);

			String[] value = { txtCode, String.valueOf(codetype), sql };
			String[] arg = { "code", "codetype", "sql" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, Language.apply("未找到此商品"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						GoodsDef gd = new GoodsDef();

						if (Transition.ConvertToObject(gd, row))
						{
							listgoods.add(gd);
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

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			cmdHead = null;
			line = null;
		}
	}

	public void getBatchList(ArrayList batchList, String code, String gz, String uid)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		try
		{
			if (!GlobalInfo.isOnline) { return; }

			int result = -1;

			cmdHead = new CmdHead(CmdDef.GETGOODSAMOUNTLIST);

			String[] value = { code, gz, uid };
			String[] arg = { "code", "gz", "uid" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, Language.apply("未找到此批量信息"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsAmountDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						GoodsAmountDef gad = new GoodsAmountDef();

						if (Transition.ConvertToObject(gad, row))
						{
							batchList.add(gad);
						}
						else
						{
							return;
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(getClass()).error(ex);
		}
	}

	public void getYhList(ArrayList yhList, String code, String gz, String catid, String ppcode, String specinfo)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();

		try
		{
			if (!GlobalInfo.isOnline) { return; }

			int result = -1;

			cmdHead = new CmdHead(CmdDef.GETGOODSPOPLIST);

			String[] value = { code, gz, catid, ppcode, specinfo };
			String[] arg = { "code", "gz", "catid", "ppcode", "specinfo" };

			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, Language.apply("未找到此优惠信息"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsPopDef.ref);

				if (v.size() > 0)
				{
					for (int i = 0; i < v.size(); i++)
					{
						String[] row = (String[]) v.elementAt(i);

						GoodsPopDef gpd = new GoodsPopDef();

						if (Transition.ConvertToObject(gpd, row))
						{
							yhList.add(gpd);
						}
						else
						{
							return;
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
		}
	}

	public boolean sendBankLog(BankLogDef bcd)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			if (!GlobalInfo.isOnline) { return false; }

			cmdHead = new CmdHead(CmdDef.SENDBANKCARD);
			line.append(cmdHead.headToString() + Transition.ConvertToXML(bcd));

			result = HttpCall(line, Language.apply("金卡日志上传失败!"));

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
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
	}

	public boolean getRefundMoney(String mkt, String syjh, long fphm, RefundMoneyDef rmd)
	{
		return this.getRefundMoney(mkt, syjh, fphm, rmd, CmdDef.GETREFUNDMONEY);
	}

	// 得到人员信息
	public boolean getRefundMoney(String mkt, String syjh, long fphm, RefundMoneyDef rmd, int cmdcode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { mkt, GlobalInfo.sysPara.jygs, syjh, String.valueOf(fphm) };
		String[] args = { "mkt", "jygs", "syjh", "fphm" };

		try
		{
			head = new CmdHead(cmdcode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(cmdcode), line, Language.apply("联网计算退货扣回金额失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, RefundMoneyDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(rmd, row)) { return true; }
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

	public boolean getCustomerSellJf(String[] row, String mktcode, String syjh, String fphm)
	{
		return getCustomerSellJf(row, mktcode, syjh, fphm, "", "");
	}

	public boolean getCustomerSellJf(String[] row, String mktcode, String syjh, String fphm, String hykh, String hytype)
	{
		return getCustomerSellJf(row, mktcode, syjh, fphm, hykh, hytype, getMemCardHttp(CmdDef.GETCUSTSELLJF));
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
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "curJf", "Jf", "memo", "num1" });

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

	public boolean sendHykJf(SaleHeadDef saleHead)
	{
		return sendHykJf(saleHead, getMemCardHttp(CmdDef.SENDHYKJF));
	}

	public boolean sendHykJf(SaleHeadDef saleHead, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;

		CmdHead head = null;
		String[] values = { saleHead.mkt, saleHead.syjh, saleHead.syyh, String.valueOf(saleHead.fphm), saleHead.hykh, String.valueOf(saleHead.bcjf), String.valueOf(saleHead.ljjf), saleHead.str5,
			// 增加以下两个字段，兼容以前版本
		String.valueOf(saleHead.ysje), // R5,百货均需发送本笔应收金额
		(SellType.ISSALE(saleHead.djlb) ? "0" : "3") }; // 标识增-加减积分

		String[] args = { "mktcode", "syjh", "syyh", "fphm", "hykh", "bcjf", "ljjf", "str5", "ysje", "djlb" };

		try
		{
			saleHead.ljjf = 0;

			head = new CmdHead(CmdDef.SENDHYKJF);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(http, line, Language.apply("发送会员积分至会员服务器同步失败!"));

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "ljjf", "memo" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					saleHead.ljjf = Double.parseDouble(row1[0]);
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

	public boolean getSellRealFQ(String[] row, String mktcode, String syjh, String fphm)
	{
		return getSellRealFQ(row, mktcode, syjh, fphm, getMemCardHttp(CmdDef.GETSELLREALFQ));
	}

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

			result = HttpCall(http, line, Language.apply("计算本笔交易小票返券失败\n请到会员中心查询返券"));

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "faq", "fbq", "memo" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					row[0] = row1[0];
					row[1] = row1[1];
					row[2] = row1[2];
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

	public boolean findVIPZKL(CustomerVipZklDef zklDef, String custcode, String custtype, GoodsDef gd)
	{
		return findVIPZKL(zklDef, custcode, custtype, gd, getMemCardHttp(CmdDef.GETCRMVIPZK));
	}

	public boolean findVIPZKL(CustomerVipZklDef zklDef, String custcode, String custtype, GoodsDef gd, Http http)
	{
		if (!GlobalInfo.isOnline)
			return false;

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { custcode, custtype, GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, gd.code, gd.gz, gd.uid, gd.catid, gd.ppcode, gd.specinfo, gd.barcode };
		String[] args = { "custcode", "custtype", "mktcode", "jygs", "code", "gz", "uid", "catid", "ppcode", "specinfo", "barcode" };

		try
		{
			head = new CmdHead(CmdDef.GETCRMVIPZK);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(http, line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, CustomerVipZklDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(zklDef, row)) { return true; }
				}
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
		}

		return false;
	}

	public double findVIPMaxSl(String findtype, String custcode, String custtype, long seqno, String code, String gz, String uid)
	{
		return findVIPMaxSl(findtype, custcode, custtype, seqno, code, gz, uid, getMemCardHttp(CmdDef.FINDVIPMAXSL));
	}

	public double findVIPMaxSl(String findtype, String custcode, String custtype, long seqno, String code, String gz, String uid, Http http)
	{
		if (!GlobalInfo.isOnline)
			return 0;

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { findtype, custcode, custtype, GlobalInfo.sysPara.mktcode, String.valueOf(seqno), code, gz, uid };
		String[] args = { "findtype", "custcode", "custtype", "mktcode", "seqno", "code", "gz", "uid" };

		try
		{
			head = new CmdHead(CmdDef.FINDVIPMAXSL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(http, line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "maxsl" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					return Convert.toDouble(row[0]);
				}
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
		}

		return 0;
	}

	public boolean getSellJfList(Vector v, String mktcode, String syjh, String fphm, String iscd, Http http)
	{
		return getSellJfList(v, mktcode, syjh, fphm, iscd, http, CmdDef.GETSALEJFLIST);
	}

	public boolean getSellJfList(Vector v, String mktcode, String syjh, String fphm, String iscd, Http http, int cmdCode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { mktcode, GlobalInfo.sysPara.jygs, syjh, fphm, iscd };
		String[] args = { "mktcode", "jygs", "syjh", "fphm", "cdbz" };

		try
		{
			head = new CmdHead(cmdCode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(http, line, Language.apply("查询定向积分明细失败\n请到会员中心察看是否存在赠品!"));

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, GiftGoodsDef.ref);

				GiftGoodsDef def = null;
				for (int i = 0; i < vi.size(); i++)
				{
					def = new GiftGoodsDef();

					String[] row = (String[]) vi.elementAt(i);

					if (Transition.ConvertToObject(def, row))
					{
						v.add(def);
					}
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

	public boolean getSaleTicketMSInfo(Vector v, String mktcode, String syjh, String fphm, String iscd, Http http)
	{
		return getSaleTicketMSInfo(v, mktcode, syjh, fphm, iscd, http, CmdDef.GETMSINFO);
	}

	public boolean getSaleTicketMSInfo(Vector v, String mktcode, String syjh, String fphm, String iscd, Http http, int cmdCode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		System.out.println(iscd);
		String[] values = { mktcode, GlobalInfo.sysPara.jygs, syjh, fphm, iscd };
		String[] args = { "mktcode", "jygs", "syjh", "fphm", "cdbz" };
		
		try
		{
			head = new CmdHead(cmdCode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(http, line, Language.apply("查询促销赠送信息失败\n请到会员中心察看是否存在赠品!"));

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, GiftGoodsDef.ref);

				GiftGoodsDef def = null;
				for (int i = 0; i < vi.size(); i++)
				{
					def = new GiftGoodsDef();

					String[] row = (String[]) vi.elementAt(i);

					if (Transition.ConvertToObject(def, row))
					{
						v.add(def);
					}
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

	public Vector getGoodsPayRule(GoodsDef gd)
	{
		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		Vector payrulevec = null;

		cmdHead = new CmdHead(CmdDef.FINDGOODSPAYRULE);

		String[] value = { GlobalInfo.sysPara.mktcode, gd.code, gd.gz, gd.uid, gd.ppcode, gd.catid, gd.barcode };
		String[] arg = { "mktcode", "code", "gz", "uid", "ppcode", "catid", "barcode" };

		line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

		result = HttpCall(line, "");

		if (result == 0)
		{
			Vector v = new XmlParse(line.toString()).parseMeth(0, PayRuleDef.ref);

			if (v.size() > 0)
			{
				payrulevec = new Vector();

				for (int i = 0; i < v.size(); i++)
				{
					String[] row = (String[]) v.elementAt(i);

					PayRuleDef prd = new PayRuleDef();

					if (Transition.ConvertToObject(prd, row))
					{
						payrulevec.add(prd);
					}
				}

				return payrulevec;
			}

			return null;
		}

		return null;
	}

	public boolean getCreditCardZK(CustFilterDef filter, String code, String track, String bankno, String gz, String catid, String ppcode, String specialInfo, String djlb)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { code, track, bankno, GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, gz, catid, ppcode, specialInfo, djlb };
		String[] args = { "code", "track", "bankno", "mktcode", "jygs", "gz", "catid", "ppcode", "specinfo", "djlb" };

		try
		{
			head = new CmdHead(CmdDef.FINDCREDITZK);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(getMemCardHttp(CmdDef.FINDCREDITZK), line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "zk", "billno", "num1", "num2", "str1", "str2", "memo" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					filter.zkl = Double.parseDouble(row[0]);

					filter.desc = row[1];
					filter.str2 = row[6];
					//获得折扣最大限额 by liufangzhou 2014-09-18
					filter.num1 =Double.parseDouble(row[2]);

					return true;
				}
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
		}

		return false;
	}

	public boolean getCreditCardList(Vector v1, String mktcode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { mktcode };
		String[] args = { "mktcode" };

		try
		{
			head = new CmdHead(CmdDef.FINDCREDITLIST);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(getMemCardHttp(CmdDef.FINDCREDITLIST), line, Language.apply("没有找到银联解析规则"));

			if (result == 0)
			{
				//<root><table><row><desc>中信银行</desc><bankno>0001</bankno><trackno>1</trackno><strpos>1</strpos><length>8</length><memo> </memo>
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "desc", "bankno", "trackno", "strpos", "length", "memo" });

				for (int i = 0; i < v.size(); i++)
				{
					String[] row = (String[]) v.elementAt(i);

					CustFilterDef filter = new CustFilterDef();
					filter.desc = row[0].trim();
					filter.TrackFlag = row[1].trim();
					filter.Trackno = Convert.toInt(row[2].trim());
					filter.Trackpos = Convert.toInt(row[3].trim());
					filter.Tracklen = row[4].trim();

					v1.add(filter);
				}

				if (v.size() > 0)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
		}

		return false;
	}

	// 发送挂单信息
	public boolean sendSaleGd(SaleHeadDef salehead, Vector saleGoods)
	{
		SaleGoodsDef saleGoodsDef = null;

		if (!GlobalInfo.isOnline) { return false; }

		try
		{
			CmdHead aa = null;
			int result = -1;
			aa = new CmdHead(CmdDef.SENDSALEGD);

			// 单头打XML
			String line = Transition.ItemDetail(salehead, SaleHeadDef.ref);
			line = Transition.closeTable(line, "SaleGdHeadDef", 1);

			// 小票明细
			String line1 = "";

			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				line1 += Transition.ItemDetail(saleGoodsDef, SaleGoodsDef.ref);
			}

			line1 = Transition.closeTable(line1, "saleGoodsDef", saleGoods.size());

			// 合并
			line = Transition.getHeadXML(line + line1);

			StringBuffer line2 = new StringBuffer();
			line2.append(aa.headToString() + line);

			//
			result = HttpCall(line2, Language.apply("上传挂单于信息失败!"));

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
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
			return false;
		}
		finally
		{
			saleGoodsDef = null;
		}
	}

	public boolean delSaleGdInfo(String syjh, String invno)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh, invno };
		String[] args = { "syjh", "invno" };

		try
		{
			// 删除挂单信息
			line.delete(0, line.length());
			head = new CmdHead(CmdDef.DELSALEGDINFO);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				// 删除失败但解挂还是成功的
				new MessageBox(Language.apply("删除解挂单失败!"));
			}

			return true;
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
	}

	public boolean getSaleGdInfo(String invno, SaleHeadDef salegdhead, Vector salegdgoods)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { invno, String.valueOf(salegdhead.djlb) };
		String[] args = { "invno", "djlb" };

		try
		{
			// 查询挂单头
			head = new CmdHead(CmdDef.GETSALEGDHEAD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("挂单小票头查询失败!"));
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleHeadDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到挂单头,不存在该挂单!"));
				return false;
			}

			String[] row = (String[]) v.elementAt(0);

			if (!Transition.ConvertToObject(salegdhead, row))
			{
				salegdhead = null;
				new MessageBox(Language.apply("挂单头转换失败!"));
				return false;
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询挂单明细
			line.delete(0, line.length());
			head = new CmdHead(CmdDef.GETSALEGDDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox(Language.apply("挂单明细查询失败!"));
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() < 1)
			{
				new MessageBox(Language.apply("没有查询到挂单明细,挂单不存在!"));
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				SaleGoodsDef sgd = new SaleGoodsDef();

				if (Transition.ConvertToObject(sgd, row))
				{
					salegdgoods.add(sgd);
				}
				else
				{
					salegdgoods.clear();
					salegdgoods = null;

					new MessageBox(Language.apply("挂单明细转换失败!"));

					return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			salegdhead = null;

			if (salegdgoods != null)
			{
				salegdgoods.clear();
				salegdgoods = null;
			}

			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
	}

	// 检查注册款机数
	public Vector checkRegisterCount(String mkt, String syjh, String regcode)
	{
		if (!GlobalInfo.isOnline) { return null; }

		String[] values = { mkt, syjh, regcode };
		String[] args = { "mkt", "syjh", "regcode" };

		try
		{
			CmdHead aa = null;

			int result = -1;

			aa = new CmdHead(CmdDef.CHECKREGCODE);

			// 单头打XML
			String line = Transition.ItemDetail(values, args);
			line = Transition.closeTable(line, "UpdateRegCode", 1);

			// 单体打XML
			String line1 = Transition.ItemDetail(values, args);
			line1 = Transition.closeTable(line1, "SelectRegCode", 1);

			// 合并
			line = Transition.getHeadXML(line + line1);

			StringBuffer line2 = new StringBuffer();
			line2.append(aa.headToString() + line);

			//
			result = HttpCall(line2, Language.apply("检查款机注册数量失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line2.toString()).parseMeth(-1, new String[] { "syjh", "regcode" });
				return v;
			}
			else
			{
				return null;
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();

			return null;
		}
	}
	
	public int sendSaleBill(SaleHeadDef saleHead, Vector saleGoods)
	{
		SaleGoodsDef saleGoodsDef = null;

		if (!GlobalInfo.isOnline)
			return -1;

		try
		{
			CmdHead aa = null;
			aa = new CmdHead(CmdDef.XMX_SENDPRESELL);

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

			line = Transition.getHeadXML(line + line1);

			StringBuffer line3 = new StringBuffer();
			line3.append(aa.headToString() + line);

			return HttpCall(line3, "预上传小票失败!");

		}
		catch (Exception er)
		{
			er.printStackTrace();
			return -1;
		}
	}

	public boolean getR5SaleCoupon(String cardno, String paycode, R5CouponDef ret)
	{
		if (!GlobalInfo.isOnline)
			return false;

		StringBuffer line = new StringBuffer();
		int result = -1;

		CmdHead head = null;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.syjStatus.syjh, GlobalInfo.syjStatus.fphm + "", GlobalInfo.sysPara.jygs, cardno, paycode };
		String[] args = { "mktcode", "syjh", "fphm", "jygs", "couponno", "paycode" };

		try
		{
			head = new CmdHead(CmdDef.XMX_SELLCOUPON);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "计算券付款失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, R5CouponDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(ret, row))
						return true;
				}
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}
	// 发送销售附加信息
	public boolean sendSaleAppend(Vector saleappend)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		String line = "";
		int result = -1;

		try
		{
			aa = new CmdHead(CmdDef.SENDSALEAPPEND);

			for (int i = 0; i < saleappend.size(); i++)
			{
				SaleAppendDef sad = (SaleAppendDef) saleappend.get(i);
				line = line + Transition.ItemDetail(sad, SaleAppendDef.ref);
			}

			line = Transition.closeTable(line, "SaleAppendDef", saleappend.size());

			StringBuffer line1 = new StringBuffer();
			line1.append(aa.headToString() +Transition.getHeadXML( line));
		
			
			result = HttpCall(line1, "");

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
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
		}

		return false;
	}

	/*
	 * 从网上取一个值 type = 1时,表示取最大缴款单号,从memo1返回该单号
	 */
	public String[] getOneCommonValues(char type, String syjh, String paravalue1, String paravalue2)
	{
		if (!GlobalInfo.isOnline) { return null; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, String.valueOf(type), syjh, paravalue1, paravalue2 };
		String[] args = { "mkt", "jygs", "type", "syjh", "paravalue1", "paravalue2" };

		try
		{
			head = new CmdHead(CmdDef.GETONECOMMONVALUES);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(getMemCardHttp(CmdDef.GETONECOMMONVALUES), line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "memo1", "memo2", "memo3" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);
					return row;// [0];
				}
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
		}

		return null;
	}

	public boolean getOldCheckInfo(Vector v, String djbh, String gz, String pdrq, String md, int cmdCode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { GlobalInfo.sysPara == null ? "" : GlobalInfo.sysPara.mktcode, djbh, gz, pdrq, md };
		String[] args = { "mkt", "djbh", "gz", "pdrq", "md" };

		try
		{
			head = new CmdHead(cmdCode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, Language.apply("查找盘点单失败"));

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
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();

			return false;
		}
	}

	// 团购信息
	public boolean getGroupBuyInfo(String fphm, Vector saleDetailList)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara == null ? "" : GlobalInfo.sysPara.mktcode, fphm };
		String[] args = { "mkt", "code" };

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

	public boolean getGoodsStockList(ArrayList listgoods, int c, String code1)
	{
		return false;
	}

	public boolean findStampYh(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String ppcode, String spec, String time, String yhtime, String stampCode, Http http, int cmdcode)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, code, gz, catid, ppcode, spec, time, yhtime, stampCode };
		String[] args = { "mktcode", "jygs", "code", "gz", "catid", "ppcode", "spec", "time", "yhtime", "stampcode" };

		try
		{
			head = new CmdHead(cmdcode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// result = HttpCall(line, "鏌ユ壘鍗拌姳淇冮攢瑙勫垯澶辫触!");
			result = HttpCall(line, "");

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

	public boolean getGoodsjfrule(String code, String gz, String catid, String ppid, String yhsj, String cardno, GoodsJFRule gjfrule)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.jygs, GlobalInfo.sysPara.mktcode, code, gz, catid, ppid, yhsj, cardno };
		String[] args = { "jygs", "mktcode", "code", "gz", "catid", "ppid", "yhsj", "cardno" };

		try
		{
			head = new CmdHead(CmdDef.GETGOODSJFRULE);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(CmdDef.GETGOODSJFRULE), line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsJFRule.ref);

				if (v.size() > 0)
				{
					String[] lines = (String[]) v.elementAt(0);
					if (Transition.ConvertToObject(gjfrule, lines)) { return true; }
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

	// 获取买积分规则
	public Vector findJf(String code, String saletype)
	{
		if (!GlobalInfo.isOnline) { return null; }

		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.GETJF);

			String[] value = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs, ConfigClass.CashRegisterCode, code + " ", saletype };
			String[] arg = { "mktcode", "jygs", "syjh", "code", "djlb" };
			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(getMemCardHttp(CmdDef.GETJF), line, Language.apply("未找到此积分规则号"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "code", "desc", "je" });
				return v;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			cmdHead = null;
			line = null;
		}

		return null;
	}
	
	public boolean sendEvaluation(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector evaluation)
	  {
	    if (!GlobalInfo.isOnline) return false;
	    String[] s = (String[])evaluation.elementAt(0);

	    CmdHead cmdHead = null;
	    StringBuffer line = new StringBuffer();
	    int result = -1;
	    try
	    {
	      cmdHead = new CmdHead(CmdDef.sendEvaluation);

	      String[] value = { ConfigClass.CashRegisterCode, String.valueOf(saleHead.fphm), saleHead.djlb, GlobalInfo.sysPara.mktcode, String.valueOf(GlobalInfo.syjStatus.bc), saleHead.rqsj, saleHead.syyh, saleHead.hykh, "", s[2], "", "", "", "", s[1], "", "", "", "" };
	      String[] arg = { "syjh", "fphm", "djlb", "mktcode", "bc", "rqsj", "syyh", "hykh", "memo", "str1", "str2", "str3", "str4", "str5", "num1", "num2", "num3", "num4", "num5" };
	      line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

	      result = HttpCall(getMemCardHttp(135), line, Language.apply("发送顾客评价失败!"));

	      if (result == 0)
	      {
	        return true;
	      }
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	    finally
	    {
	      cmdHead = null;
	      line = null;
	    }
	    return false;
	  }
	
	//支付宝
	public boolean selectOutId(String outId) {
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = {outId};
		String[] args = {"spayno"};
		boolean flag = false;

		try
		{
			aa = new CmdHead(999);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0,new String[]{"retcount"});
				System.out.println(v.elementAt(0));
				if (v.size() > 0) {
					String[] row = (String[]) v.elementAt(0);
					//1为查询到订单号  0为没有查询到
					if("1".equals(row[0]))
					{
						flag = false;
						//return true;
					}
					else
					{
						flag = true;
					}
				}
				
			}
			else
			{
				flag =false;
				//return false;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			flag =false;
			//return false;
		}
		return flag;
	}
	
	public static void main(String [] args)
	{
		NetService n = new NetService();
		n.selectOutId("");
	}
	
}
