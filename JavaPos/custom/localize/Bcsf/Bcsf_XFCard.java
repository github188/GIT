package custom.localize.Bcsf;

import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Vector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Communication.HttpService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.BankLogDef;

public class Bcsf_XFCard
{
	private static Bcsf_XFCard instance = new Bcsf_XFCard();
	private HttpService httpService = new HttpService();

	public static Bcsf_XFCard getDefault()
	{
		return instance;
	}

	public void query(String eticket, String epwd, BankLogDef bld)
	{
		XFQuery query = new XFQuery();
		query.setETicket(eticket);
		query.setEPwd(epwd);

		String retString = communication(query.getRequest());

		if (retString == null || retString.trim().equals(""))
			return;

		query.parseJson(retString);

		if (!query.getRet().equals("200"))
		{
			bld.retmsg = query.getMsg();
			new MessageBox(bld.retmsg);
			return;
		}

		new MessageBox("当前余额:" + query.getBalance());

		bld.retcode = "00";
		bld.retmsg = query.getMsg();
		bld.cardno = eticket;
		bld.kye = Convert.toDouble(query.getBalance());

	}

	public boolean sale(String eticket, String epwd, String Amount, BankLogDef bld)
	{
		XFSale sale = new XFSale();
		sale.setETicket(eticket);
		sale.setEPwd(epwd);
		sale.setAmount(Amount);
		sale.setOrderSn(String.valueOf(GlobalInfo.syjStatus.fphm));

		String retString = communication(sale.getRequest());

		if (retString == null || retString.trim().equals(""))
			return false;

		sale.parseJson(retString);

		if (!sale.getRet().equals("200"))
		{
			bld.retmsg = sale.getMsg();
			new MessageBox(bld.retmsg);
			return false;
		}

		bld.retcode = "00";
		bld.retmsg = sale.getMsg();
		bld.cardno = eticket;
		bld.je = Convert.toDouble(Amount);
		bld.oldrq = sale.getCreated();
		bld.authno = sale.getTransactionId();

		return true;
	}

	public boolean checkAmount(String orderSn, String startTime, String endTime, BankLogDef bld)
	{
		XFCheckAccount checkAccount = new XFCheckAccount();
		checkAccount.setOrderSn(orderSn);
		checkAccount.setStartTime(startTime);
		checkAccount.setEndTime(endTime);

		String retString = communication(checkAccount.getRequest());

		if (retString == null || retString.trim().equals(""))
			return false;

		Vector detail = new Vector();

		checkAccount.parseJson(retString, detail);

		if (!checkAccount.getRet().equals("200"))
		{
			bld.retmsg = checkAccount.getMsg();
			new MessageBox(bld.retmsg);
			return false;
		}

		writePrint(detail);

		bld.retcode = "00";
		bld.retmsg = checkAccount.getMsg();
		return true;
	}

