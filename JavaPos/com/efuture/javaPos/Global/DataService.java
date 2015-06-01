package com.efuture.javaPos.Global;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.AssemblyInfo;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.CustomerVipZklDef;
import com.efuture.javaPos.Struct.GoodsAmountDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.InvoiceInfoDef;
import com.efuture.javaPos.Struct.MemoInfoDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.PosTimeDef;
import com.efuture.javaPos.Struct.SaleAppendDef;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;
import com.efuture.javaPos.Struct.SyjMainDef;
import com.efuture.javaPos.Struct.TasksDef;
import com.efuture.javaPos.Struct.ZqInfoRequestDef;
import com.efuture.javaPos.UI.Design.PersonGrantForm;
import com.efuture.javaPos.UI.Design.SetSystemTimeForm;

//这个类用于将数据转换为对象
public class DataService
{
	public static DataService currentDataService = null;

	public static DataService getDefault()
	{
		if (DataService.currentDataService == null)
		{
			DataService.currentDataService = CustomLocalize.getDefault().createDataService();
		}

		return DataService.currentDataService;
	}

	public boolean getServerTime(boolean settime)
	{
		TimeDate time = new TimeDate();

		if (NetService.getDefault().getServerTime(time))
		{
			ManipulateDateTime mdt = new ManipulateDateTime();

			// 设置本机时间
			mdt.setDateTime(time);

			GlobalInfo.isOnline = true;

			return true;
		}
		else
		{
			GlobalInfo.isOnline = false;

			if (settime)
			{
				new MessageBox(Language.apply("连接网络失败,系统进入脱网状态!"));

				// 弹出窗口手工设置时间,仅设置时间
				new SetSystemTimeForm(true);
			}

			return false;
		}
	}

	public boolean checkSyjValid()
	{
		if (GlobalInfo.isOnline)
		{
			// 调试模式不上传IP地址
			String ipaddr = GlobalInfo.ipAddr;

			if (ConfigClass.DebugMode)
				ipaddr = "";

			// 由于数据库只定义了40位长,避免版本号长度超过40
			String version = AssemblyInfo.AssemblyVersion + " , " + CustomLocalize.getDefault().getAssemblyVersion();
			version = version.replaceAll(" bulid ", "-");
			version = version.replaceAll(" build ", "-");
			if (version.length() > 40)
				version = version.substring(0, 40);
			if (!NetService.getDefault().getSyjMain(ConfigClass.CashRegisterCode, ipaddr, ConfigClass.CDKey, version))
			{
				// 如果还是联网状态,则说明款机定义有错;如果脱网继续从本地读取款机定义
				if (GlobalInfo.isOnline) { return false; }
			}
		}

		//
		SyjMainDef syjDef = new SyjMainDef();

		if (!AccessLocalDB.getDefault().readSyjDef(syjDef))
		{
			new MessageBox(Language.apply("读取收银机定义时发生错误，系统马上退出!"), null, false);

			return false;
		}

		//
		if (!syjDef.syjh.equals(ConfigClass.CashRegisterCode))
		{
			// if (new MessageBox("收银机号[" + ConfigClass.CashRegisterCode +
			// "]和上次使用配置[" + syjDef.syjh +
			// "]不一致\n\n你确定要以新的设置进入系统吗?\n\n任意键-是 / 2-否", null, false).verify()
			// == GlobalVar.Key2) { return false; }
			if (new MessageBox(Language.apply("收银机号[{0}]和上次使用配置[{1}]不一致\n\n你确定要以新的设置进入系统吗?\n\n任意键-是 / 2-否", new Object[] { ConfigClass.CashRegisterCode, syjDef.syjh }), null, false).verify() == GlobalVar.Key2) { return false; }

			// 以配置文件中的收银机号为准
			syjDef.syjh = ConfigClass.CashRegisterCode;
		}

		if (!syjDef.ipaddr.equals("") && !syjDef.ipaddr.equals(GlobalInfo.ipAddr))
		{
			// if (new MessageBox("收银机IP[" + GlobalInfo.ipAddr + "]和上次使用配置[" +
			// syjDef.ipaddr + "]不一致\n\n你确定要以新的设置进入系统吗?\n\n任意键-是 / 2-否", null,
			// false).verify() == GlobalVar.Key2)
			if (new MessageBox(Language.apply("收银机IP[{0}]和上次使用配置[{1}]不一致\n\n你确定要以新的设置进入系统吗?\n\n任意键-是 / 2-否", new Object[] { GlobalInfo.ipAddr, syjDef.ipaddr }), null, false).verify() == GlobalVar.Key2)
			{
				if (!ConfigClass.DebugMode)
					return false;
			}

			// 以当前设置的IP为准
			syjDef.ipaddr = GlobalInfo.ipAddr;
		}

		//
		GlobalInfo.syjDef = syjDef;

		//
		if (String.valueOf(GlobalInfo.syjDef.ists).length() <= 0)
		{
			GlobalInfo.syjDef.ists = 'Y';
		}

		if (GlobalInfo.syjDef.isprint != 'Y')
		{
			if (Printer.getDefault() != null)
				Printer.getDefault().close();
		}

		if (GlobalInfo.syjDef.isdisp != 'Y')
		{
			if (LineDisplay.getDefault() != null)
				LineDisplay.getDefault().close();
		}

		if (GlobalInfo.syjDef.datatime <= 0)
		{
			GlobalInfo.syjDef.datatime = 30;
		}

		if (GlobalInfo.syjDef.dataspace <= 0)
		{
			GlobalInfo.syjDef.dataspace = 100;
		}

		return true;
	}

