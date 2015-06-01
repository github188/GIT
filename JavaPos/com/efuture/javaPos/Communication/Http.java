package com.efuture.javaPos.Communication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Vector;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Global.StatusType;

//construct
public class Http implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String svrurl = null;
	private String address = null;
	private int port = -1;
	private String path = null;
	private int ctimeout = 10000;
	private int rtimeout = 20000;
	private boolean islogichttp = true;
	private String requestCmd = null;
	private String requestString = null;
	private String answerString = null;
	private HttpClient httpClient = null;
	public boolean stopService = false;

	public Http()
	{
	}

	public Http(String url)
	{
		this.svrurl = url;
	}

	public boolean isSameHttp(String url)
	{
		if (this.svrurl != null && this.svrurl.equals(url))
			return true;
		else
			return false;
	}

	public Http(String address, int port, String path)
	{
		this.address = address;
		this.port = port;
		this.path = path;
	}

	// 输入IP地址
	public void setAddress(String address)
	{
		this.address = address;
	}

	// 设置端口
	public void setPort(int port)
	{
		this.port = port;
	}

	// 设置路径
	public void setPath(String path)
	{
		this.path = path;
	}

	// 得到本机IP地址
	public String getIPAddress()
	{
		try
		{
			InetAddress[] IP = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
			for (int i = 0; i < IP.length; i++)
			{
				String ip = IP[i].getHostAddress();

				// System.out.println("IP" + i + " :\t" + ip + "\n");

				// 查找和POSSERVER网段一致的IP
				if (CommonMethod.isValidIPAddress(ConfigClass.ServerIP))
				{
					int p = ConfigClass.ServerIP.indexOf('.', 0);
					p = ConfigClass.ServerIP.indexOf('.', p + 1);
					if (p >= 0 && ConfigClass.ServerIP.substring(0, p).equals(ip.substring(0, p))) { return ip; }
				}
			}
			return IP[0].getHostAddress();
			/*
			 * InetAddress myIp = InetAddress.getLocalHost(); String abc =
			 * myIp.getHostAddress(); int index = abc.indexOf("/"); return
			 * abc.substring(++index);
			 */
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
			return ConfigClass.CashRegisterCode;
		}
	}

	// init and set timeout is 1000 初始化客户端
	public boolean init()
	{
		httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(ctimeout);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(rtimeout);

		return true;
	}

	// 设置连接超时
	public void setConncetTimeout(int n)
	{
		ctimeout = n;
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(n);
	}

	// 设置读取超时
	public void setReadTimeout(int n)
	{
		rtimeout = n;
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(n);
	}

	// 设置发送信息.
	public void setRequestString(String n)
	{
		requestString = n;
	}

	public void setRequestCmdCode(String cmd)
	{
		requestCmd = cmd;
	}

	// 得到返回值
	public String getReply()
	{
		return answerString;
	}

	public void disconncet()
	{

	}

	// 确认URL是否合法
	public boolean check()
	{
		String[] strsp = address.split(".");

		if (address != null)
		{
			for (int i = 0; i < strsp.length; i++)
			{
				if ((strsp[i] == null) | (strsp[i].length() >= 3) | (Integer.parseInt(strsp[i]) < 0) | (Integer.parseInt(strsp[i]) > 255) | (strsp.length < 4)) { return false; }
			}
		}

		if ((address == null) | (port == -1) | (path == null) | (requestString == null))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private String getNormalException(String errmsg, String url)
	{
		// 解析原命令码
		CmdHead head;
		String[] lines = requestString.split(GlobalVar.divisionFlag1);
		String[] body = lines[0].split(GlobalVar.divisionFlag2);
		if (body.length >= 6)
		{
			head = new CmdHead(body);
		}
		else
		{
			head = new CmdHead("2222", "11", "1", "1", "", "");
		}
		head.setBackCode("999");
		head.setErrorMessage(Language.apply("连接服务器失败!\n\n") + errmsg);

		// 提示是否暂停该URL服务访问，以避免服务已停止，下一个命令又访问长时间超时
		if (url != null && new MessageBox(Language.apply("远端服务器可能已经关闭无法访问\n\n是否暂停访问以避免后续请求长时间超时\n\n待需要恢复访问时执行联网操作"), null, true).verify() == GlobalVar.Key1)
		{
			if (GlobalInfo.httpStatus == null)
				GlobalInfo.httpStatus = new Vector();
			GlobalInfo.httpStatus.add(url);
		}

		//
		return head.headToString();
	}

	private String getDisconnectException(String errmsg)
	{
		// 解析原命令码
		CmdHead head;
		String[] lines = requestString.split(GlobalVar.divisionFlag1);
		String[] body = lines[0].split(GlobalVar.divisionFlag2);
		if (body.length >= 6)
		{
			head = new CmdHead(body);
		}
		else
		{
			if (ConfigClass.Market == null || !ConfigClass.Market.equals(""))
			{
				head = new CmdHead("2222", "11", "1", "1", "", "");
			}
			else
			{
				head = new CmdHead(ConfigClass.Market, "11", "1", "1", "", "");
			}
		}

		head.setBackCode("999");
		head.setErrorMessage(Language.apply("连接超时,系统进入脱网状态!\n\n") + errmsg);

		// 记录日志
		if (GlobalInfo.dayDB != null && this != GlobalInfo.timeHttp)
		{
			// 主线程的HTTP对象才进行设置为脱网的操作
			GlobalInfo.isOnline = false;

			// 刷新状态栏
			GlobalInfo.statusBar.setNetStatus();

			// 记录脱网日志
			AccessDayDB.getDefault().writeWorkLog(Language.apply("连接超时,系统进入脱网状态:") + errmsg, StatusType.WORK_SENDERROR);
		}

		//
		return head.headToString();
	}

	public boolean isSendOtherHttp()
	{
		return !islogichttp;
	}

	private String getPostURL()
	{
		String url;

		if (this.svrurl == null)
			url = "http://" + address + ":" + port + path;
		else
			url = this.svrurl;

		// 检查命令是否被定义为例外,要发往其他HTTP服务器
		islogichttp = true;
		if (GlobalInfo.otherHttp != null && GlobalInfo.otherHttp.size() > 0 && this.requestCmd != null && this.requestCmd.trim().length() > 0)
		{
			for (int i = 0; i < GlobalInfo.otherHttp.size(); i++)
			{
				String[] s = (String[]) GlobalInfo.otherHttp.elementAt(i);
				if (s.length > 1 && s[1].indexOf("," + requestCmd + ",") >= 0)
				{
					GlobalInfo.statusBar.setHelpMessage(Language.apply("定向转发{0}号请求,请等待服务器应答......", new Object[] { requestCmd }));
					islogichttp = false; // 发往其他服务器标记为不
					return s[0];
				}
			}
		}

		return url;
	}

	public void convertRequestXML(String value)
	{
		try
		{
			String[] lines = requestString.split(GlobalVar.divisionFlag1);
			if (lines.length < 2)
				return;

			XmlParse x = new XmlParse(lines[1]);
			Document doc = x.getDocument();
			NodeList nList = doc.getElementsByTagName("table");
			for (int i = 0; i < nList.getLength(); i++)
			{
				Element e = (Element) nList.item(i);
				NodeList nll = e.getElementsByTagName("row");
				if (nll.getLength() <= 0)
				{
					// System.out.println(e.getTextContent());

					// 由于1.4Jdk不支持setTextContent方法
					// e.setTextContent("");
					XmlParse.setElementValue(e, "");

					Node nd = doc.createElement("row");
					e.appendChild(nd);
					nll = e.getElementsByTagName("row");
				}
				for (int j = 0; j < nll.getLength(); j++)
				{
					// 加入MKT节点
					Element e1 = (Element) nll.item(j);
					Node nd = doc.createElement(ConfigClass.MarketText);

					// 由于1.4Jdk不支持setTextContent方法,所以改成appendChild方法
					// nd.setTextContent(ConfigClass.Market);

					nd.appendChild(doc.createTextNode(value));
					e1.appendChild(nd);
				}
			}

			requestString = lines[0] + GlobalVar.divisionFlag1 + x.doc2String();
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			ex.printStackTrace();
		}
	}

	// 发送和 接收信息
	public String execute()
	{
		PostMethod postMethod = null;
		String posturl = null;
		boolean logFlag = false;

		try
		{
			// 定义了门店号,总是在请求数据前加入门店号
			if (ConfigClass.Market != null && ConfigClass.Market.trim().length() > 0)
			{
				convertRequestXML(ConfigClass.Market);
			}

			if (ConfigClass.AliasMarket != null && ConfigClass.AliasMarket.trim().length() > 0)
			{
				convertRequestXML(ConfigClass.AliasMarket);
			}

			// 调试模式记录请求串备查
			if (ConfigClass.DebugMode)
				System.out.println("请求:\r\n" + requestString);
			else
			{
				if (ConfigClass.EnableCmdLog != null && ConfigClass.EnableCmdLog.length() > 1)
				{
					if (("," + ConfigClass.EnableCmdLog + ",").indexOf("," + this.requestCmd + ",") != -1)
					{
						PublicMethod.traceCmdLog(Language.apply("请求:\r\n") + requestString);
						logFlag = true;
					}
				}
			}
			// 检查postURL是否被暂停访问,如果暂停访问直接返回失败,避免长时间超时
			stopService = false;
			posturl = getPostURL();
			for (int i = 0; GlobalInfo.httpStatus != null && i < GlobalInfo.httpStatus.size(); i++)
			{
				if (posturl.equalsIgnoreCase((String) GlobalInfo.httpStatus.elementAt(i)))
				{
					stopService = true;
					return getNormalException(Language.apply("客户端暂停POSSERVER访问"), null);
				}
			}

			// 创建请求方法
			postMethod = new PostMethod(posturl);
			postMethod.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");

			// 设置请求数据
			byte[] requestBuff = null;
			requestBuff = requestString.getBytes("UTF-8");
			postMethod.setRequestEntity(new ByteArrayRequestEntity(requestBuff));

			// 发送HTTP请求
			httpClient.executeMethod(postMethod);

			// 读取HTTP应答数据
			// byte[] replay = postMethod.getResponseBody();
			// answerString = new String(replay, "UTF-8");
			String s;
			StringBuffer sb = new StringBuffer();
			InputStream is = postMethod.getResponseBodyAsStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((s = in.readLine()) != null)
			{
				sb.append(s);
				sb.append("\n");
			}
			answerString = sb.toString();

			if (ConfigClass.DebugMode)
				System.out.println("应答:\r\n" + answerString);
			else if (logFlag)
				PublicMethod.traceCmdLog(Language.apply("应答:\r\n") + answerString);

			// 分析应答数据
			if (answerString.indexOf(GlobalVar.divisionFlag1) < 0 && answerString.indexOf(GlobalVar.divisionFlag2) < 0)
			{
				return getNormalException("AnswerString Invalid\n\n" + answerString, null);
			}
			else
			{
				return answerString;
			}
		}
		catch (HttpException e)
		{
			e.printStackTrace();

			// svrurl为null表示本http对象为业务,异常要设置脱网
			// svrurl不为空表示本http对象为其他通讯对象(例如独立的卡服务),异常不设置脱网.
			if (this.svrurl == null && islogichttp)
			{
				return getDisconnectException(e.getMessage());
			}
			else
			{
				return getNormalException(e.getMessage(), posturl);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			// svrurl为null表示本http对象为业务,异常要设置脱网
			// svrurl不为空表示本http对象为其他通讯对象(例如独立的卡服务),异常不设置脱网
			if (this.svrurl == null && islogichttp)
			{
				return getDisconnectException(e.getMessage());
			}
			else
			{
				return getNormalException(e.getMessage(), posturl);
			}
		}
		finally
		{
			if (postMethod != null)
			{
				postMethod.releaseConnection();
				logFlag = false;
			}
		}
	}

	public String getSvrURL()
	{
		if (this.svrurl == null)
			return "http://" + address + ":" + port + path;
		else
			return this.svrurl;
	}
}
