package custom.localize.Ybsj;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.TextBox;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Ybsj.CrmMzkInfo.CrmMzkInfoResult;
import custom.localize.Ybsj.CrmUserLogin.CrmUserLoginResult;
import custom.localize.Ybsj.CrmVipInfo.CrmVipInfoResult;
import custom.localize.Ybsj.CrmVipPoint.CrmVipPointResult;

public class Ybsj_SocketCrmModule
{
	private static Ybsj_SocketCrmModule instance = new Ybsj_SocketCrmModule();

	private boolean isEnableCrm;

	private String sessionId = "";

	private String serverIp = "";
	private int serverPort = 6000;
	private String usrname = "";
	private String usrpwd = "";

	public static Ybsj_SocketCrmModule getDefault()
	{
		return instance;
	}

	public String getXmlHead()
	{
		return "BFCRMXML08";
	}

	public void initCrmConnection()
	{
		if (GlobalInfo.sysPara.cyCrmUrl == null || GlobalInfo.sysPara.cyCrmUrl.trim().equals("") || GlobalInfo.sysPara.cyCrmUrl.trim().indexOf(":") == -1)
		{
			new MessageBox("请设置正确的用于登录Crm的连接地址");
			return;
		}

		if (GlobalInfo.sysPara.cyCrmUsrPwd == null || GlobalInfo.sysPara.cyCrmUsrPwd.trim().equals("") || GlobalInfo.sysPara.cyCrmUsrPwd.trim().indexOf(",") == -1)
		{
			new MessageBox("请设置正确的用于登录Crm的用户名和密码");
			return;
		}

		String[] urlAry = GlobalInfo.sysPara.cyCrmUrl.split(":");
		if (urlAry != null && urlAry.length > 0)
			serverIp = urlAry[0];
		if (urlAry != null && urlAry.length > 1)
			serverPort = Convert.toInt(urlAry[1]);

		String[] usrAry = GlobalInfo.sysPara.cyCrmUsrPwd.split(",");
		if (usrAry != null && usrAry.length > 0)
			usrname = usrAry[0];
		if (usrAry != null && usrAry.length > 1)
			usrpwd = usrAry[1];
	}

	public boolean userLogin(String store, String operator, String machine)
	{
		CrmUserLogin usrLogin = null;
		try
		{
			isEnableCrm = false;

			usrLogin = new CrmUserLogin();

			usrLogin.setApp("0000");
			usrLogin.setStore(store);
			usrLogin.setOperator(operator);
			usrLogin.setBfcrm_user(usrname);
			usrLogin.setPassword(usrpwd);
			usrLogin.setMachine(machine);
			usrLogin.setSystem("bfpos");

			String retXml = readDataFromCrmServer(usrLogin.toXml(getXmlHead()));

			CrmUserLoginResult result = usrLogin.fromXml(retXml);

			if (result == null)
			{
				new MessageBox("登录Crm系统失败");
				return false;
			}
			else if (result.getSuccess().equals("N"))
			{
				new MessageBox(result.getMessage());
				return false;
			}
			else if (result.getSuccess().equals("Y"))
			{
				this.sessionId = result.getSession_id();
				isEnableCrm = true;
				return true;
			}
			else
			{
				new MessageBox("登录Crm系统失败");
				return false;
			}
		}
		catch (Exception ex)
		{
			new MessageBox("登录Crm系统发生异常");
			ex.printStackTrace();
			isEnableCrm = false;
			return false;
		}
	}