	public boolean getNetPosTime()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getPosTime();
		}

		// 读取班次信息到全局信息
		AccessLocalDB.getDefault().readPosTime();

		return true;
	}

	public boolean getNetSysPara()
	{
		try
		{
			if (GlobalInfo.isOnline)
			{
				if (!NetService.getDefault().getSysPara())
					return false;
			}
			return true;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
		finally
		{
			// 读取参数信息到全局信息
			AccessLocalDB.getDefault().readSysPara();
		}
	}

	public boolean getNetPayMode()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getPayModeDef();
		}

		// 读取付款模版到全局信息
		Vector paymode = new Vector();

		if (AccessLocalDB.getDefault().readPayMode(paymode))
		{
			GlobalInfo.payMode = paymode;
		}

		return true;
	}

	// 得到菜单信息
	public boolean getNetMenuFunc()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getMenuFunc();
		}

		// 读取菜单定义到全局变量
		AccessLocalDB.getDefault().readMenuFunc();

		return true;
	}

	// 得到收银机收银范围
	public boolean getNetSyjGrange()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getSyjGrange();
		}

		return true;
	}

	// 得到用户角色
	public boolean getNetOperRole()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getOperRole();
		}

		return true;
	}

	// 得到顾客卡类型
	public boolean getNetCustomerType()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getCustomerType();
		}

		return true;
	}

	// 得到顾客采集信息
	public boolean getNetBuyerInfo()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getBuyerInfo();
		}

		return true;
	}

	// 得到呼叫信息
	public boolean getNetCallInfo()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getCallInfo();
		}

		// 读取呼叫信息到全局变量
		AccessLocalDB.getDefault().readCallInfo();

		return true;
	}

	// 得到备用信息
	public boolean getNetMemoInfo()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getMemoInfo();
		}

		// 读取备用信息到全局变量
		AccessLocalDB.getDefault().readMemoInfo();

		return true;
	}

	// 得到电子秤定义
	public boolean getNetDzcMode()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getDzcMode();
		}

		// 读取电子秤模版到全局变量
		AccessLocalDB.getDefault().readDzcMode();

		return true;
	}

	// 得到缴款模版定义
	public boolean getNetPayinMode()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getPayinMode();
		}

		return true;
	}

	// 得到系统管理架构
	public boolean getNetManaFrame()
	{
		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().getManaFrame();
		}

		return true;
	}

	// 得到收银上线数据
	public boolean getNetPaymentLimit()
	{
		if (GlobalInfo.funcMap.containsKey("PAYLIMIT") && ((String) GlobalInfo.funcMap.get("PAYLIMIT")).equals("00000000"))
		{
			if (GlobalInfo.isOnline)
			{
				NetService.getDefault().getPaymentLimit();
			}

			// 读取付款上限到全局变量
			AccessLocalDB.getDefault().readPaymentLimit();
		}
		return true;
	}

	// 用于客户化时加载第三方扩展定义
	public boolean getNewOtherExtItem()
	{
		return true;
	}

	// 用于脱机控制登录门店(前提是日终时operuser表中的Memo字段得存入工号可同时登录的门店号，多个门店以逗号分隔)
	// 若memo 为空则不控制, 不能控制market是否为空，否则，memo中有值，但Market为空时，就达不到效果了
	public boolean checkMarket(OperUserDef staff)
	{
		if(ConfigClass.Market.equals(""))
			return true;
		
		if (staff.memo != null && !staff.memo.trim().equals("") && ("," + staff.memo + ",").indexOf("," + ConfigClass.Market + ",") < 0)
		{
			new MessageBox(Language.apply("该工号不属于当前门店!"));
			return false;
		}
		return true;
	}

	public boolean getOperUser(OperUserDef staff, String id)
	{
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
		{
			if (!AccessBaseDB.getDefault().getOperUser(id, staff))
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z')
				{
					new MessageBox(Language.apply("该工号不存在!"), null, false);

					return false;
				}
			}
			else
			{
				if (!checkMarket(staff))
					return false;

				return true;
			}
		}

		if (GlobalInfo.isOnline)
		{
			if (!NetService.getDefault().getOperUser(id, staff)) { return false; }
		}
		else
		{
			if (!AccessBaseDB.getDefault().getOperUser(id, staff))
			{
				new MessageBox(Language.apply("该工号不存在!"), null, false);

				return false;
			}

			if (!checkMarket(staff))
				return false;
		}

		return true;
	}

	public boolean getCustomer(CustomerDef cust, String track)
	{
		
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N' && GlobalInfo.sysPara.customerbyconnect != 'Y')
		{
			// 联网且优先找本地时，当参数为A时先找网上的会员 /美佳美
			if (GlobalInfo.sysPara.customerbyconnect == 'A' && NetService.getDefault().getCustomer(cust, track))
				return true;

			if (!AccessBaseDB.getDefault().getCustomer(cust, track))
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z')
				{
					new MessageBox(Language.apply("无此顾客卡信息!"), null, false);

					return false;
				}
			}
			else
			{
				return true;
			}
		}

		if (GlobalInfo.isOnline)
		{
			if (!NetService.getDefault().getCustomer(cust, track)) { return false; }
		}
		else
		{
			// 会员卡必须联网使用
			if (GlobalInfo.sysPara.customerbyconnect == 'Y')
			{
				new MessageBox(Language.apply("顾客卡必须联网使用!"));

				return false;
			}

			if (!AccessBaseDB.getDefault().getCustomer(cust, track))
			{
				new MessageBox(Language.apply("无此顾客卡信息!"), null, false);

				return false;
			}
		}

		return true;
	}

	// 得到商品批量信息
	public boolean findAmountDef(GoodsAmountDef pl, String code, String gz, String uid, double sl)
	{
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
		{
			if (!AccessBaseDB.getDefault().findAmountDef(pl, code, gz, uid, sl))
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z') { return false; }
			}
			else
			{
				return true;
			}
		}

		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().findAmountDef(pl, code, gz, uid, sl);
		}
		else
		{
			return AccessBaseDB.getDefault().findAmountDef(pl, code, gz, uid, sl);
		}
	}

	public String analyzeGoodsBarcode(String code)
	{
		return code;
	}

	// 得到商品信息
	public int getGoodsDef(GoodsDef goodsDef, int searchFlag, String barcode, String gz, String proTime, String yhsj, String djlb)
	{
		int result = -1;

		// 分析编码
		String code = analyzeGoodsBarcode(barcode);

		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
		{
			// 如果找不到商品，按原编码重新查
			result = AccessBaseDB.getDefault().getGoodsDef(goodsDef, searchFlag, code, gz, proTime, yhsj, djlb);
			if ((result < 0) && !code.equals(barcode))
			{
				result = AccessBaseDB.getDefault().getGoodsDef(goodsDef, searchFlag, barcode, gz, proTime, yhsj, djlb);
			}

			if (result < 0)
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z') { return getGoodsDefResult(result); }
			}
			else
			{
				return getGoodsDefResult(result);
			}
		}

		if (GlobalInfo.isOnline)
		{
			// 不提示错误
			NetService.getDefault().setErrorMsgEnable(false);

			// 如果找不到商品，按原编码重新查询
			result = NetService.getDefault().getGoodsDef(goodsDef, String.valueOf(searchFlag), code, gz, proTime, yhsj, djlb);

			if ((result < 0) && !code.equals(barcode))
			{
				result = NetService.getDefault().getGoodsDef(goodsDef, String.valueOf(searchFlag), barcode, gz, proTime, yhsj, djlb);
			}

			// 恢复提示错误
			NetService.getDefault().setErrorMsgEnable(true);
		}
		else
		{
			// 如果找不到商品，按原编码重新查
			result = AccessBaseDB.getDefault().getGoodsDef(goodsDef, searchFlag, code, gz, proTime, yhsj, djlb);

			if ((result < 0) && !code.equals(barcode))
			{
				result = AccessBaseDB.getDefault().getGoodsDef(goodsDef, searchFlag, barcode, gz, proTime, yhsj, djlb);
			}
		}

		return getGoodsDefResult(result);
	}

	public int getGoodsDefResult(int result)
	{
		// 播放声音
		playGoodsSound(result);

		switch (result)
		{
			case 0:
				break;

			case 4:
				// new MessageBox("该商品有多个柜组，需确定柜组");
				break;

			case 2:
				// if (GlobalInfo.syjDef.issryyy == 'N')
				// GlobalInfo.saleform.getSaleEvent().setBigInfo("该商品不属于收银机的收银范围",
				// "","");
				// else new MessageBox("该商品不属于收银机的收银范围");
				if (GlobalInfo.syjDef.issryyy == 'N')
				{
					if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
						new MessageBox(Language.apply("该商品不属于收银机的收银范围"), GlobalVar.Validation);
					else
						new MessageBox(Language.apply("该商品不属于收银机的收银范围"), GlobalVar.Enter);
				}
				else
				{
					new MessageBox(Language.apply("该商品不属于收银机的收银范围"));
				}
				break;

			case 3:

				// if (GlobalInfo.syjDef.issryyy == 'N')
				// GlobalInfo.saleform.getSaleEvent().setBigInfo("该商品不属于营业员的营业柜组",
				// "", "", -1);
				// else new MessageBox("该商品不属于营业员的营业柜组");
				if (GlobalInfo.syjDef.issryyy == 'N')
				{
					if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
						new MessageBox(Language.apply("该商品不属于营业员的营业柜组"), GlobalVar.Validation);
					else
						new MessageBox(Language.apply("该商品不属于营业员的营业柜组"), GlobalVar.Enter);
				}
				else
				{
					new MessageBox(Language.apply("该商品不属于营业员的营业柜组"));
				}
				break;

			case 9:

				// if (GlobalInfo.syjDef.issryyy == 'N')
				// GlobalInfo.saleform.getSaleEvent().setBigInfo("该商品不能看板销售",
				// "","");
				// else new MessageBox("该商品不能看板销售");
				if (GlobalInfo.syjDef.issryyy == 'N')
				{
					if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
						new MessageBox(Language.apply("该商品不能看板销售"), GlobalVar.Validation);
					else
						new MessageBox(Language.apply("该商品不能看板销售"), GlobalVar.Enter);
				}
				else
				{
					new MessageBox(Language.apply("该商品不能看板销售"));
				}
				break;

			case 10:
				new MessageBox(Language.apply("该商品不允许在当前交易类型下销售"));
				break;

			default:
				// if (GlobalInfo.syjDef.issryyy == 'N')
				// GlobalInfo.saleform.getSaleEvent().setBigInfo("找不到该商品信息",
				// "","");
				// else new MessageBox("找不到该商品信息");
				if (GlobalInfo.syjDef.issryyy == 'N')
				{
					if (GlobalInfo.sysPara.isinputnextgoods == 'Y')
						new MessageBox(Language.apply("找不到该商品信息"), GlobalVar.Validation);
					else
						new MessageBox(Language.apply("找不到该商品信息"), GlobalVar.Enter);
				}
				else
				{
					new MessageBox(Language.apply("找不到该商品信息"));
				}
				result = -1;
				break;
		}

		return result;
	}

	// 面值卡交易
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().sendMzkSale(req, ret);
		}
		else
		{
			new MessageBox(Language.apply("面值卡必须联网使用!"));
		}

		return false;
	}

	// 面值卡查询
	public boolean getMzkInfo(MzkRequestDef req, MzkResultDef ret)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getMzkInfo(req, ret);
		}
		else
		{
			new MessageBox(Language.apply("查询面值卡必须联网使用!"));
		}

		return false;
	}

	// 电子券交易
	public boolean sendDzqSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().sendDzqSale(req, ret);
		}
		else
		{
			new MessageBox(Language.apply("电子券必须联网使用!"));
		}

		return false;
	}

	// 电子券查询
	public boolean getDzqInfo(MzkRequestDef req, MzkResultDef ret)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getDzqInfo(req, ret);
		}
		else
		{
			new MessageBox(Language.apply("查询电子券必须联网使用!"));
		}

		return false;
	}

	// 返券卡查询
	public boolean getFjkInfo(MzkRequestDef req, ArrayList fjklist)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getFjkInfo(req, fjklist);
		}
		else
		{
			new MessageBox(Language.apply("返券卡查询必须联网使用!"));
		}

		return false;
	}

	// 积分明细查询
	public boolean getJfInfo(MzkRequestDef req, ArrayList jfList)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getJfInfo(req, jfList);
		}
		else
		{
			new MessageBox(Language.apply("积分明细查询必须联网使用!"));
		}

		return false;
	}

	// 返券卡交易
	public boolean sendFjkSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().sendFjkSale(req, ret);
		}
		else
		{
			new MessageBox(Language.apply("返券卡交易必须联网使用!"));

			return false;
		}
	}

	// 赠券
	public boolean saveZqInfo(ZqInfoRequestDef req)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().saveZqInfo(req);
		}
		else
		{
			new MessageBox(Language.apply("返券卡交易必须联网使用!"));

			return false;
		}
	}

	// 查询返卡规则信息
	public boolean getFjkRuleInfo(MzkRequestDef req, ArrayList fjklist)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getFjkRuleInfo(req, fjklist);
		}
		else
		{
			new MessageBox(Language.apply("返券卡规则查询必须联网使用!"));
		}

		return false;
	}

	// CRM会员卡交易
	public boolean sendHykSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().sendHykSale(req, ret);
		}
		else
		{
			new MessageBox(Language.apply("会员卡交易必须联网使用!"));
		}

		return false;
	}

	// 发送收银状态
	public boolean sendSyjStatus()
	{
		GlobalInfo.syjStatus.je = ManipulatePrecision.doubleConvert(GlobalInfo.syjStatus.je, 2, 1);
		GlobalInfo.syjStatus.xjje = ManipulatePrecision.doubleConvert(GlobalInfo.syjStatus.xjje, 2, 1);

		if (GlobalInfo.isOnline)
		{
			NetService.getDefault().sendSyjStatus(GlobalInfo.syjStatus);
		}

		return true;
	}

	public void checkInvoiceNo()
	{
		Object obj;

		// 先检查当前小票号是否小于当天最大一笔小票号
		obj = GlobalInfo.dayDB.selectOneData("select max(fphm) from SALEHEAD where syjh = '" + ConfigClass.CashRegisterCode + "'");

		if (obj != null)
		{
			if (GlobalInfo.syjStatus.fphm <= Long.parseLong(String.valueOf(obj)))
			{
				// 记录日志
				AccessDayDB.getDefault().writeWorkLog(Language.apply("本地小票号 ") + String.valueOf(GlobalInfo.syjStatus.fphm) + Language.apply(" 比交易小票号 ") + String.valueOf(obj) + Language.apply(" 小"));

				// 改写最大小票号
				GlobalInfo.syjStatus.fphm = Long.parseLong(String.valueOf(obj)) + 1;
				AccessLocalDB.getDefault().writeSyjStatus();
			}
		}
		else
		{
			// 再检查当前小票号是否小于前一个工作日最大一笔小票号
			ManipulateDateTime mdt = new ManipulateDateTime();
			String date = mdt.getDateByEmpty();
			File invoice = new File(ConfigClass.LocalDBPath + "Invoice//");
			String[] list = invoice.list();
			int max = 0;

			for (int i = 0; i < list.length; i++)
			{
				if ((date.compareTo(list[i]) == 0) || (list[i].length() != 8))
				{
					continue;
				}
				else if (list[i].compareTo(list[max]) > 0)
				{
					max = i;
				}
			}

			date = list[max];

			Sqldb sql = LoadSysInfo.getDefault().loadDayDB(date);

			if (sql != null)
			{
				try
				{
					obj = sql.selectOneData("select max(fphm) from SALEHEAD where syjh = '" + ConfigClass.CashRegisterCode + "'");

					if (obj != null)
					{
						if (GlobalInfo.syjStatus.fphm <= Long.parseLong(String.valueOf(obj)))
						{
							// 记录日志
							AccessDayDB.getDefault().writeWorkLog(Language.apply("本地小票号 ") + String.valueOf(GlobalInfo.syjStatus.fphm) + Language.apply(" 比交易小票号 ") + String.valueOf(obj) + Language.apply(" 小"));

							// 改写最大小票号
							GlobalInfo.syjStatus.fphm = Long.parseLong(String.valueOf(obj)) + 1;
							AccessLocalDB.getDefault().writeSyjStatus();
						}
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}
				finally
				{
					sql.Close();
				}
			}

			sql = null;
		}

		// 再检查当前小票号是否小于网上最大小票号,网上最大小票号返回的是最大加1
		InvoiceInfoDef inv = new InvoiceInfoDef();

		if (GlobalInfo.isOnline && NetService.getDefault().getInvoiceInfo(inv))
		{
			if (GlobalInfo.syjStatus.fphm < inv.maxinv)
			{
				// 记录日志
				AccessDayDB.getDefault().writeWorkLog(Language.apply("本地小票号 ") + String.valueOf(GlobalInfo.syjStatus.fphm) + Language.apply(" 比网上小票号 ") + String.valueOf(inv.maxinv) + Language.apply(" 小"));

				// 改写最大小票号
				GlobalInfo.syjStatus.fphm = inv.maxinv;
				AccessLocalDB.getDefault().writeSyjStatus();
			}
		}

		// 再检查当前小票号是否达到最大值
		if (GlobalInfo.syjStatus.fphm > 9999999)
		{
			new MessageBox(Language.apply("目前系统小票号已达到最大值\n\n系统将重新设置小票号"));

			// 记录错误日志
			AccessDayDB.getDefault().writeWorkLog(Language.apply("系统小票号已达到最大值999999"));

			// 改写最大小票号
			GlobalInfo.syjStatus.fphm = 1;
			AccessLocalDB.getDefault().writeSyjStatus();
		}
	}

	public void execHistoryTask()
	{
		TasksDef task = null;
		long seqno = 0;
		boolean ret = false;
		// 强制写入小票及缴款发送任务
		AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDINVOICE, TaskExecute.getKeyTextByBalanceDate());
		AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDPAYJK, TaskExecute.getKeyTextByBalanceDate());
		// 执行任务表未完成任务
		while ((task = AccessLocalDB.getDefault().readTask(seqno)) != null)
		{
			// 读取下一个seqno任务
			seqno = task.seqno;
			// 执行任务
			ret = TaskExecute.getDefault().executeTask(task);

			// 任务执行成功则删除任务 或者 不是一个必要任务先删除,避免任务重复执行
			if (ret || !StatusType.isMustTask(task.type))
			{
				AccessLocalDB.getDefault().deleteTask(task.seqno);
			}
		}

		// 发送付款冲正
		CreatePayment.getDefault().sendAllPaymentCz();

	}

	// 获得退货商品明细
	public boolean getBackGoodsDetail(Vector backgoods, String oldSyj, String oldFphm, String code, String gz, String uid)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getBackGoodsDetail(backgoods, oldSyj, oldFphm, code, gz, uid);
		}
		else
		{
			return false;
		}
	}

	// 获得前台退货是否有扣回付款
	public boolean getBackSaleBuckle(MemoInfoDef mid)
	{
		if (!GlobalInfo.isOnline)
		{
			new MessageBox(Language.apply("脱网状态下不支持扣回付款!"));

			return false;
		}

		return true;
	}

	// 调用共用方法
	public boolean doCommonMethod(String type, String para1, String para2, String para3, String para4, String para5, String para6, String para7, String para8, String para9, String para10, Vector vcresult)
	{
		if (!GlobalInfo.isOnline)
		{
			new MessageBox(Language.apply("脱网状态下不支持该方法!"));

			return false;
		}

		return NetService.getDefault().doCommonMethod(type, para1, para2, para3, para4, para5, para6, para7, para8, para9, para10, vcresult);
	}

	public boolean getCheckLogin(String syjh, String staffid)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getCheckLogin(syjh, staffid);
		}
		else
		{
			return true;
		}
	}

	public boolean getGoodsMutiUnit(Vector unit, String code)
	{
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
		{
			if (!AccessBaseDB.getDefault().getGoodsMutiUnit(unit, code))
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z') { return false; }
			}
			else
			{
				return true;
			}
		}

		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getMutiUnit(unit, code);
		}
		else
		{
			return AccessBaseDB.getDefault().getGoodsMutiUnit(unit, code);
		}
	}

	public boolean getSubGoodsDef(Vector subGoods, String fxm, String gz, char type)
	{
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
		{
			if (!AccessBaseDB.getDefault().getSubGoodsDef(subGoods, fxm, gz, type))
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z') { return false; }
			}
			else
			{
				return true;
			}
		}

		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getSubGoodsDef(subGoods, fxm, gz, type);
		}
		else
		{
			return AccessBaseDB.getDefault().getSubGoodsDef(subGoods, fxm, gz, type);
		}
	}

	// 查找付款方式
	public PayModeDef searchPayMode(String code)
	{
		PayModeDef mode = null;

		for (int i = 0; i < GlobalInfo.payMode.size(); i++)
		{
			mode = (PayModeDef) GlobalInfo.payMode.elementAt(i);

			if (mode.code.trim().equals(code.trim())) { return mode; }
		}

		return null;
	}

	// 传入付款代码,得到对应的主付款
	public PayModeDef searchMainPayMode(String code)
	{
		String bjcode;
		PayModeDef pay = null;

		bjcode = code;

		do
		{
			// 找到自己
			pay = searchPayMode(bjcode);

			if (pay == null) { return null; }

			// 如果是最上级，返回
			if (pay.sjcode.equals("0") || pay.sjcode.equals(pay.code)) { return pay; }

			// 继续查找上级代码
			bjcode = pay.sjcode;
		} while (true);
	}

	public boolean isChildPayMode(String sjcode, String code)
	{
		String bjcode;
		PayModeDef pay = null;

		bjcode = code;

		do
		{
			// 找到自己
			pay = searchPayMode(bjcode);

			if (pay == null) { return false; }

			// 自己的上级=sjcode
			if (pay.sjcode.equals(sjcode)) { return true; }

			// 找到的付款方式是第一级跳出循环
			if (pay.sjcode.equals("0") || pay.sjcode.equals(pay.code))
			{
				break;
			}

			// 继续查找上级代码
			bjcode = pay.sjcode;
		} while (true);

		return false;
	}

	public char getTimeCodeByName(String name)
	{
		PosTimeDef def = null;

		for (int i = 0; i < GlobalInfo.posTime.size(); i++)
		{
			def = (PosTimeDef) GlobalInfo.posTime.elementAt(i);

			if (def.name.equals(name)) { return def.code; }
		}

		if (name.equals(Language.apply("全天"))) { return '0'; }

		return (char) 0;
	}

	public String getTimeNameByCode(char code)
	{
		PosTimeDef def = null;

		for (int i = 0; i < GlobalInfo.posTime.size(); i++)
		{
			def = (PosTimeDef) GlobalInfo.posTime.elementAt(i);

			if (def.code == code) { return def.name; }
		}

		if (code == '0') { return Language.apply("全天"); }

		return String.valueOf(code);
	}

	public OperUserDef personGrant()
	{
		return personGrant(Language.apply("员工卡授权"));
	}

	public OperUserDef personGrant(String title)
	{
		PersonGrantForm window = new PersonGrantForm();

		if (!window.open(title)) { return null; }

		return window.getStaff();
	}

	public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		return sendSaleData(saleHead, saleGoods, salePayment, null);
	}

	public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Sqldb sql)
	{
		if (!GlobalInfo.isOnline)// && !(ConfigClass.DataBaseEnable.equals("Y")
									// && GlobalInfo.RemoteDB != null)
		{ return false; }

		boolean again;

		// 送网小票返回数据
		Vector retValue = new Vector();

		// sql对象为空,非重发小票
		if (sql == null)
		{
			again = false;
		}
		else
		{
			again = true;
		}

		// 发送小票
		int result = 0;
		/*
		 * // 如果 ConfigClass.DataBaseEnable.equals("Y") 条件成立 则小票信息发送到远程数据库 if
		 * (ConfigClass.DataBaseEnable.equals("Y")) { result =
		 * AccessRemoteDB.getDefault().writeSale(saleHead, saleGoods,
		 * salePayment); } else { result =
		 * NetService.getDefault().sendSaleData(saleHead, saleGoods,
		 * salePayment, retValue); }
		 */
		result = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue);

		// 非重发如果返回不为0，表示小票发送失败
		if (!again && result != 0)
			return false;

		// 重发小票，如果返回为2表示小票已存在，0表示成功，其他为送网失败
		if (again && result != 0 && result != 2)
			return false;

		// 得到返回数据,可对返回数据进行处理
		if (retValue.size() > 0)
		{
			String memo = retValue.elementAt(0).toString();
			double value = Double.parseDouble(CommonMethod.isNull(retValue.elementAt(1).toString(), "0"));

			updateSendSaleData(saleHead, memo, value, sql);
		}

		// 发送小票成功后更新小票送网标志
		if (sql == null)
		{
			// 更新小票送网标志
			AccessDayDB.getDefault().updateSaleBz(saleHead.fphm, 1, 'Y');
		}
		else
		{
			// 重发未送网小票时，不能用sql的execute(sqltext)方法
			// 和前面selectData换一个对象执行,否则冲突
			// 更新小票送网标志
			sql.setSql("update SALEHEAD set netbz = 'Y' where syjh = '" + saleHead.syjh + "' and fphm = " + String.valueOf(saleHead.fphm));
			sql.executeSql();
		}

		// 需要将小票发送到独立会员服务器
		if (GlobalInfo.sysPara.sendsaletocrm == 'Y')
		{
			sendSaleDataToMemberDB(saleHead, saleGoods, salePayment, again);
		}

		// 需要联网实时计算返券
		if (GlobalInfo.sysPara.calcfqbyreal == 'Y')
		{
			getSellRealFQ(saleHead);
		}

		if (GlobalInfo.sysPara.calcmystorecouponbyreal != 'N')
		{
			CreatePayment.getDefault().getPaymentMyStore().getMyStoreCoupon(saleHead, saleGoods, salePayment);
		}

		// 需要联网实时计算积分
		if (GlobalInfo.sysPara.calcjfbyconnect == 'Y' || GlobalInfo.sysPara.calcjfbyconnect == 'A')
		{
			getCustomerSellJf(saleHead, saleGoods, salePayment);
		}

		// 需要将小票送往WebService
		sendSaleWebService(saleHead, saleGoods, salePayment);

		return true;
	}

	/**
	 * 中免
	 * 
	 * @param saleHead
	 * @param saleGoods
	 * @param salePayment
	 * @param saleCust
	 *            顾客信息
	 * @return
	 */
	public boolean sendSaleDataCust(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, SaleCustDef saleCust)
	{
		return sendSaleDataCust(saleHead, saleGoods, salePayment, saleCust, null);
	}

	/**
	 * 中免
	 * 
	 * @param saleHead
	 * @param saleGoods
	 * @param salePayment
	 * @param saleCust
	 *            顾客信息
	 * @param sql
	 * @return
	 */
	public boolean sendSaleDataCust(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, SaleCustDef saleCust, Sqldb sql)
	{
		return sendSaleData(saleHead, saleGoods, salePayment, null);
	}

	public void updateSendSaleData(SaleHeadDef saleHead, String memo, double value, Sqldb sql)
	{
	}

	public int doRefundExtendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		return NetService.getDefault().sendExtendSaleData(saleHead, saleGoods, salePayment, retValue);
	}

	public boolean doWhtBlackList(String oprtype, String cardno, String termflag, String oprtime, String logicno, String physicno, String primtype, String subtype, String memo)
	{
		if (GlobalInfo.isOnline)
		{
			if (!NetService.getDefault().doWhtBlackList(oprtype, cardno, termflag, oprtime, logicno, physicno, primtype, subtype, memo)) { return false; }
		}
		else
		{
			if (!AccessBaseDB.getDefault().doWhtBlackList(cardno)) { return false; }
		}
		return true;
	}

	// 发生小票到CRM
	public void sendSaleDataToMemberDB(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, boolean again)
	{
		boolean sendok = true;

		int result = NetService.getDefault().sendExtendSaleData(saleHead, saleGoods, salePayment, null);

		// 非重发如果返回不为0，表示小票发送失败
		if (!again && result != 0)
			sendok = false;

		// 重发小票，如果返回为2表示小票已存在，0表示成功，其他为送网失败
		if (again && result != 0 && result != 2)
			sendok = false;

		if (result == -500)
			sendok = true;

		// 送网失败，记录小票未发送到CRM数据源的命令任务
		if (!sendok)
		{
			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDINVTOEXTEND, GlobalInfo.balanceDate + "," + saleHead.fphm);

			// 提示
			if (!NetService.getDefault().isStopService())
				new MessageBox(Language.apply("上传小票到会员服务器失败\n请去会员中心查询!"));
		}
	}

	// 获取小票实时积分
	public void getCustomerSellJf(SaleHeadDef saleHead, Vector saleGoods, Vector salePay)
	{
		getCustomerSellJf(saleHead);
	}

	// 获取小票实时积分
	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		String[] row = new String[4];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh, saleHead.hytype))
			{
				saleHead.bcjf = Double.parseDouble(row[0]);
				saleHead.ljjf = Double.parseDouble(row[1]);
				saleHead.str5 = row[2];

				if (GlobalInfo.sysPara.sendhyjf == 'Y')
				{
					if (!sendHykJf(saleHead))
					{
						new MessageBox(Language.apply("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!"));
					}
				}

				if ((Math.abs(saleHead.bcjf) > 0 || Math.abs(saleHead.ljjf) > 0) && GlobalInfo.sysPara.calcjfbyconnect == 'Y')
				{
					StringBuffer sb = new StringBuffer();
					sb.append(Language.apply("本笔交易存在积分\n"));
					sb.append(Language.apply("本次积分: ") + Convert.appendStringSize("", String.valueOf(saleHead.bcjf), 0, 10, 10, 1) + "\n");
					sb.append(Language.apply("累计积分: ") + Convert.appendStringSize("", String.valueOf(saleHead.ljjf), 0, 10, 10, 1));

					new MessageBox(sb.toString());
				}

				AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf);
			}
			else
			{
				saleHead.bcjf = 0;
				new MessageBox(Language.apply("计算本笔交易小票积分失败\n请到会员中心查询积分!"));
			}
		}
	}

	// 获取小票实时返券
	public void getSellRealFQ(SaleHeadDef saleHead)
	{
		String[] row = new String[3];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			if (NetService.getDefault().getSellRealFQ(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm)))
			{
				saleHead.memo = row[0] + "," + row[1];

				double faq = Convert.toDouble(row[0]);
				double fbq = Convert.toDouble(row[1]);
				AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 2, faq, fbq);

				// 提示
				if ((Convert.toDouble(row[0]) > 0) || (Convert.toDouble(row[1]) > 0))
				{
					StringBuffer sb = new StringBuffer();
					sb.append(Language.apply("本笔交易有活动返券\n"));
					sb.append(Language.apply("返A券: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(faq), 0, 10, 10, 1) + "\n");
					sb.append(Language.apply("返B券: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(fbq), 0, 10, 10, 1));
					new MessageBox(sb.toString());
				}
			}
			else
			{
				saleHead.memo = "-1,-1";
				new MessageBox(Language.apply("计算本笔交易小票返券失败\n请到会员中心查询返券!"));
			}
		}
	}

	public boolean sendHykJf(SaleHeadDef saleHead, Vector saleGoods, Vector salePay)
	{
		return sendHykJf(saleHead);
	}

	public boolean sendHykJf(SaleHeadDef saleHead)
	{
		if (saleHead != null)
		{
			if (NetService.getDefault().sendHykJf(saleHead))
				return true;

			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDHYKJF, GlobalInfo.balanceDate + "," + saleHead.fphm);

			return false;
		}

		return true;
	}

	// 附加扩展方法
	public int sendSaleWebService(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		if (EBill.getDefault().isEnable())
		{
			if (!EBill.getDefault().sendSaleBill(saleHead))
			{
				AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDWEBSERVICE, GlobalInfo.balanceDate + "," + saleHead.fphm);
				AccessDayDB.getDefault().writeWorkLog(Language.apply("更新手持终端单据状态失败"));
				return -1;
			}
			AccessDayDB.getDefault().writeWorkLog(Language.apply("更新手持终端单据状态成功"));
		}

		return 0;
	}

	public boolean getPreSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		try
		{
			if (!GlobalInfo.isOnline)
			{
				new MessageBox(Language.apply("脱网状态下不支持预售提货!"));

				return false;
			}

			// Test lwj
			/**
			 * try { Sqldb sql = null; sql = GlobalInfo.dayDB; if (sql != null)
			 * { ResultSet rs = null;
			 * 
			 * try { boolean bOK; bOK = true; rs =
			 * sql.selectData("select * from SALEHEAD where fphm = " + fphm +
			 * " order by fphm"); if (rs != null && rs.next()) {
			 * 
			 * if (!sql.getResultSetToObject(shd)) { bOK = false; return bOK; }
			 * sql.resultSetClose();
			 * 
			 * 
			 * // 读取商品明细 rs =
			 * sql.selectData("select * from SALEGOODS where syjh = '" +
			 * shd.syjh + "' and fphm = " + String.valueOf(shd.fphm) +
			 * " order by rowno"); while (rs != null && rs.next()) {
			 * SaleGoodsDef sg = new SaleGoodsDef();
			 * 
			 * if (!sql.getResultSetToObject(sg)) { return bOK; }
			 * 
			 * saleDetailList.add(sg); } sql.resultSetClose();
			 * 
			 * // 读取付款明细 rs =
			 * sql.selectData("select * from SALEPAY where syjh = '" + shd.syjh
			 * + "' and fphm = " + String.valueOf(shd.fphm) +
			 * " order by rowno"); while (rs != null && rs.next()) { SalePayDef
			 * sp = new SalePayDef();
			 * 
			 * if (!sql.getResultSetToObject(sp)) { bOK = false; return bOK; }
			 * 
			 * payDetail.add(sp); } sql.resultSetClose();
			 * 
			 * } } catch(Exception ex) { ex.printStackTrace(); } finally {
			 * sql.resultSetClose(); } } }catch(Exception er) {
			 * er.printStackTrace(); }
			 */
			if (!NetService.getDefault().getPreSaleInfo(syjh, fphm, shd, saleDetailList, payDetail)) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	public boolean getBackSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		try
		{
			if (!GlobalInfo.isOnline)
			{
				if ((syjh == null) || syjh.trim().equals(""))
				{
					new MessageBox(Language.apply("脱网状态下不支持后台退货!"));
				}
				else
				{
					new MessageBox(Language.apply("脱网状态下不支持指定小票退货!"));
				}

				return false;
			}

			if (!NetService.getDefault().getBackSaleInfo(syjh, fphm, shd, saleDetailList, payDetail)) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	public Vector getGoodsPayRule(GoodsDef gd)
	{
		if (GlobalInfo.isOnline)
		{
			NetService netservice = NetService.getDefault();

			return netservice.getGoodsPayRule(gd);
		}
		else
		{
			AccessBaseDB accessbasedb = AccessBaseDB.getDefault();

			return accessbasedb.getGoodsPayRule(gd);
		}
	}

	public boolean findVIPZKL(CustomerVipZklDef zklDef, String custcode, String custtype, GoodsDef gd)
	{
		// 联网本地优先查询(Y-联网优先本地查询再查网上/Z-联网只查询本地)
		if (GlobalInfo.isOnline && GlobalInfo.sysPara.localfind != 'N')
		{
			if (!AccessBaseDB.getDefault().findVIPZKL(zklDef, custcode, custtype, gd))
			{
				// Z-联网只查询本地,失败不再查询网上
				if (GlobalInfo.sysPara.localfind == 'Z') { return false; }
			}
			else
			{
				return true;
			}
		}

		if (GlobalInfo.isOnline)
		{
			NetService netservice = NetService.getDefault();

			return netservice.findVIPZKL(zklDef, custcode, custtype, gd);
		}
		else
		{
			AccessBaseDB accessbasedb = AccessBaseDB.getDefault();

			return accessbasedb.findVIPZKL(zklDef, custcode, custtype, gd);
		}
	}

	public Vector getSaleTicketMSInfo(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		if (GlobalInfo.sysPara.calcfqbyreal != 'A')
			return null;

		// 查询小票实时赠品信息
		Vector v = new Vector();
		NetService netservice = NetService.getDefault();
		if (netservice.getSaleTicketMSInfo(v, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), "N", NetService.getDefault().getMemCardHttp(CmdDef.GETMSINFO))) { return v; }

		return null;
	}

	public Vector getSellJfList(Vector v, SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		// 查询积分明细
		NetService netservice = NetService.getDefault();
		if (netservice.getSellJfList(v, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), "N", NetService.getDefault().getMemCardHttp(CmdDef.GETSALEJFLIST))) { return v; }

		return null;
	}

	public void playGoodsSound(int rtnValue)
	{
		// 声音文件路径
		String audioPath = "";

		if (rtnValue == 0)
		{
			if (ConfigClass.SoundSuccess == null) { return; }

			audioPath = ConfigClass.SoundSuccess;
		}
		else
		{
			if (ConfigClass.SoundFail == null) { return; }

			audioPath = ConfigClass.SoundFail;
		}

		try
		{
			CommonMethod.waitForExec(audioPath);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public boolean getCreditCardZK(CustFilterDef filter, String code, String track, String mktcode, String gz, String catid, String ppcode, String specialInfo, String djlb)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getCreditCardZK(filter, code, track, mktcode, gz, catid, ppcode, specialInfo, djlb);
		}
		else
		{
			new MessageBox(Language.apply("脱网下不支持此功能"));
			return false;
		}
	}

	public boolean getCreditCardList(Vector v, String mktcode)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getCreditCardList(v, mktcode);
		}
		else
		{
			new MessageBox(Language.apply("脱网下不支持此功能"));
			return false;
		}
	}

	// 发送挂单信息
	public boolean sendSaleGd(SaleHeadDef salehead, Vector saleGoods)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().sendSaleGd(salehead, saleGoods);
		}
		else
		{
			new MessageBox(Language.apply("脱网下不支持此功能"));
			return false;
		}
	}

	// 获得挂单信息
	public boolean getSaleGdInfo(String invno, SaleHeadDef salehead, Vector salegdgoods)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getSaleGdInfo(invno, salehead, salegdgoods);
		}
		else
		{
			new MessageBox(Language.apply("脱网下不支持此功能"));
			return false;
		}
	}

	// 删除挂单信息
	public boolean delSaleGdInfo(String syjh, String invno)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().delSaleGdInfo(syjh, invno);
		}
		else
		{
			new MessageBox(Language.apply("脱网下不支持此功能"));
			return false;
		}
	}

	//
	public boolean sendSaleAppend(Vector saleappendlist)
	{
		if (!GlobalInfo.isOnline) { return false; }

		// 发送小票附加信息
		boolean result = NetService.getDefault().sendSaleAppend(saleappendlist);

		if (result && saleappendlist.size() > 0)
		{
			SaleAppendDef sad = (SaleAppendDef) saleappendlist.get(0);
			// 更新送网标志
			AccessDayDB.getDefault().updateSaleBz(sad.fphm, 3, 'Y');
		}

		return result;
	}

	// 从网上取一个值
	public String[] getOneCommonValues(char type, String syjh, String paravalue1, String paravalue2)
	{
		if (GlobalInfo.isOnline)
		{
			return NetService.getDefault().getOneCommonValues(type, syjh, paravalue1, paravalue2);
		}
		else
		{
			return null;
		}
	}

	public boolean getGoodsOrCatePages(Vector listgoods, boolean searchflag, long startpos, long endpos, long cateid, int level)
	{
		boolean result = false;
		try
		{
			if (GlobalInfo.isOnline)
			{
				result = NetService.getDefault().getGoodsOrCategoryPages(listgoods, searchflag, startpos, endpos, cateid, level);
			}
			else
			{
				result = AccessBaseDB.getDefault().getGoodsOrCategoryPages(listgoods, searchflag, startpos, endpos, cateid, level);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return result;
	}

	public long getGoodsOrCateMaxCount(boolean searchflag, long cateid, int level)
	{
		long maxcount = 0;
		try
		{
			if (GlobalInfo.isOnline)
			{
				maxcount = NetService.getDefault().getGoodsOrCategoryMaxCount(searchflag, cateid, level);
			}
			else
			{
				maxcount = AccessBaseDB.getDefault().getGoodsOrCategoryMaxCount(searchflag, cateid, level);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return maxcount;
	}

	public boolean getGroupBuy(String fphm, Vector saleDetailList)
	{
		try
		{
			if (!GlobalInfo.isOnline)
			{
				new MessageBox(Language.apply("脱网状态下不支持团购!"));

				return false;
			}

			if (!NetService.getDefault().getGroupBuyInfo(fphm, saleDetailList)) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	public boolean findStamYh(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String ppcode, String spec, String time, String yhtime, String stampCode)
	{
		try
		{
			if (!GlobalInfo.isOnline)
			{
				new MessageBox(Language.apply("脱网暂时不支持印花促销"));
				return false;
			}

			if (!NetService.getDefault().findStampYh(ruleDef, code, gz, catid, ppcode, spec, time, yhtime, stampCode, NetService.getDefault().getMemCardHttp(CmdDef.FINDSTAMP), CmdDef.FINDSTAMP)) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}
}
