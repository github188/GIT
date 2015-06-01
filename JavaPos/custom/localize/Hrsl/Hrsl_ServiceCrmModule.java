package custom.localize.Hrsl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Hrsl.CrmCouponInfo.CrmCouponInfoResult;
import custom.localize.Hrsl.CrmCouponInfo.voucher;
import custom.localize.Hrsl.CrmMzkInfo.CrmMzkInfoResult;
import custom.localize.Hrsl.CrmPrepareCouponSale.CrmCouponCard;
import custom.localize.Hrsl.CrmPrepareCouponSale.PrepareVoucher;
import custom.localize.Hrsl.CrmUserLogin.CrmUserLoginResult;
import custom.localize.Hrsl.CrmVipInfo.CrmVipInfoResult;
import custom.localize.Hrsl.CrmVipPoint.CrmVipPointResult;
import custom.localize.Hrsl.CrmVipPoint.CrmVipPointSaleResult;

public class Hrsl_ServiceCrmModule
{
	private static Hrsl_ServiceCrmModule instance = new Hrsl_ServiceCrmModule();

	private boolean isEnableCrm;

	private String sessionId = "";

	private String serverIp = "";
	// private int serverPort = 6000;
	private String usrname = "";
	private String usrpwd = "";
	private String storeCrm = "";

	public static Hrsl_ServiceCrmModule getDefault()
	{
		return instance;
	}

	public String getXmlHead()
	{
		return "BFCRMXML";
	}