	public void userLogoff()
	{
		if (!isEnableCrm)
			return;

		CrmUserLogOff usrLogoff = null;

		try
		{
			usrLogoff = new CrmUserLogOff();

			usrLogoff.setApp("0001");
			usrLogoff.setSession_id(sessionId);

			readDataFromCrmServer(usrLogoff.toXml(getXmlHead()), false);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public boolean queryVipInfo(String track, CustomerDef customer)
	{
		if (!isEnableCrm)
		{
			new MessageBox("Crm功能无法使用");
			return false;
		}

		CrmVipInfo vipInfo = null;
		StringBuffer verifyCode = new StringBuffer();

		try
		{
			if (GlobalInfo.sysPara.cardpasswd.equals("A"))
			{
				TextBox txt = new TextBox();

				if (!txt.open("请输入会员验证码", "VerifyCode", "需先输入验证码后才能查询卡资料", verifyCode, 0, 0, false, TextBox.AllInput))
					return false;
			}

			vipInfo = new CrmVipInfo();

			vipInfo.setApp("0101");
			vipInfo.setTrack_data(track);
			vipInfo.setVerify_code(verifyCode.toString());
			vipInfo.setSession_id(sessionId);

			String retXml = readDataFromCrmServer(vipInfo.toXml(getXmlHead()));

			CrmVipInfoResult result = vipInfo.fromXml(retXml);

			if (result == null)
			{
				new MessageBox("查找会员失败");
				return false;
			}
			else if (result.getSuccess().equals("N"))
			{
				new MessageBox(result.getMessage());
				return false;
			}
			else if (result.getSuccess().equals("Y"))
			{
				customer.code = result.getMember().getCode();
				customer.name = result.getMember().getName();
				customer.track = result.getMember().getId(); // 此ID很重要
				customer.type = result.getMember().getType_code();
				customer.str1 = result.getMember().getType_name(); // 卡类型名称

				customer.ishy = 'Y';
				customer.str2 = result.getMember().getPermit_valuedcard();
				customer.str3 = result.getMember().getPermit_voucher();
				customer.isjf = result.getMember().getCumulate_cent().charAt(0); // 是否能积分功能
				customer.iszk = 'Y';
				customer.zkl = 1;
				customer.ispay = true;
				customer.maxdate = result.getMember().getDate_valid(); // 有效期
				customer.status = "Y";

				customer.num1 = Convert.toDouble(result.getMember().getCent_available()); // 有效积分
				customer.num2 = Convert.toDouble(result.getMember().getCzkye()); // 储值卡余额
				customer.num3 = Convert.toDouble(result.getMember().getYhqye());// 优惠券余额
				
				return true;
			}
			else
			{
				new MessageBox("查找会员失败");
				return false;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("查找会员发生异常");
			return false;
		}
	}

	public boolean queryMzkInfo(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		if (!isEnableCrm)
		{
			new MessageBox("Crm功能无法使用");
			return false;
		}

		CrmMzkInfo mzkInfo = null;
		StringBuffer verifyCode = new StringBuffer();

		try
		{
			if (GlobalInfo.sysPara.cardpasswd.equals("A"))
			{
				TextBox txt = new TextBox();

				if (!txt.open("请输入面值卡验证码", "VerifyCode", "需先输入验证码后才能查询卡资料", verifyCode, 0, 0, false, TextBox.AllInput))
					return false;
			}

			mzkInfo = new CrmMzkInfo();

			mzkInfo.setApp("0105");
			mzkInfo.setSession_id(sessionId);
			mzkInfo.setTrack_data(mzkreq.track2);
			mzkInfo.setVerify_code(verifyCode.toString());
			mzkInfo.setMember_password(mzkreq.passwd);

			String retXml = readDataFromCrmServer(mzkInfo.toXml(getXmlHead()));

			CrmMzkInfoResult result = mzkInfo.fromXml(retXml);

			if (result == null)
			{
				new MessageBox("查找面值卡信息失败");
				return false;
			}
			else if (result.getSuccess().equals("N"))
			{
				new MessageBox(result.getMessage());
				return false;
			}
			else if (result.getSuccess().equals("Y"))
			{
				mzkreq.str1 = result.getMember_id();// ID很重要

				mzkret.str3 = "ID: " + result.getMember_id();
				mzkret.cardno = result.getMember_code();
				mzkret.ye = ManipulatePrecision.doubleConvert(Convert.toDouble(result.getBalance()), 2, 1);
				mzkret.value3 = ManipulatePrecision.doubleConvert(Convert.toDouble(result.getBottom()), 2, 1);
				mzkret.str1 = result.getDate_valid();

				return true;
			}
			else
			{
				new MessageBox("查找面值卡信息失败");
				return false;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("查找会员发生异常");
			return false;
		}
	}

	public String mzkPrepareSale(MzkRequestDef mzkreq)
	{
		CrmPrepareMzkSale preSale = null;
		double money = mzkreq.je;

		try
		{
			preSale = new CrmPrepareMzkSale();

			preSale.setApp("0106");
			preSale.setSession_id(sessionId);
			preSale.setBill_id(String.valueOf(mzkreq.fphm));
			preSale.setDate_account(mzkreq.str2);
			preSale.setCashier(mzkreq.syjh);

			ArrayList card_list = new ArrayList();

			CrmMzkCard card = new CrmMzkCard();
			card.setMember_id(mzkreq.str1);

			if (mzkreq.type.equals("03"))
				money = money * -1;

			card.setAmount(String.valueOf(money));
			card.setRecycle("N");
			card_list.add(card);

			preSale.setCard_list(card_list);

			String retXml = readDataFromCrmServer(preSale.toXml(getXmlHead()), true);

			CrmMzkSaleResult result = preSale.fromXml(retXml);

			if (result == null)
			{
				new MessageBox("准备支付储值卡失败");
				return null;
			}
			else if (result.getSuccess().equals("N"))
			{
				new MessageBox(result.getMessage());
				return null;
			}
			else if (result.getSuccess().equals("Y"))
			{
				mzkreq.str3 = result.getServer_transaction_id();
				return result.getServer_transaction_id();
			}
			else
			{
				new MessageBox("准备支付储值卡失败");
				return null;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("准备支付储值卡失败发生异常");
			return null;
		}
	}

	public boolean mzkConfirmSale(MzkRequestDef mzkreq)
	{
		CrmConfirmMzkSale confirmSale = null;
		double money = mzkreq.je;

		try
		{
			confirmSale = new CrmConfirmMzkSale();
			confirmSale.setApp("0109");
			confirmSale.setStep("confirm");
			confirmSale.setSession_id(sessionId);
			confirmSale.setServer_transaction_id(mzkreq.str3);

			if (mzkreq.type.equals("03"))
				money = money * -1;

			confirmSale.setTotal_amount(String.valueOf(money));

			String retXml = readDataFromCrmServer(confirmSale.toXml(getXmlHead()), true);

			CrmMzkSaleResult result = confirmSale.fromXml(retXml);

			if (result == null)
			{
				new MessageBox("确认支付储值卡失败");
				return false;
			}
			else if (result.getSuccess().equals("N"))
			{
				new MessageBox(result.getMessage());
				return false;
			}
			else if (result.getSuccess().equals("Y"))
			{
				return mzkSaleRetMsg(result.getStatus_before_perform());
			}
			else
			{
				new MessageBox("确认支付储值卡失败");
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("确认支付储值卡发生异常");
			return false;
		}
	}

	public boolean mzkCancelSale(MzkRequestDef mzkreq)
	{
		CrmCancelMzkSale cancelSale = null;
		double money = mzkreq.je;

		try
		{
			cancelSale = new CrmCancelMzkSale();

			cancelSale.setApp("0109");
			cancelSale.setSession_id(sessionId);
			cancelSale.setStep("cancel");
			cancelSale.setServer_transaction_id(mzkreq.str3);

			if (mzkreq.type.equals("03") || mzkreq.type.equals("04"))
				money = money * -1;

			cancelSale.setTotal_amount(String.valueOf(money));

			String retXml = readDataFromCrmServer(cancelSale.toXml(getXmlHead()), true);

			CrmMzkSaleResult result = cancelSale.fromXml(retXml);

			if (result == null)
			{
				new MessageBox("取消储值卡支付失败");
				return false;
			}
			else if (result.getSuccess().equals("N"))
			{
				new MessageBox(result.getMessage());
				return false;
			}
			else if (result.getSuccess().equals("Y"))
			{
				return true;
			}
			else
			{
				new MessageBox("取消储值卡支付失败");
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("取消储值卡支付发生异常");
			return false;
		}
	}

	public boolean mzkSale(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		if (!isEnableCrm)
		{
			new MessageBox("系统还未登录Crm\n暂时无法发起面值卡冲正\n稍后采用面值卡付款时系统将会自动发起");
			return false;
		}

		try
		{
			// 02,04发送冲正
			if (mzkreq.type.equals("02") || mzkreq.type.equals("04"))
				return mzkCancelSale(mzkreq);

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("面值卡消费发生异常");
			return false;
		}
	}

	private boolean mzkSaleRetMsg(String code)
	{
		String defaultMsg = "Crm无返回提示,无法判断交易是否成功";

		if (code == null || code.equals(""))
		{
			new MessageBox(defaultMsg);
			return false;
		}

		if (code.equals("not_exist"))
		{
			new MessageBox("交易不存在-会员支付交易ID有误");
			return false;
		}

		if (code.equals("prepared") || code.equals("confirmed"))
		{
			// new MessageBox("[" + code + "]-交易成功");
			return true;
		}

		if (code.equals("canceled"))
		{
			new MessageBox("交易已取消");
			return false;
		}

		if (code.equals("rolledback"))
		{
			new MessageBox("交易已回滚");
			return false;
		}

		new MessageBox(defaultMsg);
		return false;
	}

	public boolean sendVipPoint(SaleHeadDef saleHead, double point)
	{
		if (!isEnableCrm)
		{
			new MessageBox("Crm功能无法使用");
			return false;
		}

		CrmVipPoint vipPoint = null;
		double sale = saleHead.ysje;

		try
		{
			vipPoint = new CrmVipPoint();

			vipPoint.setApp("0141");
			vipPoint.setSession_id(sessionId);
			vipPoint.setMember_id(saleHead.memo);

			if (SellType.ISBACK(saleHead.djlb))
			{
				point = point * -1;
				sale = saleHead.ysje * -1;
			}

			vipPoint.setPoint(String.valueOf(point));
			vipPoint.setSale(String.valueOf(sale));
			vipPoint.setOperator(saleHead.syyh);
			vipPoint.setBillid(String.valueOf(saleHead.fphm));
			vipPoint.setDate_account(ManipulateDateTime.getCurrentDateBySign());
			vipPoint.setTime_shopping(ManipulateDateTime.getCurrentDateBySign() + " " + ManipulateDateTime.getCurrentTime());
			vipPoint.setSystem("bfpos");

			String retXml = readDataFromCrmServer(vipPoint.toXml(getXmlHead()));

			CrmVipPointResult result = vipPoint.fromXml(retXml);

			if (result == null)
			{
				new MessageBox("发送积分失败");
				return false;
			}
			else if (result.getSuccess().equals("N"))
			{
				new MessageBox(result.getMessage());
				return false;
			}
			else if (result.getSuccess().equals("Y"))
			{
				return true;
			}
			else
			{
				new MessageBox("发送积分失败");
				return false;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("发送积分发生异常");
			return false;
		}
	}
	
	public boolean checkSellBill()
	{
		//未开发
		if (!isEnableCrm)
		{
			new MessageBox("Crm功能无法使用");
			return false;
		}

		//CrmCheckSellBill chkSell = null;
		
		return true;
	}

	private String readDataFromCrmServer(String srcXml)
	{
		return readDataFromCrmServer(srcXml, true);
	}

	private String readDataFromCrmServer(String srcXml, boolean isRet)
	{
		return readDataFromCrmServer(null, srcXml, isRet);
	}

	private String readDataFromCrmServer(Socket outSocket, String srcXml, boolean isRet)
	{
		Socket socket = null;
		byte[] readBuffer = new byte[1024];

		try
		{
			if (outSocket == null)
				socket = new Socket(serverIp, serverPort);
			else
				socket = outSocket;

			socket.setSoTimeout(ConfigClass.ReceiveTimeout);

			// 发送数据
			socket.getOutputStream().write(srcXml.getBytes());
			socket.getOutputStream().flush();

			// isRet为false时直接返回，不读取返回数据，用于注销
			if (!isRet)
				return null;

			long interval = 0; // 时间间隔
			int tmpData = 0; // 读取的数据
			int i = 0; // 缓存记数器

			long starttime = System.currentTimeMillis();

			do
			{
				tmpData = socket.getInputStream().read();

				if (tmpData == -1)
					break;

				readBuffer[i++] = (byte) tmpData;
				interval = System.currentTimeMillis() - starttime;

				if (interval > ConfigClass.ReceiveTimeout)
				{
					new MessageBox("读取Crm返回数据超时");
					return null;
				}

				if (i > 1024)
				{
					new MessageBox("缓冲区溢出");
					return null;
				}
			} while (tmpData != -1);

			return new String(readBuffer, "GBK").trim();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(ex.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if (socket != null && outSocket == null)
					socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				socket = null;
			}
		}
	}

	// public

	// /XStream xStream = new XStream(new XppDriver(new
	// XmlFriendlyNameCoder("__", "_")));
	// 因为_是关键字，默认的会变为__
	// $也是关键字，默认为__,这2个参数一个改变$的显示，一个改变_的显示

	public static void main(String[] arsgs)
	{
		try
		{
			// Ybsj_SocketCrmModule.getDefault().initCrmConnection("124.115.39.11",
			// 6000); // 222.91.222.58
			// Ybsj_SocketCrmModule.getDefault().userLogin("YBDL01", "", "YBDL",
			// "YBDL", "POS099"); // 002199093223144176
			// Ybsj_SocketCrmModule.getDefault().queryVipInfo("102097246", "",
			// null);
			MzkRequestDef req = new MzkRequestDef();
			req.track2 = "002199093223144176";
			req.track3 = "";
			req.passwd = "";

			MzkResultDef ret = new MzkResultDef();
			Ybsj_SocketCrmModule.getDefault().queryMzkInfo(req, ret);

			SaleHeadDef saleHead = new SaleHeadDef();
			saleHead.syjh = "0009";
			saleHead.fphm = 8888;
			saleHead.rqsj = "2012/07/20 16:30";

			// Ybsj_SocketCrmModule.getDefault().mzkSale(saleHead, null);
			// Ybsj_SocketCrmModule.getDefault().userLogoff();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