	public boolean writePrint(Vector list)
	{
		try
		{
			PrintWriter pw = null;
			String printName = "c:\\JavaPOS\\xfjz.txt";

			// 先删除上次交易数据文件
			if (PathFile.fileExist(printName))
				PathFile.deletePath(printName);

			try
			{
				pw = CommonMethod.writeFileUTF(printName);

				if (pw != null)
				{
					pw.println(billContent(list));
					pw.flush();
				}
			}
			finally
			{
				if (pw != null)
				{
					pw.close();
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("写入打印数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
	}

	private String billContent(Vector list)
	{
		StringBuffer br = new StringBuffer();
		try
		{
			br.append("          薪福卡消费对账单" + "\n");
			br.append("=====================================" + "\n");
			br.append("商户:" + GlobalInfo.sysPara.mktname + "   ");
			br.append("机号:" + GlobalInfo.syjDef.syjh + "   ");
			br.append("工员:" + GlobalInfo.posLogin.gh + "\n");
			br.append("-------------------------------------\n");

			for (int i = 0; i < list.size(); i++)
			{
				String[] item = (String[]) list.get(i);
				if (item.length > 0)
					br.append("小票号:" + item[0] + "      ");
				if (item.length > 3)
					br.append("金额:" + item[3] + "\n");
				if (item.length > 2)
					br.append("交易ID:\n" + item[2] + "\n");
				if (item.length > 1)
					br.append("交易日期:" + item[1] + "\n");

				br.append("-------------------------------------\n");
			}

			return br.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
	}

	public static void main(String[] args)
	{
		String sign = "hello,world";
		sign = Bcsf_XFCard.encryptMD5(sign);
		System.out.print(sign);

		String ret = "{Ret:200,Msg:null,Data:{ResultCount:1,Info:[{OrderSn:333,Created:2222,TransactionId:32323,Amount:100}]}}";
		Bcsf_XFCard.parseJson(ret);
	}

	private String communication(String request)
	{
		try
		{
			return httpService.httpGet(GlobalInfo.sysPara.xfcardsrvurl + request);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static String encryptMD5(String sign)
	{
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try
		{
			byte[] srcSign = sign.getBytes();
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(srcSign);
			byte[] encrypt = md5.digest();

			int len = encrypt.length;
			char str[] = new char[len * 2];
			int i = 0;
			for (int j = 0; j < len; j++)
			{
				byte tmp = encrypt[j];
				str[i++] = hexDigits[tmp >>> 4 & 0xf];
				str[i++] = hexDigits[tmp & 0xf];
			}

			return new String(str).toLowerCase();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static boolean parseJson(String line)
	{
		JSONObject content = JSONObject.fromObject(line);
		String dataStr = content.getString("Data");
		content = JSONObject.fromObject(dataStr);

		String count = content.getString("ResultCount");

		JSONArray jsonDetail = content.getJSONArray("Info");
		Vector detail = new Vector();

		for (int i = 0; i < Convert.toInt(count); i++)
		{
			JSONObject array = jsonDetail.getJSONObject(i);
			String[] info = new String[4];

			info[0] = array.getString("OrderSn");
			info[1] = array.getString("Created");
			info[2] = array.getString("TransactionId");
			info[3] = array.getString("Amount");

			detail.add(info);
		}

		return true;
	}

	public String getErrMsg(String retcode)
	{
		if (retcode.equals("200"))
			return "返回成功";
		else if (retcode.equals("301"))
			return "参数错误";
		else if (retcode.equals("301.1"))
			return "参数数量错误";
		else if (retcode.equals("301.2"))
			return "参数类型错误";
		else if (retcode.equals("301.3"))
			return "参数值错误";
		else if (retcode.equals("403"))
			return "数据验证错误";
		else if (retcode.equals("404"))
			return "请求接口不存在";
		else if (retcode.equals("404.1"))
			return "服务不存在";
		else if (retcode.equals("404.2"))
			return "方法不存在";
		else if (retcode.equals("500"))
			return "Internal Server Error";
		else
			return "未知错误";

	}

	class BaseInfo
	{
		protected String MerchantNo;
		protected String MPwd;
		protected String Method;
		protected String Sign;

		protected String Ret;
		protected String Msg;

		public BaseInfo(String method)
		{
			this.MerchantNo = GlobalInfo.sysPara.xfcardmerchantno;
			this.MPwd = GlobalInfo.sysPara.xfcardmpwd;
			this.Method = method;
			this.Ret = "";
			this.Msg = "未知错误";
		}
	}

	class XFQuery
	{
		private BaseInfo base;
		private String ETicket;
		private String EPwd;

		private String Balance;

		public XFQuery()
		{
			base = new BaseInfo("eticket.info");
		}

		public String getRequest()
		{
			// ArrayList list = new ArrayList();

			StringBuffer req = new StringBuffer();
			setSign();

			/*
			 * list.add(httpService.obtainParameter("MerchantNo",
			 * base.MerchantNo)); list.add(httpService.obtainParameter("MPwd",
			 * base.MPwd)); list.add(httpService.obtainParameter("ETicket",
			 * ETicket)); list.add(httpService.obtainParameter("EPwd", EPwd));
			 * list.add(httpService.obtainParameter("Method", base.Method));
			 * list.add(httpService.obtainParameter("Sign", base.Sign));
			 * 
			 * return list;
			 */

			req.append("MerchantNo=");
			req.append(base.MerchantNo);
			req.append("&");

			req.append("MPwd=");
			req.append(base.MPwd);
			req.append("&");

			req.append("ETicket=");
			req.append(ETicket);
			req.append("&");

			req.append("EPwd=");
			req.append(EPwd);
			req.append("&");

			req.append("Method=");
			req.append(base.Method);
			req.append("&");

			req.append("Sign=");
			req.append(base.Sign);

			return req.toString();

		}

		public void setSign()
		{
			String signStr = base.MerchantNo + base.MPwd + ETicket + EPwd + base.Method + base.MerchantNo;
			base.Sign = encryptMD5(signStr);
		}

		public boolean parseJson(String line)
		{
			try
			{
				JSONObject content = JSONObject.fromObject(line);
				base.Ret = content.getString("Ret");
				base.Msg = content.getString("Msg");

				String dataStr = content.getString("Data");
				if (dataStr == null || dataStr.trim().equals(""))
					return false;

				content = JSONObject.fromObject(dataStr);
				Balance = content.getString("Balance");

				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}

		public String getRet()
		{
			return base.Ret;
		}

		public String getMsg()
		{
			return base.Msg;
		}

		public String getBalance()
		{
			return Balance;
		}

		public void setETicket(String eTicket)
		{
			ETicket = eTicket;
		}

		public void setEPwd(String ePwd)
		{
			EPwd = ePwd;
		}

	}

	class XFSale
	{
		private BaseInfo base;
		private String ETicket;
		private String EPwd;
		private String Amount;
		private String OrderSn;

		private String Created;
		private String TransactionId;

		public XFSale()
		{
			base = new BaseInfo("eticket.use");
		}

		public boolean parseJson(String line)
		{
			try
			{
				JSONObject content = JSONObject.fromObject(line);
				base.Ret = content.getString("Ret");
				base.Msg = content.getString("Msg");
				String dataStr = content.getString("Data");

				if (dataStr == null || dataStr.trim().equals(""))
					return false;

				content = JSONObject.fromObject(dataStr);
				Created = content.getString("Created");
				TransactionId = content.getString("TransactionId");

				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}

		public String getRet()
		{
			return base.Ret;
		}

		public String getMsg()
		{
			return base.Msg;
		}

		public String getCreated()
		{
			return Created;
		}

		public String getTransactionId()
		{
			return TransactionId;
		}

		public String getETicket()
		{
			return ETicket;
		}

		public void setETicket(String eTicket)
		{
			ETicket = eTicket;
		}

		public String getEPwd()
		{
			return EPwd;
		}

		public void setEPwd(String ePwd)
		{
			EPwd = ePwd;
		}

		public String getAmount()
		{
			return Amount;
		}

		public void setAmount(String amount)
		{
			Amount = amount;
		}

		public String getOrderSn()
		{
			return OrderSn;
		}

		public void setOrderSn(String orderSn)
		{
			OrderSn = orderSn;
		}

		public void setSign()
		{
			String signStr = base.MerchantNo + base.MPwd + ETicket + EPwd + Amount + OrderSn + base.Method + base.MerchantNo;
			base.Sign = encryptMD5(signStr);
		}

		public String getRequest()
		{

			// ArrayList list = new ArrayList();

			StringBuffer req = new StringBuffer();

			setSign();

			/*
			 * list.add(httpService.obtainParameter("MerchantNo",
			 * base.MerchantNo)); list.add(httpService.obtainParameter("MPwd",
			 * base.MPwd)); list.add(httpService.obtainParameter("ETicket",
			 * ETicket)); list.add(httpService.obtainParameter("EPwd", EPwd));
			 * list.add(httpService.obtainParameter("Amount", Amount));
			 * list.add(httpService.obtainParameter("OrderSn", OrderSn));
			 * list.add(httpService.obtainParameter("Method", base.Method));
			 * list.add(httpService.obtainParameter("Sign", base.Sign));
			 * 
			 * return list;
			 */

			req.append("MerchantNo=");
			req.append(base.MerchantNo);
			req.append("&");

			req.append("MPwd=");
			req.append(base.MPwd);
			req.append("&");

			req.append("ETicket=");
			req.append(ETicket);
			req.append("&");

			req.append("EPwd=");
			req.append(EPwd);
			req.append("&");

			req.append("Amount=");
			req.append(Amount);
			req.append("&");

			req.append("OrderSn=");
			req.append(OrderSn);
			req.append("&");

			req.append("Method=");
			req.append(base.Method);
			req.append("&");

			req.append("Sign=");
			req.append(base.Sign);

			return req.toString();

		}
	}

	class XFCheckAccount
	{
		private BaseInfo base;
		private String OrderSn;
		private String StartTime;
		private String EndTime;

		public XFCheckAccount()
		{
			base = new BaseInfo("eticket.recon");
		}

		public boolean parseJson(String line, Vector list)
		{
			JSONObject content = JSONObject.fromObject(line);
			base.Ret = content.getString("Ret");
			base.Msg = content.getString("Msg");

			String dataStr = content.getString("Data");
			content = JSONObject.fromObject(dataStr);

			String count = content.getString("ResultCount");

			JSONArray jsonDetail = content.getJSONArray("Info");

			for (int i = 0; i < Convert.toInt(count); i++)
			{
				JSONObject array = jsonDetail.getJSONObject(i);
				String[] info = new String[4];

				info[0] = array.getString("OrderSn");
				info[1] = array.getString("Created");
				info[2] = array.getString("TransactionId");
				info[3] = array.getString("Amount");

				list.add(info);
			}
			return true;
		}

		public String getRet()
		{
			return base.Ret;
		}

		public String getMsg()
		{
			return base.Msg;
		}

		public void setOrderSn(String orderSn)
		{
			OrderSn = orderSn;
		}

		public void setStartTime(String startTime)
		{
			StartTime = startTime;
		}

		public void setEndTime(String endTime)
		{
			EndTime = endTime;
		}

		public void setSign()
		{
			String signStr = base.MerchantNo + base.MPwd + (OrderSn.equals("0") ? "" : OrderSn) + StartTime + EndTime + base.Method + base.MerchantNo;
			base.Sign = encryptMD5(signStr);
		}

		public String getRequest()
		{

			// ArrayList list = new ArrayList();
			StringBuffer req = new StringBuffer();
			setSign();

			/*
			 * list.add(httpService.obtainParameter("MerchantNo",
			 * base.MerchantNo)); list.add(httpService.obtainParameter("MPwd",
			 * base.MPwd)); list.add(httpService.obtainParameter("OrderSn",
			 * OrderSn)); list.add(httpService.obtainParameter("StartTime",
			 * StartTime)); list.add(httpService.obtainParameter("EndTime",
			 * EndTime)); list.add(httpService.obtainParameter("Method",
			 * base.Method)); list.add(httpService.obtainParameter("Sign",
			 * base.Sign));
			 * 
			 * return list;
			 */

			req.append("MerchantNo=");
			req.append(base.MerchantNo);
			req.append("&");

			req.append("MPwd=");
			req.append(base.MPwd);
			req.append("&");

			req.append("OrderSn=");
			req.append(OrderSn.equals("0") ? "" : OrderSn);
			req.append("&");

			req.append("StartTime=");
			req.append(StartTime);
			req.append("&");

			req.append("EndTime=");
			req.append(EndTime);
			req.append("&");

			req.append("Method=");
			req.append(base.Method);
			req.append("&");

			req.append("Sign=");
			req.append(base.Sign);

			return req.toString();

		}

	}
}