	public void initCrmConnection()
	{
		if (GlobalInfo.sysPara.cyCrmUrl == null || GlobalInfo.sysPara.cyCrmUrl.trim().equals(""))
		{
			new MessageBox("请设置正确的用于登录Crm的连接地址");
			return;
		}

		if (GlobalInfo.sysPara.cyCrmUsrPwd == null || GlobalInfo.sysPara.cyCrmUsrPwd.trim().equals("") || GlobalInfo.sysPara.cyCrmUsrPwd.trim().indexOf(",") == -1)
		{
			new MessageBox("请设置正确的用于登录Crm的用户名和密码");
			return;
		}
		serverIp = GlobalInfo.sysPara.cyCrmUrl.trim();

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
			String[] usrAry = GlobalInfo.sysPara.cyCrmUsrPwd.split(",");
			if (usrAry != null && usrAry.length > 2)
				storeCrm = usrAry[2];
			usrLogin = new CrmUserLogin();
			usrLogin.setApp("0000");
			usrLogin.setStore(storeCrm);
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
//			if (GlobalInfo.sysPara.cardpasswd.equals("A"))
//			{
//				TextBox txt = new TextBox();
//
//				if (!txt.open("请输入会员验证码", "VerifyCode", "需先输入验证码后才能查询卡资料", verifyCode, 0, 0, false, TextBox.AllInput))
//					return false;
//			}

			vipInfo = new CrmVipInfo();

			vipInfo.setApp("0101");

			// new MessageBox("queryVipInfo:"+track);

			if (track.length() == 11)
			{
				// track = track.substring(1);

				vipInfo.setTrack_data(track);
				vipInfo.setFlag("1");
			}
			else
			{
				// vipInfo.setTrack_data(track.substring(1));
				vipInfo.setTrack_data(track);
				vipInfo.setFlag("0");

			}

			vipInfo.setVerify_code(verifyCode.toString());
			vipInfo.setSession_id(sessionId);

			String retXml = readDataFromCrmServer(vipInfo.toXml(getXmlHead()));
			// String retXml =
			// "BFCRMXML00000561<?xml version=\"1.0\" encoding=\"GBK\"?><bfcrm_resp success=\"Y\"><member id=\"10575\"><code>32048886</code><name>孙伟</name><type_code>102</type_code><type_name>贵宾卡</type_name><date_valid>2015-01-18</date_valid><permit_discount>N</permit_discount><permit_voucher>Y</permit_voucher><permit_valuedcard>N</permit_valuedcard><cumulate_cent>Y</cumulate_cent><permit_th>N</permit_th><cent_available>343.0000</cent_available><cent_period>343.0000</cent_period><cent_bn>343.0000</cent_bn><phone>13946090007</phone><hello></hello><master_code></master_code></member></bfcrm_resp>";
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
				if (customer.track == null || customer.track.trim().equals(""))
				{
					new MessageBox("查找会员失败,请重新刷卡");
					System.out.println(ManipulateDateTime.getCurrentDateTimeMilliSencond() + "  查找会员卡失败: 磁道号" + track + " 卡号" + customer.code);
					PosLog.getLog(getClass()).info(ManipulateDateTime.getCurrentDateTimeMilliSencond() + "  查找会员卡失败: 磁道号" + track + " 卡号" + customer.code);
					return false;
				}
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
				// customer.valstr1 = result.getMember().getPhone();

				// 记录下手机号方便后续付款时带出手机号
				if (track.length() == 11)
					customer.valstr10 = track;

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

	public boolean queryCouponInfo(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{

		if (!isEnableCrm)
		{
			new MessageBox("Crm功能无法使用");
			return false;
		}

		CrmCouponInfo cInfo = null;

		try
		{

			cInfo = new CrmCouponInfo();

			cInfo.setApp("0107");

			if (mzkreq.track2.length() == 11)
			{
				// cInfo.setTrack_data(mzkreq.track2.substring(1));
				cInfo.setTrack_data(mzkreq.track2);
				cInfo.setFlag("1");

				StringBuffer cardno = new StringBuffer();

				// 输入顾客卡号
				TextBox txt = new TextBox();
				do
				{
					if (!txt.open("请输入会员卡查询密码", "PASSWORD", "请输入会员卡查询密码", cardno, 0, 0, false, TextBox.MsrKeyInput))
						return false;

					if (cardno.toString().trim().equals(""))
						continue;

					break;
				}
				while (true);

				String pass = getEncryPhonePassword(cardno.toString());

				if (pass == null || pass.trim().equals(""))
				{
					new MessageBox("密码加密失败!");
					return false;
				}
				cInfo.setMember_password(pass);
			}
			/*
			 * else if (mzkreq.track2.startsWith("#"))
			 * {
			 * if (mzkreq.track2.indexOf("=") != -1)
			 * {
			 * cInfo.setTrack_data(mzkreq.track2.substring(1).substring(0,
			 * mzkreq.track2.indexOf("=")-1));
			 * }
			 * else
			 * {
			 * cInfo.setTrack_data(mzkreq.track2.substring(1));
			 * }
			 * 
			 * cInfo.setFlag("0");
			 * }
			 */
			else
			{
				if (mzkreq.track2.indexOf("=") != -1)
				{
					cInfo.setTrack_data(mzkreq.track2.substring(0, mzkreq.track2.indexOf("=")));
				}
				else
				{
					cInfo.setTrack_data(mzkreq.track2);
				}

				cInfo.setFlag("0");

			}
			cInfo.setRequire_valid_date("Y");
			cInfo.setSession_id(sessionId);

			String retXml = readDataFromCrmServer(cInfo.toXml(getXmlHead()));
			// String retXml =
			// "BFCRMXML00000347<?xml version='1.0' encoding='GBK'?><bfcrm_resp success='Y'><member_id>288957</member_id><member_code>31297207</member_code><voucher_list><voucher id='0' date_valid='2013-07-31'><name>积分返券</name><balance>5.00</balance></voucher><voucher id='0' date_valid='2013-12-31'><name>积分返券</name><balance>14.00</balance></voucher><voucher id='2' date_valid='2013-07-31'><name>积分返券</name><balance>5.00</balance></voucher><voucher id='2' date_valid='2013-12-31'><name>积分返券</name><balance>14.00</balance></voucher></voucher_list></bfcrm_resp>";
			CrmCouponInfoResult result = cInfo.fromXml(retXml);

			if (result == null)
			{
				new MessageBox("查找券信息失败");
				return false;
			}
			else if (result.getSuccess().equals("N"))
			{
				new MessageBox(result.getMessage());
				return false;
			}
			else if (result.getSuccess().equals("Y"))
			{
				mzkreq.str1 = result.getMember_id().trim();
				mzkret.cardno = result.getMember_code();

				ArrayList al = result.getVoucher_list();
				ManipulateDateTime mt = new ManipulateDateTime();
				int now = Integer.parseInt(mt.getDateByEmpty());
				String[] usrAry = GlobalInfo.sysPara.cyCrmUsrPwd.split(",");
				boolean b = true;
				if (usrAry != null && usrAry.length > 3)
					b = false;

				// 券id,名称,余额,汇率,日期
				for (int i = 0; i < al.size(); i++)
				{
					voucher v = (voucher) al.get(i);
					// 比较有效期时间，不在效期内的则不显示
					String date = v.getDate().replace("-", "");
					int date1 = Integer.parseInt(date.replace("/", ""));
					if (now <= date1 && b)
					{
						mzkret.memo = mzkret.memo + "|" + v.getId() + "," + v.getName() + "," + v.getBalance() + ",1," + v.getDate();
					}
					else if (now <= date1 && !b)
					{
						boolean bb = true;
						for (int j = 3; j < usrAry.length; j++)
						{
							if (usrAry[j].trim().equals(v.getId().trim()))
							{
								bb = false;
								break;
							}
						}
						if (bb)
						{
							mzkret.memo = mzkret.memo + "|" + v.getId() + "," + v.getName() + "," + v.getBalance() + ",1," + v.getDate();
						}
					}
				}
				return true;
			}
			else
			{
				new MessageBox("查找券信息失败");
				return false;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("查找券信息发生异常");
			return false;
		}

	}

	public String couponPrepareSale(MzkRequestDef mzkreq, Vector payinfo)
	{
		CrmPrepareCouponSale preSale = null;
		double money = 0;

		try
		{
			preSale = new CrmPrepareCouponSale();

			preSale.setApp("0108");

			preSale.setType("pay");

			preSale.setSession_id(sessionId);
			preSale.setBill_id(String.valueOf(mzkreq.fphm));
			preSale.setStore_code(storeCrm);
			preSale.setPos_id(mzkreq.syjh);
			preSale.setDate_account(mzkreq.str2);
			preSale.setCashier(GlobalInfo.syjStatus.syyh);

			Vector cardV = new Vector();
			// 按照会员卡id号分类
			for (int i = 0; i < payinfo.size(); i++)
			{
				String[] row = ((String) payinfo.elementAt(i)).split(",");
				String temp = (String) payinfo.elementAt(i);
				for (int j = i + 1; j < payinfo.size();)
				{
					// 卡id 金额 券id
					String[] row1 = ((String) payinfo.elementAt(j)).split(",");
					if (row[0].equals(row1[0]))
					{
						temp = temp + "|" + (String) payinfo.elementAt(j);
						payinfo.remove(j);
						continue;
					}
					else
					{
						j = j + 1;
					}
				}
				cardV.add(temp);
			}
			ArrayList card_list = new ArrayList();
			for (int i = 0; i < cardV.size(); i++)
			{
				CrmCouponCard card = new CrmCouponCard();
				String[] tempA = ((String) cardV.elementAt(i)).split("\\|");
				card.setMember_id((tempA[0].split(","))[0]);
				ArrayList voucher_list = new ArrayList();

				for (int j = 0; j < tempA.length; j++)
				{
					PrepareVoucher vv = new PrepareVoucher();
					vv.setId((tempA[j].split(","))[2]);
					double d = Convert.toDouble((tempA[j].split(","))[1]);
					money = money + d;
					if (mzkreq.type.equals("03"))
						d = d * -1;
					vv.setAmount(String.valueOf(d));

					voucher_list.add(vv);
				}

				card.setVoucher_list(voucher_list);
				card_list.add(card);
			}
			mzkreq.num1 = money;
			preSale.setCard_list(card_list);

			String retXml = readDataFromCrmServer(preSale.toXml(getXmlHead()), true);
			// String retXml =
			// "BFCRMXML00000135<?xml version='1.0' encoding='GBK'?><bfcrm_resp success='Y'><server_transaction_id>100000012</server_transaction_id></bfcrm_resp>";
			CrmMzkSaleResult result = preSale.fromXml(retXml);

			if (result == null)
			{
				new MessageBox("准备支付优惠券失败");
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
				new MessageBox("准备支付优惠券失败");
				return null;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("准备支付优惠券失败发生异常");
			return null;
		}
	}

	public boolean couponConfirmSale(MzkRequestDef mzkreq)
	{
		CrmConfirmMzkSale confirmSale = null;
		double money = mzkreq.num1;

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
			// String retXml =
			// "BFCRMXML00000134<?xml version='1.0' encoding='GBK'?><bfcrm_resp success='Y'><status_before_perform>prepared</status_before_perform></bfcrm_resp>";
			CrmMzkSaleResult result = confirmSale.fromXml(retXml);

			if (result == null)
			{
				new MessageBox("确认支付优惠券失败");
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
				new MessageBox("确认支付优惠券失败");
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("确认支付优惠券发生异常");
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
			preSale.setCashier(GlobalInfo.syjStatus.syyh);

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
			vipPoint.setMember_id(saleHead.str6);

			if (SellType.ISBACK(saleHead.djlb))
			{
				// point = point * -1;
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

	/*
	 * public boolean sendVipPoint(SaleHeadDef saleHead, double point,String id)
	 * {
	 * if (!isEnableCrm)
	 * {
	 * new MessageBox("Crm功能无法使用");
	 * return false;
	 * }
	 * 
	 * CrmVipPoint vipPoint = null;
	 * double sale = saleHead.ysje;
	 * 
	 * try
	 * {
	 * vipPoint = new CrmVipPoint();
	 * 
	 * vipPoint.setApp("0141");
	 * vipPoint.setSession_id(sessionId);
	 * vipPoint.setMember_id(id);
	 * 
	 * if (SellType.ISBACK(saleHead.djlb))
	 * {
	 * //point = point * -1;
	 * sale = saleHead.ysje * -1;
	 * }else{
	 * point = point * -1;
	 * }
	 * 
	 * vipPoint.setPoint(String.valueOf(point));
	 * vipPoint.setSale(String.valueOf(sale));
	 * vipPoint.setOperator(saleHead.syyh);
	 * vipPoint.setBillid(String.valueOf(saleHead.fphm));
	 * vipPoint.setDate_account(ManipulateDateTime.getCurrentDateBySign());
	 * vipPoint.setTime_shopping(ManipulateDateTime.getCurrentDateBySign() + " "
	 * + ManipulateDateTime.getCurrentTime());
	 * vipPoint.setSystem("bfpos");
	 * 
	 * String retXml = readDataFromCrmServer(vipPoint.toXml(getXmlHead()));
	 * 
	 * CrmVipPointResult result = vipPoint.fromXml(retXml);
	 * 
	 * if (result == null)
	 * {
	 * new MessageBox("发送积分失败");
	 * return false;
	 * }
	 * else if (result.getSuccess().equals("N"))
	 * {
	 * new MessageBox(result.getMessage());
	 * return false;
	 * }
	 * else if (result.getSuccess().equals("Y"))
	 * {
	 * return true;
	 * }
	 * else
	 * {
	 * new MessageBox("发送积分失败");
	 * return false;
	 * }
	 * 
	 * }
	 * catch (Exception ex)
	 * {
	 * ex.printStackTrace();
	 * new MessageBox("发送积分发生异常");
	 * return false;
	 * }
	 * }
	 */
	public boolean sendVipPointSale(SaleHeadDef saleHead, double str, CustomerDef cust)
	{
		if (!isEnableCrm)
		{
			new MessageBox("Crm功能无法使用");
			return false;
		}

		CrmVipPoint vipPoint = null;
		try
		{
			vipPoint = new CrmVipPoint();

			vipPoint.setApp("0142");
			vipPoint.setSession_id(sessionId);
			vipPoint.setMember_id(cust.track);

			if (SellType.ISBACK(saleHead.djlb))
			{
				str = str * -1;

			}

			vipPoint.setPoint(String.valueOf(str));
			vipPoint.setOperator(saleHead.syyh);
			vipPoint.setSystem("bfpos");

			String retXml = readDataFromCrmServer(vipPoint.toXmlSale(getXmlHead()));

			CrmVipPointSaleResult result = vipPoint.fromXmlSale(retXml);

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
				new MessageBox("积分返券流水号:" + result.getBillid() + "\n" + "返券金额:" + result.getCoupon());
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
		// 未开发
		if (!isEnableCrm)
		{
			new MessageBox("Crm功能无法使用");
			return false;
		}

		// CrmCheckSellBill chkSell = null;

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
		URL url;
		String result = "";
		try
		{
			url = new URL(serverIp);
			HttpURLConnection conn;
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			// conn.setRequestMethod("POST");
			// conn.addRequestProperty("User-Agent",
			// "Mozilla/4.0(compatible;MSIE5.5;Windows NT; DigExt)");
			conn.setConnectTimeout(ConfigClass.ConnectTimeout);
			conn.setReadTimeout(ConfigClass.ReceiveTimeout);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			writer.write(srcXml);
			writer.flush();
			writer.close();

			// isRet为false时直接返回，不读取返回数据，用于注销
			if (!isRet)
				return null;
			long interval = 0; // 时间间隔
			long starttime = System.currentTimeMillis();
			System.out.print(conn.getResponseCode());
			InputStreamReader reder = new InputStreamReader(conn.getInputStream());

			BufferedReader breader = new BufferedReader(reder);

			String content = "";

			while ((content = breader.readLine()) != null)
			{
				System.out.println(content);
				result = result + content;
				interval = System.currentTimeMillis() - starttime;

				if (interval > ConfigClass.ReceiveTimeout)
				{
					new MessageBox("读取Crm返回数据超时");
					return null;
				}
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println(e);
		}
		return result;

	}

	public String httpPost(String str, String postdata)
	{
		// URL url = new
		// URL("http://116.90.81.246/api/retailer/retailercheck.php");
		URL url;
		String result = "";
		try
		{
			url = new URL(str);
			URLConnection conn;
			conn = url.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			writer.write(postdata);
			writer.flush();
			writer.close();

			InputStreamReader reder = new InputStreamReader(conn.getInputStream(), "utf-8");

			BufferedReader breader = new BufferedReader(reder);

			String content = null;

			while ((content = breader.readLine()) != null)
			{
				result = result + content;
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	protected String getEncryPhonePassword(String pass)
	{
		String line = null;
		try
		{
			String command = "BFCRM.exe encode " + pass;

			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);

			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

			line = br.readLine().toString();

			return line;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}
