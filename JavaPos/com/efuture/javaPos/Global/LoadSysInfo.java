package com.efuture.javaPos.Global;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.Vector;

import mediaPlayer.MainFun;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.DiskSpace;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.commonKit.TextBox;
import com.efuture.commonKit.Unzip;
import com.efuture.javaPos.AssemblyInfo;
import com.efuture.javaPos.Communication.DownBaseTask;
import com.efuture.javaPos.Communication.DownloadData;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Communication.UpdateBaseInfo;
import com.efuture.javaPos.Device.BankTracker;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.ElectronicScale;
import com.efuture.javaPos.Device.KeyBoard;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Device.MSR;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Device.Scanner;
import com.efuture.javaPos.Device.SecMonitor;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.RemoveDayBS;
import com.efuture.javaPos.Logic.SaleBS0Data;
import com.efuture.javaPos.Logic.ShortcutKeyBS;
import com.efuture.javaPos.PrintTemplate.ArkGroupBillMode;
import com.efuture.javaPos.PrintTemplate.BusinessPerBillMode;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.PrintTemplate.DisplayMode;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.HangBillMode;
import com.efuture.javaPos.PrintTemplate.InvoiceSummaryMode;
import com.efuture.javaPos.PrintTemplate.MzkRechargeBillMode;
import com.efuture.javaPos.PrintTemplate.PayinBillMode;
import com.efuture.javaPos.PrintTemplate.SaleAppendBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.PrintTemplate.StoredCardStatisticsMode;
import com.efuture.javaPos.PrintTemplate.SyySaleBillMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;
import com.efuture.javaPos.Struct.CallInfoDef;
import com.efuture.javaPos.Struct.GlobalParaDef;
import com.efuture.javaPos.Struct.InvoiceInfoDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.TasksDef;
import com.efuture.javaPos.UI.Design.ImportSmallTicketBackupForm;
import com.efuture.javaPos.UI.Design.LoginForm;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.PersonnelGoForm;
import com.efuture.javaPos.UI.Design.PreMoneyForm;
import com.swtdesigner.SWTResourceManager;

import device.ICCard.Muti_ICCard;

//加载系统信息
public class LoadSysInfo
{
	public static LoadSysInfo currentLoadSysInfo = null;
	public boolean needimportsale = false;
	protected boolean rerunflag = false;

	public static LoadSysInfo getDefault()
	{
		if (LoadSysInfo.currentLoadSysInfo == null)
		{
			LoadSysInfo.currentLoadSysInfo = CustomLocalize.getDefault().createLoadSysInfo();
		}

		return LoadSysInfo.currentLoadSysInfo;
	}

	public void setLabelHint(Label status, String msg)
	{
		status.setText(msg);
		status.getDisplay().update();
	}

	public void openMediaPlayer(Label lbl_message)
	{
		if (!ConfigClass.SecMonitor_Open.equals("Y"))
			return;
		if (GlobalInfo.sysPara != null && GlobalInfo.sysPara.secMonitorPlayer != 'Y')
			return;

		// 检查功能授权
		setLabelHint(lbl_message, Language.apply("正在检查广告播放模块授权......"));
		if (!CustomLocalize.getDefault().checkAllowUseMovie())
			return;

		// 检查是否开启广告播放模块
		if (ConfigClass.ServerID == null || ConfigClass.ServerID.trim().length() <= 0)
		{
			if (Display.getDefault().getMonitors().length >= 2 || ConfigClass.DebugMode)
			{
				setLabelHint(lbl_message, Language.apply("正在开启广告播放模块......"));

				Display.getCurrent().syncExec(new Runnable()
				{
					public void run()
					{
						MainFun main = new MainFun(ConfigClass.MediaPath);
						main.init();
					}
				});
			}
			else
			{
				// 本机无双屏
				return;
			}
		}

		// 连接广告播放屏
		setLabelHint(lbl_message, Language.apply("正在连接广告播放模块......"));
		String id = ConfigClass.ServerID;
		if (id == null || id.trim().length() <= 0)
			id = "localhost";
		SecMonitor sec = new SecMonitor(id, ConfigClass.Port);
		if (sec.getStatus())
			SecMonitor.secMonitor = sec;
	}

	// 开机载入收银信息
	public boolean startLoadInfo(Label lbl_message)
	{
		setLabelHint(lbl_message, Language.apply("正在检查系统模块类型......"));
		GlobalInfo.background.setModuleType(CustomLocalize.getDefault().getAssemblyVersion() + " " + GlobalInfo.ModuleType);

		// 初始化设备
		setLabelHint(lbl_message, Language.apply("正在初始化系统设备......"));
		if (!initDevice(lbl_message)) { return false; }

		setLabelHint(lbl_message, Language.apply("正在加载第三方插件......"));
		initRdPlugins();
		
		// 检查系统是否未经过正常途径关机
		setLabelHint(lbl_message, Language.apply("正在检查系统状态......"));
		checkExceptionShutdown(true);

		// 加载快捷键
		setLabelHint(lbl_message, Language.apply("正在载入快捷键定义......"));

		ShortcutKeyBS skbs = CustomLocalize.getDefault().createShortcutKeyBS();
		skbs.loadFile(GlobalVar.ShortcutKeyFile);
		skbs.setKeyList();

		// 读取模板配置
		setLabelHint(lbl_message, Language.apply("正在读取文件配置模版......"));
		if (!getConfigTemplate(lbl_message)) { return false; }

		// 创建HTTP连接
		while (true)
		{
			if (!NetService.getDefault().createHttpConnection()) { return false; }

			GlobalInfo.background.setIPText("[" + ConfigClass.CashRegisterCode + "] " + GlobalInfo.ipAddr);

			if (GlobalInfo.ipAddr.equals("127.0.0.1") || GlobalInfo.ipAddr.equals(ConfigClass.CashRegisterCode))
			{
				int ret = new MessageBox(Language.apply("系统启动时网络有延迟,可能无法进行联网\n\n是否重新连接网络？\n\n任意键-重试 / 2-放弃 "), null, false).verify();
				if (ret != GlobalVar.Key2)
				{
					continue;
				}
			}
			break;
		}

		// 创建WebService连接对象
		NetService.getDefault().createWebServerConn();

		// 创建Plugin插件对象
		// NetService.getDefault().createPlugin();

		// 同步时间的同时，检查网络是否连接
		setLabelHint(lbl_message, Language.apply("正在同步收银机时间......"));
		DataService.getDefault().getServerTime(true);

		// 联网失败，提示可从IPList.ini中切换POSSERVER连接
		if (!GlobalInfo.isOnline)
			MenuFuncBS.openPosIPList(null, null);

		// 刷新状态栏网络状态
		GlobalInfo.statusBar.setNetStatus();

		//
		setLabelHint(lbl_message, Language.apply("正在删除历史无效数据......"));
		RemoveDayBS.autoRemoveDataBase();

		//
		setLabelHint(lbl_message, Language.apply("正在下载基础数据库......"));
		DownloadData.downloadBaseDB(lbl_message);

		//
		setLabelHint(lbl_message, Language.apply("正在校验本地数据库......"));

		if (!checkLocalDB(lbl_message)) { return false; }

		//
		setLabelHint(lbl_message, Language.apply("正在创建每日数据库......"));

		if (!createDayDB()) { return false; }

		//
		setLabelHint(lbl_message, Language.apply("正在连接本地数据库......"));

		if (!loadLocalDB(lbl_message)) { return false; }

		//
		setLabelHint(lbl_message, Language.apply("正在连接JSTORE数据库......"));

		if (!loadRemoteDB(lbl_message)) { return false; }

		//
		setLabelHint(lbl_message, Language.apply("正在检查收银机合法性......"));

		if (!DataService.getDefault().checkSyjValid()) { return false; }

		//
		setLabelHint(lbl_message, Language.apply("正在读取收银机状态......"));

		if (!AccessLocalDB.getDefault().readSyjStatus()) { return false; }

		//
		setLabelHint(lbl_message, Language.apply("正在发送收银机开机信息......"));

		if (GlobalInfo.syjStatus.status != StatusType.STATUS_LEAVE)
		{
			GlobalInfo.syjStatus.status = StatusType.STATUS_START;
		}

		AccessLocalDB.getDefault().writeSyjStatus();
		DataService.getDefault().sendSyjStatus();

		// 记录开机工作日志
		if (GlobalInfo.isOnline)
		{
			AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机联网开机"), StatusType.WORK_BOOT);
		}
		else
		{
			AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机脱网开机,请检查"), StatusType.WORK_BOOT);
		}

		//
		setLabelHint(lbl_message, Language.apply("正在下载系统基本信息......"));

		if (!getNetNewData(lbl_message)) { return false; }

		//
		setLabelHint(lbl_message, Language.apply("正在检查系统注册信息......"));

		if (!checkLicence()) { return false; }

		//
		setLabelHint(lbl_message, Language.apply("正在检查系统使用有效期......"));

		if (!checkServiceDate()) { return false; }

		//
		setLabelHint(lbl_message, Language.apply("正在检查系统版本信息......"));

		if (!checkVersion()) { return false; }

		// 根据数据库参数定义在开机检查之后再开启广告播放
		openMediaPlayer(lbl_message);

		//
		try
		{
			setLabelHint(lbl_message, Language.apply("正在执行未完成任务......"));
			DataService.getDefault().execHistoryTask();
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		//
		setLabelHint(lbl_message, Language.apply("正在检查剩余磁盘空间......"));
		checkfreeSpace(lbl_message);

		//
		setLabelHint(lbl_message, Language.apply("正在检查交易小票号......"));
		DataService.getDefault().checkInvoiceNo();

		//
		setLabelHint(lbl_message, Language.apply("正在检查断点数据......"));
		boolean broken = checkBroken();

		// 系统启动成功
		setLabelHint(lbl_message, "");

		// 启动后台定时器
		startBackgroundTimer();

		// 恢复断点数据,检查是否需要自动登录
		if (!revertBroken(broken)) { return false; }

		// 检查外设并给出提示
		checkDeviceMessage();

		// 读取当天收银汇总,因为现金存量要加上备用金,所以必须放在备用金输入完成以后
		setLabelHint(lbl_message, Language.apply("正在读取当天收银信息......"));
		AccessDayDB.getDefault().readSyjSaleState();

		//
		setLabelHint(lbl_message, Language.apply("正在发送收银机登录信息......"));
		GlobalInfo.syjStatus.status = StatusType.STATUS_LOGIN;
		AccessLocalDB.getDefault().writeSyjStatus();
		DataService.getDefault().sendSyjStatus();

		// 登录成功
		setLabelHint(lbl_message, "");

		// 恢复数据库成功,导入销售小票
		if (needimportsale)
		{
			needimportsale = false;
			setLabelHint(lbl_message, Language.apply("正在恢复损坏的数据库销售记录......"));
			new ImportSmallTicketBackupForm();
		}

		return true;
	}

	protected void checkDeviceMessage()
	{
		// 提示打印机是否有效
		if (!ConfigClass.DebugMode && !Printer.getDefault().getStatus())
		{
			AccessDayDB.getDefault().writeWorkLog(Language.apply("打印机未连接,打印操作将无法执行"));

			new MessageBox(Language.apply("打印机未连接,打印操作将无法执行"));
		}

		// 调试模式下提示客显,打印机是否已启用
		if (ConfigClass.DebugMode && ConfigClass.LineDispaly1 != null && ConfigClass.LineDispaly1.length() > 12 && GlobalInfo.syjDef.isdisp == 'N')
		{
			new MessageBox(Language.apply("收银机定义中未启用客显"));
		}

		if (ConfigClass.DebugMode && ConfigClass.Printer1 != null && ConfigClass.Printer1.length() > 8 && GlobalInfo.syjDef.isprint == 'N')
		{
			new MessageBox(Language.apply("收银机定义中未启用打印机"));
		}
	}

	public void getLocalNewData()
	{
		if (!(new File(GlobalVar.ConfigPath + "//LocalSysPara.ini").exists()))
			return;

		BufferedReader br;
		br = CommonMethod.readFile(GlobalVar.ConfigPath + "/LocalSysPara.ini");
		if (br == null)
			return;
		String line;
		String[] sp;
		try
		{
			while ((line = br.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}
				String[] lines = line.split("&&");
				sp = lines[0].split("=");
				if (sp.length < 2)
					continue;
				AccessLocalDB.getDefault().paraConvertByCode(sp[0].trim(), sp[1].trim());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void startBackgroundTimer()
	{
		// 创建定时器
		if (GlobalInfo.backgroundTimer != null)
		{
			GlobalInfo.backgroundTimer.cancel();
		}

		GlobalInfo.backgroundTimer = new Timer();

		// 查询线程启动
		if (GlobalInfo.sysPara.querytime > 0)
		{
			if (GlobalInfo.sysPara.querytime < 60)
			{
				GlobalInfo.sysPara.querytime = 60;
			}

			TaskThread thread = new TaskThread();
			GlobalInfo.backgroundTimer.schedule(thread, 1000, GlobalInfo.sysPara.querytime * 1000);
		}

		// 下载线程启动
		if (GlobalInfo.sysPara.downloadtime > 0)
		{
			if (GlobalInfo.sysPara.downloadtime < 180)
			{
				GlobalInfo.sysPara.downloadtime = 180;
			}

			DownBaseTask thread1 = new DownBaseTask();
			GlobalInfo.backgroundTimer.schedule(thread1, 1000, GlobalInfo.sysPara.downloadtime * 1000);
		}

		// 空闲线程启动,主要用于检测时间，切换当日数据源
		IdleThread thread2 = new IdleThread();
		GlobalInfo.backgroundTimer.schedule(thread2, 1000, 5000);
	}

	public boolean checkBroken()
	{
		String gh = SaleBS0Data.checkBrokenData();

		if ((gh == null) || gh.equals("")) { return false; }

		// 自动登录
		GlobalInfo.syjStatus.syyh = gh;

		return true;
	}

	public boolean startBroken(boolean b)
	{
		boolean broken = b;

		if (broken || (GlobalInfo.syjStatus.status == StatusType.STATUS_LEAVE))
		{
			broken = false;

			// 断点保护自动登录
			if (GlobalInfo.sysPara.havebroken == 'Y')
			{
				// 查找收银员
				OperUserDef staff = new OperUserDef();

				if ((GlobalInfo.syjStatus.syyh.length() > 0) && DataService.getDefault().getOperUser(staff, GlobalInfo.syjStatus.syyh))
				{
					// 设置当前收银员
					GlobalInfo.posLogin = staff;

					//
					AccessDayDB.getDefault().writeWorkLog(Language.apply("断点保护,收银员自动登录"), StatusType.WORK_LOGIN);

					//
					broken = true;
				}
				else
				{
//					AccessDayDB.getDefault().writeWorkLog("断点保护,找不到[" + GlobalInfo.syjStatus.syyh + "]收银员,无法自动登录");
					AccessDayDB.getDefault().writeWorkLog(Language.apply("断点保护,找不到[{0}]收银员,无法自动登录", new Object[]{GlobalInfo.syjStatus.syyh}));
				}
			}
		}

		return broken;
	}

	public boolean revertBroken(boolean b)
	{
		if (!startBroken(b))
		{
			// 登录
			if (!new LoginForm().open(null)) { return false; }

			if ("Y".equals(GlobalInfo.sysPara.isinputpremoney))
			{
				// 输入备用金
				new PreMoneyForm().open();
			}
		}
		else
		{
			if (GlobalInfo.syjStatus.status == StatusType.STATUS_LEAVE)
			{
				// 恢复离开锁定状态
				new PersonnelGoForm();
			}
			else
			{
				// 读取断点数据
			}

		}
		return true;
		/*
		 * boolean broken = b;
		 * 
		 * // if (broken || (GlobalInfo.syjStatus.status ==
		 * StatusType.STATUS_LEAVE)) { broken = false;
		 * 
		 * // 断点保护自动登录 if (GlobalInfo.sysPara.havebroken == 'Y') { // 查找收银员
		 * OperUserDef staff = new OperUserDef();
		 * 
		 * if ((GlobalInfo.syjStatus.syyh.length() > 0) &&
		 * DataService.getDefault().getOperUser(staff,
		 * GlobalInfo.syjStatus.syyh)) { // 设置当前收银员 GlobalInfo.posLogin = staff;
		 * 
		 * // AccessDayDB.getDefault().writeWorkLog("断点保护,收银员自动登录",
		 * StatusType.WORK_LOGIN);
		 * 
		 * // broken = true; } else {
		 * AccessDayDB.getDefault().writeWorkLog("断点保护,找不到[" +
		 * GlobalInfo.syjStatus.syyh + "]收银员,无法自动登录"); } } }
		 * 
		 * // if (!broken) { // 登录 if (!new LoginForm().open(null)) { return
		 * false; }
		 * 
		 * if ("Y".equals(GlobalInfo.sysPara.isinputpremoney)) { // 输入备用金 new
		 * PreMoneyForm().open(); } } else { if (GlobalInfo.syjStatus.status ==
		 * StatusType.STATUS_LEAVE) { // 恢复离开锁定状态 new PersonnelGoForm(); } else
		 * { // 读取断点数据 } }
		 * 
		 * return true;
		 */}

	public boolean getConfigTemplate(Label lbl_message)
	{
		//
		setLabelHint(lbl_message, Language.apply("正在读取小票打印模版......"));

		if (!SaleBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取小票打印模版文件错误!"));
		}

		setLabelHint(lbl_message, Language.apply("正在读取小票汇总打印模版......"));

		if (!InvoiceSummaryMode.getDefault().ReadTemplateFile())
		{
			// new MessageBox("读取小票汇总打印模版文件错误!");
		}
		//
		setLabelHint(lbl_message, Language.apply("正在读取顾客显示模版......"));

		if (!DisplayMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取顾客显示模版文件错误!"));
		}
		else
		{
			// 显示欢迎信息
			DisplayMode.getDefault().lineDisplayWelcome();
		}

		setLabelHint(lbl_message, Language.apply("正在读取缴款打印模版......"));

		if (!PayinBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取缴款打印模版文件错误!"));
		}

		//
		setLabelHint(lbl_message, Language.apply("正在读取收银员销售报表打印模版......"));

		if (!SyySaleBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取收银员销售报表打印模版文件错误!"));
		}

		//
		setLabelHint(lbl_message, Language.apply("正在读取柜组对账单打印模版......"));

		if (!ArkGroupBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取柜组对账单打印模版文件错误!"));
		}

		//
		setLabelHint(lbl_message, Language.apply("正在读取营业员报表打印模版......"));

		if (!BusinessPerBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取营业员报表打印模版文件错误!"));
		}

		setLabelHint(lbl_message, Language.apply("正在读取电子卡联打印模版......"));
		if (!CardSaleBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取电子卡联打印模版文件错误!"));
		}

		setLabelHint(lbl_message, Language.apply("正在读取营业员联打印模版......"));
		if (!YyySaleBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取营业员联打印模版文件错误!"));
		}

		setLabelHint(lbl_message, Language.apply("正在读取小票附加数据联打印模版......"));
		if (!SaleAppendBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取小票附加数据联打印模版文件错误!"));
		}

		//
		setLabelHint(lbl_message, Language.apply("正在读取挂单小票打印模版......"));

		if (!HangBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取挂单小票打印模版文件错误!"));
		}

		// 读取赠券模板
		setLabelHint(lbl_message, Language.apply("正在读取赠券打印模版......"));

		if (GiftBillMode.getDefault().checkTemplateFile())
		{
			GiftBillMode.getDefault().ReadTemplateFile();
		}

		// 读取面值卡收款统计
		setLabelHint(lbl_message, Language.apply("正在读取面值卡收款统计打印模版......"));

		if (StoredCardStatisticsMode.getDefault().checkTemplateFile())
		{
			StoredCardStatisticsMode.getDefault().ReadTemplateFile();
		}

		// 读取面值卡充值模板
		setLabelHint(lbl_message, Language.apply("正在读取面值卡充值打印模版......"));

		if (!MzkRechargeBillMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取面值卡充值打印模版文件错误!"));
		}

		// 读取盘点单模版
		setLabelHint(lbl_message, Language.apply("正在读取盘点单打印模版......"));

		if (!CheckGoodsMode.getDefault().ReadTemplateFile())
		{
			new MessageBox(Language.apply("读取盘点单打印模版文件错误!"));
		}

		// 读取调用WebService的配置
		setLabelHint(lbl_message, Language.apply("正在读取WebService配置信息......"));
		if (!WebServiceConfigClass.getDefault().ReadWebServiceConfigFile())
		{
			new MessageBox(Language.apply("读取WebService配置文件错误!"));
		}

		// 读取功能模块配置
		setLabelHint(lbl_message, Language.apply("正在读取功能模块配置信息......"));
		readFunctionMode();
		return true;
	}

	public boolean readFunctionMode()
	{
		BufferedReader br = null;
		GlobalInfo.funcMap = new HashMap();
		try
		{
			try
			{
				if (!PathFile.fileExist(GlobalVar.ConfigPath + "\\FunctionID.ini"))
					return false;
				
				br = CommonMethod.readFileGBK(GlobalVar.ConfigPath + "\\FunctionID.ini");

				if (br == null)
				{
					// new MessageBox("打开" + GlobalVar.ConfigPath +
					// "\\FunctionID.ini" + "文件失败!");
					return false;
				}

				String line = null;

				while ((line = br.readLine()) != null)
				{
					if (line.trim().length() == 0)
						continue;
					if (line.trim().charAt(0) == ';')
						continue;
					if (line.trim().indexOf("=") > -1)
					{
						String func = line.split("=")[0].trim();
						String code = line.split("=")[1].trim();
						if (!GlobalInfo.funcMap.containsKey(func))
							GlobalInfo.funcMap.put(func, code);
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			finally
			{
				if (br != null)
				{
					br.close();
				}
			}
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean getNetNewData(Label lbl_message)
	{
		int i = 0;
		while (true)
		{
			while (true)
			{
				if (i == 0)
				{
					setLabelHint(lbl_message, Language.apply("正在获取菜单信息定义......"));
					if (!DataService.getDefault().getNetMenuFunc())
						break;
					i++;
				}

				if (i == 1)
				{
					setLabelHint(lbl_message, Language.apply("正在获取系统参数定义......"));
					if (!DataService.getDefault().getNetSysPara())
						break;
					LoadSysInfo.getDefault().getLocalNewData();
					i++;
				}

				if (i == 2)
				{
					setLabelHint(lbl_message, Language.apply("正在获取收银班次定义......"));
					if (!DataService.getDefault().getNetPosTime())
						break;
					i++;
				}

				if (i == 3)
				{
					setLabelHint(lbl_message, Language.apply("正在获取收银机的付款方式......"));
					if (!DataService.getDefault().getNetPayMode())
						break;
					i++;
				}

				if (i == 4)
				{
					setLabelHint(lbl_message, Language.apply("正在获取收银机的收银范围......"));
					if (!DataService.getDefault().getNetSyjGrange())
						break;
					i++;
				}

				if (i == 5)
				{
					setLabelHint(lbl_message, Language.apply("正在获取用户角色定义......"));
					if (!DataService.getDefault().getNetOperRole())
						break;
					i++;
				}

				if (i == 6)
				{
					setLabelHint(lbl_message, Language.apply("正在获取电子秤码规则......"));
					if (!DataService.getDefault().getNetDzcMode())
						break;
					i++;
				}

				if (i == 7)
				{
					setLabelHint(lbl_message, Language.apply("正在获取系统缴款模板......"));
					if (!DataService.getDefault().getNetPayinMode())
						break;
					i++;
				}

				if (i == 8)
				{
					setLabelHint(lbl_message, Language.apply("正在获取顾客信息种类......"));
					if (!DataService.getDefault().getNetBuyerInfo())
						break;
					i++;
				}

				if (i == 9)
				{
					setLabelHint(lbl_message, Language.apply("正在获取呼叫信息定义......"));
					if (!DataService.getDefault().getNetCallInfo())
						break;
					i++;
				}

				if (i == 10)
				{
					setLabelHint(lbl_message, Language.apply("正在获取备用信息定义......"));
					if (!DataService.getDefault().getNetMemoInfo())
						break;
					i++;
				}

				if (i == 11)
				{
					setLabelHint(lbl_message, Language.apply("正在获取系统管理架构......"));
					if (!DataService.getDefault().getNetManaFrame())
						break;
					i++;
				}

				if (i == 12)
				{
					setLabelHint(lbl_message, Language.apply("正在获取顾客卡类别定义......"));
					if (!DataService.getDefault().getNetCustomerType())
						break;
					i++;
				}

				if (i == 13)
				{
					setLabelHint(lbl_message, Language.apply("正在获取付款上限定义......"));
					if (!DataService.getDefault().getNetPaymentLimit())
						break;
					i++;
				}

				if (i == 14)
				{
					setLabelHint(lbl_message, Language.apply("正在获取系统附加项定义......"));
					if (!DataService.getDefault().getNewOtherExtItem())
						break;
					i++;
				}
				// 跳出循环
				break;
			}

			// 有数据获取失败
			if (i <= 12)
			{
				String msg = lbl_message.getText();
				msg = msg.replaceAll(Language.apply("正在"), "");
				msg = msg.replaceAll("\\.", "");
				int key = new MessageBox(msg + Language.apply("失败\n\n可能导致系统运行不正确\n\n1-放弃 / 2-退出 / 任意键-重试")).verify();
				if (key == GlobalVar.Key1)
				{
					AccessDayDB.getDefault().writeWorkLog(msg + Language.apply("失败,收银员放弃"));
					i++;
					continue;
				}
				else if (key == GlobalVar.Key2)
				{
					return false;
				}
				else
				{
					continue;
				}
			}
			else
				break;
		}

		return true;
	}

	public String getBaseDBName()
	{
		if (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite"))
		{
			return "Base.db3";
		}
		else
		{
			return "Base";
		}
	}

	public String getLocalDBName()
	{
		if (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite"))
		{
			return "Local.db3";
		}
		else
		{
			return "Local";
		}
	}

	public String getDayDBName()
	{
		if (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite"))
		{
			return "Day.db3";
		}
		else
		{
			return "Day";
		}
	}

	public String getJdbcDriverName()
	{
		if (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite"))
		{
			return "org.sqlite.JDBC";
		}
		else
		{
			return "org.apache.derby.jdbc.EmbeddedDriver";
		}
	}

	public String getJdbcConnurlName()
	{
		if (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite"))
		{
			return "jdbc:sqlite:";
		}
		else
		{
			return "jdbc:derby:";
		}
	}

	public boolean checkLocalDB(Label lbl_message)
	{
		Unzip zip = null;

		try
		{
			// 检查数据库是否以ZIP文件形式存在，如果是先解压
			if (!PathFile.fileExist(ConfigClass.LocalDBPath + getBaseDBName()) && PathFile.fileExist(ConfigClass.LocalDBPath + "Base.zip"))
			{
				if (zip == null)
				{
					zip = new Unzip();
				}

				zip.unzipAnt(ConfigClass.LocalDBPath + "Base.zip", ConfigClass.LocalDBPath, 1);
			}

			if (!PathFile.fileExist(ConfigClass.LocalDBPath + getLocalDBName()) && PathFile.fileExist(ConfigClass.LocalDBPath + "Local.zip"))
			{
				if (zip == null)
				{
					zip = new Unzip();
				}

				zip.unzipAnt(ConfigClass.LocalDBPath + "Local.zip", ConfigClass.LocalDBPath, 1);
			}

			if (!PathFile.fileExist(ConfigClass.LocalDBPath + getDayDBName()) && PathFile.fileExist(ConfigClass.LocalDBPath + "Day.zip"))
			{
				if (zip == null)
				{
					zip = new Unzip();
				}

				zip.unzipAnt(ConfigClass.LocalDBPath + "Day.zip", ConfigClass.LocalDBPath, 1);
			}
		}
		catch (Exception er)
		{
			new MessageBox(Language.apply("校验本地数据库出现异常\n\n") + er.getMessage());

			return false;
		}
		finally
		{
			zip = null;
		}

		return true;
	}

	/**
	 * 加载JSTORE数据库连接
	 * 
	 * @param lbl_message
	 * @return
	 */
	public boolean loadRemoteDB(Label lbl_message)
	{
		if (!ConfigClass.DataBaseEnable.equals("Y"))
			return true;

		try
		{
			Sqldb sqldb = new Sqldb();
			String strConn = "DataBaseDriver=[" + ConfigClass.DataBaseDriver + "],DataBaseUrl=[" + ConfigClass.DataBaseUrl + "],DataBaseUser=[" + ConfigClass.DataBaseUser + "],DataBasePwd=[" + ConfigClass.DataBasePwd + "]";
			System.out.println(strConn);
			if (!sqldb.startCreate(ConfigClass.DataBaseDriver, ConfigClass.DataBaseUrl, ConfigClass.DataBaseUser, ConfigClass.DataBasePwd, false))
			{
				// return false;
				System.out.println("JSTORE数据库连接失败.");
			}
			else
			{
				GlobalInfo.RemoteDB = sqldb;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println("JSTORE数据库连接异常:" + ex.getMessage());
			// new MessageBox("连接远程数据库出现异常\n\n" + ex.getMessage());

			// return false;
		}
		return true;// 不管成功与否,均返回成功,以免影响系统初始化及登录
	}

	public boolean loadLocalDB(Label lbl_message)
	{
		return loadLocalDB(lbl_message, 0);
	}

	public boolean loadLocalDB(Label lbl_message, int type)
	{
		Sqldb sql = null;
		ManipulateDateTime mdt = null;

		try
		{
			// Base
			if (type == 0 || type == 1)
			{
				sql = new Sqldb(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + getBaseDBName());

				// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
				if (sql.isOpen() && sql.selectOneData("select count(*) from goods where 1=2") == null)
				{
					sql.Close();
				}

				if (!sql.isOpen())
				{
					AccessDayDB.getDefault().writeWorkLog(Language.apply("开机时连接BASE数据库失败"), StatusType.WORK_SENDERROR);

					MessageBox me = new MessageBox(Language.apply("连接最新基础数据库失败,是否恢复上次数据库?\n\n任意键-是 / 退出键-否"), null, false);

					if (me.verify() != GlobalVar.Exit)
					{
						setLabelHint(lbl_message, Language.apply("正在恢复上次数据库,请等待......"));

						PathFile.deletePath(ConfigClass.LocalDBPath + getBaseDBName());
						if (PathFile.fileExist(ConfigClass.LocalDBPath + "Bak//" + getBaseDBName()))
						{
							PathFile.copyPath(ConfigClass.LocalDBPath + "Bak//" + getBaseDBName(), ConfigClass.LocalDBPath + getBaseDBName());
						}
						else
						{
							checkLocalDB(lbl_message);
						}

						setLabelHint(lbl_message, Language.apply("正在重新连接本地数据库,请等待......"));

						sql.startCreate(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + getBaseDBName());

						// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
						if (sql.isOpen() && sql.selectOneData("select count(*) from goods where 1=2") == null)
						{
							sql.Close();
						}

						if (!sql.isOpen())
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("开机恢复BASE数据库失败"), StatusType.WORK_SENDERROR);

							new MessageBox(Language.apply("恢复BASE数据库连接失败,请检查后重新启动..."), null, false);

							return false;
						}
						else
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("开机恢复BASE数据库成功"), StatusType.WORK_SENDERROR);

							GlobalInfo.baseDB = sql;
						}
					}
					else
					{
						return false;
					}
				}
				else
				{
					GlobalInfo.baseDB = sql;
				}
			}

			// Local
			if (type == 0 || type == 2)
			{
				sql = new Sqldb(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + getLocalDBName());

				// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
				if (sql.isOpen() && sql.selectOneData("select count(*) from syjmain where 1=2") == null)
				{
					sql.Close();
				}

				if (!sql.isOpen())
				{
					AccessDayDB.getDefault().writeWorkLog(Language.apply("开机时连接LOCAL数据库失败"), StatusType.WORK_SENDERROR);

					MessageBox me = new MessageBox(Language.apply("连接最新本地数据库失败,是否恢复上次数据库?\n\n任意键-是 / 退出键-否"), null, false);

					if (me.verify() != GlobalVar.Exit)
					{
						setLabelHint(lbl_message, Language.apply("正在恢复上次数据库,请等待......"));

						PathFile.deletePath(ConfigClass.LocalDBPath + getLocalDBName());
						if (PathFile.fileExist(ConfigClass.LocalDBPath + "Bak//" + getLocalDBName()))
						{
							PathFile.copyPath(ConfigClass.LocalDBPath + "Bak//" + getLocalDBName(), ConfigClass.LocalDBPath + getLocalDBName());
						}
						else
						{
							checkLocalDB(lbl_message);
						}

						setLabelHint(lbl_message, Language.apply("正在重新连接本地数据库,请等待......"));

						sql.startCreate(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + getLocalDBName());

						// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
						if (sql.isOpen() && sql.selectOneData("select count(*) from syjmain where 1=2") == null)
						{
							sql.Close();
						}

						if (!sql.isOpen())
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("开机恢复LOCAL数据库失败"), StatusType.WORK_SENDERROR);

							new MessageBox(Language.apply("恢复LOCAL数据库连接失败,请检查后重新启动..."), null, false);

							return false;
						}
						else
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("开机恢复LOCAL数据库成功"), StatusType.WORK_SENDERROR);

							GlobalInfo.localDB = sql;
						}
					}
					else
					{
						return false;
					}
				}
				else
				{
					GlobalInfo.localDB = sql;

					// 备份
					PathFile.deletePath(ConfigClass.LocalDBPath + "Bak//" + getLocalDBName());
					PathFile.copyPath(ConfigClass.LocalDBPath + getLocalDBName(), ConfigClass.LocalDBPath + "Bak//" + getLocalDBName());
				}
			}

			// Day,设置记账日期
			if (type == 0 || type == 3)
			{
				// 记账日期
				mdt = new ManipulateDateTime();
				GlobalInfo.balanceDate = mdt.getDateBySlash();

				// 判断通宵营业时间,确定当日本地库
				String newdate = getOverNight(true);
				if (newdate != null && !newdate.equals(""))
					GlobalInfo.balanceDate = newdate;

				// 连接每日库
				String date = ExpressionDeal.replace(GlobalInfo.balanceDate, "/", "");
				sql = new Sqldb(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName());

				// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
				if (sql.isOpen() && sql.selectOneData("select count(*) from salehead where 1=2") == null)
				{
					sql.Close();
				}

				if (!sql.isOpen())
				{
					MessageBox me = new MessageBox(Language.apply("连接每日数据库失败，是否恢复上次数据库?\n\n任意键-是 / 退出键-否"), null, false);

					if (me.verify() != GlobalVar.Exit)
					{
						setLabelHint(lbl_message, Language.apply("正在恢复上次数据库,请等待......"));

						// 先备份当前损坏的DAY数据库
						String errname = mdt.getTimeByEmpty() + "_Day";
						PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice//" + date + "//Err//" + errname);
						PathFile.copyPath(ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName(), ConfigClass.LocalDBPath + "Invoice//" + date + "//Err//" + errname);

						// 再恢复上次正常的DAY数据库
						PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName());
						if (PathFile.fileExist(ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + getDayDBName()))
						{
							PathFile.copyPath(ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + getDayDBName(), ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName());
						}
						else
						{
							PathFile.copyPath(ConfigClass.LocalDBPath + getDayDBName(), ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName());
						}

						setLabelHint(lbl_message, Language.apply("正在重新连接每日数据库,请等待......"));

						sql.startCreate(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName());

						// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
						if (sql.isOpen() && sql.selectOneData("select count(*) from salehead where 1=2") == null)
						{
							sql.Close();
						}

						if (!sql.isOpen())
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("开机恢复DAY数据库失败"), StatusType.WORK_SENDERROR);

							new MessageBox(Language.apply("恢复DAY数据库连接失败,请检查后重新启动..."), null, false);

							return false;
						}
						else
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("开机恢复DAY数据库成功"), StatusType.WORK_SENDERROR);

							GlobalInfo.dayDB = sql;

							// 导入未连接数据库之前的日志到数据库
							AccessDayDB.getDefault().writeWorkLogByHistory();

							// 标记需要恢复交易数据
							needimportsale = true;

							new MessageBox(Language.apply("恢复每日数据库成功\n\n请在登录以后进行销售导入,避免丢失销售数据!"));
						}
					}
					else
					{
						return false;
					}

					new MessageBox(Language.apply("连接day数据库完成"), null, false);
				}
				else
				{
					GlobalInfo.dayDB = sql;

					// 备份
					PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + getDayDBName());
					PathFile.copyPath(ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName(), ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + getDayDBName());

					// 导入未连接数据库之前的日志到数据库
					AccessDayDB.getDefault().writeWorkLogByHistory();
				}
			}

			// 创建商品查询预编译SQL对象,加快本地查询商品速度
			if (type == 0 || type == 1)
				AccessBaseDB.getDefault().createPreparedSql();
		}
		catch (Exception er)
		{
			er.printStackTrace();
			new MessageBox(Language.apply("连接本地数据库出现异常\n\n") + er.getMessage());

			return false;
		}
		finally
		{
			mdt = null;
			sql = null;
		}

		return true;
	}

	public String getOverNight(boolean read)
	{
		// 先读取上次配置
		String file = GlobalVar.ConfigPath + "/OverNightConfig.ini";
		String overNight = "";
		if (PathFile.fileExist(file))
		{
			BufferedReader br = null;
			try
			{
				br = CommonMethod.readFile(file);
				if (br != null)
				{
					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.length() <= 0)
						{
							continue;
						}
						overNight = line.trim();
						break;
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				try
				{
					if (br != null)
						br.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}

		// 写入参数
		if (!read)
		{
			String newOverNight = "";
			if (PathFile.fileExist(file))
				PathFile.deletePath(file);

			if (GlobalInfo.sysPara.overNightBegin != null && !GlobalInfo.sysPara.overNightBegin.equals("") && GlobalInfo.sysPara.overNightEnd != null && !GlobalInfo.sysPara.overNightEnd.equals(""))
			{
				newOverNight = GlobalInfo.sysPara.overNightBegin + "," + GlobalInfo.sysPara.overNightEnd;
			}
			else if (GlobalInfo.sysPara.overNightTime != null && !GlobalInfo.sysPara.overNightTime.equals(""))
			{
				newOverNight = GlobalInfo.sysPara.overNightTime;
			}

			// 设置了通宵营业参数
			if (newOverNight != null && !newOverNight.equals(""))
			{
				PrintWriter pw = null;
				try
				{
					pw = CommonMethod.writeFile(file);
					pw.write(newOverNight);
					pw.flush();
				}
				finally
				{
					if (pw != null)
						pw.close();
				}
			}

			// 如果本次设置和上次设置不同则提示用户进行重启参数才能生效
			if (!overNight.equals(newOverNight))
			{
				new MessageBox(Language.apply("系统改变了通宵营业时间设置,必须重新启动才能生效!"));
			}
		}
		else
		{
			try
			{
				// 2010/01/01|00:00:00,2010/01/02|00:00:00 （日期时间区间）
				if (overNight.length() >= 39 && overNight.indexOf(",") >= 19)
				{
					String[] s = overNight.split(",");
					String[] begin = s[0].split("\\|");
					String[] end = s[1].split("\\|");
					String dayBegin = begin[0].trim(); // 开始日期
					String timeBegin = begin[1].trim(); // 开始时间
					String dayEnd = end[0].trim(); // 结束日期
					String timeEnd = end[1].trim(); // 结束时间

					SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					String curDateTime = ManipulateDateTime.getCurrentDateTime();

					// 如果当前日期时间在区间之内，则记账到左区间
					if (df.parse(curDateTime).compareTo(df.parse(dayBegin + " " + timeBegin)) >= 0 && df.parse(curDateTime).compareTo(df.parse(dayEnd + " " + timeEnd)) <= 0) { return dayBegin; }
				}
				else
				// 02:30:00 （时间点）
				if (overNight.length() == 8)
				{
					// 如果当前系统时间在设置时间点之前，则记账到前一天
					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
					if (df.parse(ManipulateDateTime.getCurrentTime()).compareTo(df.parse(overNight)) <= 0)
					{
						df = new SimpleDateFormat("yyyy/MM/dd");
						Calendar c = Calendar.getInstance();
						c.add(Calendar.DAY_OF_MONTH, -1);

						return df.format(c.getTime());
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		return null;
	}

	public Sqldb loadDayDB(String day)
	{
		Sqldb sql = null;
		String s = null;

		s = day.replaceAll("/", "");
		s = s.replaceAll("-", "");

		String name = ConfigClass.LocalDBPath + "Invoice//" + s + "//" + getDayDBName();

		// 检查数据库文件是否存在
		if (!new File(name).exists()) { return null; }

		// 连接数据库
		sql = new Sqldb(getJdbcDriverName(), getJdbcConnurlName() + name);

		if (!sql.isOpen())
		{
			return null;
		}
		else
		{
			return sql;
		}
	}

	// 建立今日数据库
	public boolean createDayDB()
	{
		String name = null;
		ManipulateDateTime mdt = null;

		try
		{
			mdt = new ManipulateDateTime();
			name = ConfigClass.LocalDBPath + "Invoice//" + mdt.getDateByEmpty() + "//" + getDayDBName();

			if (!new File(name).exists())
			{
				PathFile.copyPath(ConfigClass.LocalDBPath + getDayDBName(), name);

				return true;
			}
			else
			{
				return true;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();

			new MessageBox(Language.apply("创建每日数据库失败，请检查后重新启动"), null, false);

			return false;
		}
		finally
		{
			name = null;
			mdt = null;
		}
	}

	public boolean checkLicence()
	{
		String key = ManipulatePrecision.getRegisterCodeKey(ConfigClass.CDKey);
		String code = "";
		String strseq = "";

		// 检查是否临时注册码
		if (ConfigClass.CDKey.length() >= 38)
		{
			String maxdate = "";
			int i = ConfigClass.CDKey.lastIndexOf('-');
			if (i >= 0)
				maxdate = ConfigClass.CDKey.substring(i + 1);
			int j = ConfigClass.CDKey.lastIndexOf('-', i - 1);
			if (j >= 0)
				strseq = ConfigClass.CDKey.substring(j + 1, i);
			code = ManipulatePrecision.getRegisterCode("", maxdate, strseq, key);
			if (code.equals(ConfigClass.CDKey.substring(0, i)))
			{
				// 生成临时有效期
				if (!maxdate.equals("00000000"))
				{
					StringBuffer sb = new StringBuffer();
					sb.append(maxdate.substring(0, 4));
					sb.append("-");
					sb.append(maxdate.substring(4, 6));
					sb.append("-");
					sb.append(maxdate.substring(6, 8));
					GlobalInfo.sysPara.validservicedate = ManipulatePrecision.EncodeString(sb.toString() + ",15", key);
				}
				return true;
			}
		}

		//
		int i = ConfigClass.CDKey.lastIndexOf('-');
		if (i >= 0)
		{
			strseq = ConfigClass.CDKey.substring(i + 1);
		}

		if (!key.equals(""))
		{
			code = ManipulatePrecision.getRegisterCode(GlobalInfo.sysPara.mktname, GlobalInfo.sysPara.mktcode, strseq, key);

			if (code.equals(ConfigClass.CDKey)) { return true; }

			if (checkLicenceByExtend(strseq, key)) { return true; }
		}

		//
		if (ConfigClass.DebugMode)
		{
			new MessageBox(Language.apply("注册资料是: ") + GlobalInfo.sysPara.mktname + " / " + GlobalInfo.sysPara.mktcode + Language.apply("\n\n注册码不正确,不能进入系统!"));
		}
		else
		{
			new MessageBox(Language.apply("注册码不正确,不能进入系统!"));

			PublicMethod.traceDebugLog(Language.apply("注册码不正确: ") + GlobalInfo.sysPara.mktname + " / " + GlobalInfo.sysPara.mktcode);
		}

		return false;
	}

	public boolean checkLicenceByExtend(String strseq, String key)
	{
		return false;
	}

	public boolean checkServiceDate()
	{
		String key = ManipulatePrecision.getRegisterCodeKey(ConfigClass.CDKey);
		String strdate = "";
		ManipulateDateTime dt = new ManipulateDateTime();
		String curdate = dt.getDateBySign();

		// 兼容已上线的项目没有定义有效期,自动生成90天有效期文件
		if (GlobalInfo.sysPara.validservicedate == null || GlobalInfo.sysPara.validservicedate.trim().length() <= 0)
		{
			PrintWriter pw = null;
			BufferedReader br = null;
			try
			{
				String name = ConfigClass.LocalDBPath + "/ServiceDate.dat";
				File indexFile = new File(name);
				if (!indexFile.exists())
				{
					strdate = ManipulatePrecision.EncodeString(dt.skipDate(curdate, 90).replace('/', '-') + ",15", key);
					pw = CommonMethod.writeFile(name);
					pw.println(strdate);
					pw.flush();
					pw.close();
					pw = null;
				}

				// 读取有效期
				br = CommonMethod.readFile(name);
				String line = null;
				while ((line = br.readLine()) != null)
				{
					if (line.length() <= 0)
					{
						continue;
					}
					else
					{
						GlobalInfo.sysPara.validservicedate = line.trim();
						break;
					}
				}
				br.close();
				br = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				new MessageBox(Language.apply("系统有效期数据不正确,不能进入系统!"));
				return false;
			}
			finally
			{
				try
				{
					if (pw != null)
						pw.close();
					if (br != null)
						br.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		// 解密有效期
		strdate = ManipulatePrecision.DecodeString(GlobalInfo.sysPara.validservicedate, key);
		String[] s = strdate.split(",");
		if (s.length < 2 || !ManipulateDateTime.checkDate(s[0]) || Convert.toInt(s[1]) <= 0)
		{
			new MessageBox(Language.apply("系统有效期格式不正确,不能进入系统!"));
			return false;
		}
		if (dt.compareDate(curdate, s[0]) > 0)
		{
//			new MessageBox("本系统有效期为 " + s[0] + "\n目前已超过使用有效期,不能进入系统!\n\n请马上联系富基公司延长使用有效期!");
			new MessageBox(Language.apply("本系统有效期为 {0} \n目前已超过使用有效期,不能进入系统!\n\n请马上联系富基公司延长使用有效期!" ,new Object[]{s[0]}));
			return false;
		}
		if (s.length > 2 && !checkRegisterCount(Convert.toInt(s[2]))) // 检查款机注册数量
		{ return false; }
		long n = dt.compareDate(curdate, dt.skipDate(s[0], Convert.toInt(s[1]) * -1));
		if (n >= 0)
		{
//			new MessageBox("本系统还有 " + (Convert.toInt(s[1]) - n) + " 天就过期了\n\n请尽快联系富基公司延长使用有效期!");
			new MessageBox(Language.apply("本系统还有 {0} 天就过期了\n\n请尽快联系富基公司延长使用有效期!" ,new Object[]{(Convert.toInt(s[1]) - n)+""}));
		}

		return true;
	}

	public boolean checkRegisterCount(int regcount)
	{
		Vector v = NetService.getDefault().checkRegisterCount(GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode, ConfigClass.CDKey);
		if (v == null)
		{
			new MessageBox(Language.apply("检查款机注册数量失败,不能进入系统!"));
			return false;
		}
		if (v.size() > regcount)
		{
//			new MessageBox("本系统款机有效注册数量为 " + regcount + " 个\n\n目前系统内注册款机数量为 " + v.size() + " 个,暂时不能进入系统\n\n请马上联系富基公司增加款机注册数量!");
			new MessageBox(Language.apply("本系统款机有效注册数量为 {0} 个\n\n目前系统内注册款机数量为 {1} 个,暂时不能进入系统\n\n请马上联系富基公司增加款机注册数量!" ,new Object[]{regcount+"" ,v.size()+""}));
			return false;
		}
		boolean find = false;
		for (int i = 0; i < v.size(); i++)
		{
			String[] s = (String[]) v.elementAt(i);
			if (s.length < 2)
				continue;
			if (!ConfigClass.CashRegisterCode.equals(s[0]) && ConfigClass.CDKey.equals(s[1]))
			{
				new MessageBox(Language.apply("本收银机的许可证与其他款机冲突!\n\n不能进入系统!"));
				return false;
			}
			if (ConfigClass.CashRegisterCode.equals(s[0]) && ConfigClass.CDKey.equals(s[1]))
			{
				find = true;
			}
		}
		if (!find)
		{
			new MessageBox(Language.apply("本收银机的许可证注册失败!\n\n不能进入系统!"));
			return false;
		}
		return true;
	}

	public boolean checkVersion()
	{
		// 脱网或者无版本控制时不检查
		if (!GlobalInfo.isOnline || (GlobalInfo.sysPara.validver.trim().length() <= 0)) { return true; }

		String[] s = GlobalInfo.sysPara.validver.trim().split(",");

		if ((s.length >= 1) && !s[0].trim().equalsIgnoreCase(AssemblyInfo.AssemblyVersion.trim()))
		{
//			new MessageBox("当前系统版本号为 " + AssemblyInfo.AssemblyVersion.trim() + "\n系统版本号要求是 " + s[0].trim() + "\n\n系统版本号不正确,不能进入系统!");
			new MessageBox(Language.apply("当前系统版本号为 {0} \n系统版本号要求是 {1}\n\n系统版本号不正确,不能进入系统!" ,new Object[]{AssemblyInfo.AssemblyVersion.trim() ,s[0].trim()}));

			return false;
		}

		if ((s.length >= 2) && !s[1].trim().equalsIgnoreCase(CustomLocalize.getDefault().getAssemblyVersion().trim()))
		{
//			new MessageBox("当前客户化版本为 " + CustomLocalize.getDefault().getAssemblyVersion().trim() + "\n客户化版本要求是 " + s[1].trim() + "\n\n客户化版本不正确,不能进入系统!");
			new MessageBox(Language.apply("当前客户化版本为 {0} \n客户化版本要求是 {1}\n\n客户化版本不正确,不能进入系统!" ,new Object[]{CustomLocalize.getDefault().getAssemblyVersion().trim() ,s[1].trim()}));

			return false;
		}

		return true;
	}

	public boolean ShowTaskList()
	{
		TasksDef task = new TasksDef();
		ResultSet rs = null;
		String[] title = { Language.apply("代码"), Language.apply("类型"), Language.apply("关键字") };
		int[] width = { 60, 200, 250 };
		Vector contents = new Vector();

		try
		{
			rs = GlobalInfo.localDB.selectData("select * from TASKS order by seqno");

			while ((rs != null) && rs.next())
			{
				if (GlobalInfo.localDB.getResultSetToObject(task))
				{
					contents.add(new String[] { String.valueOf(task.type), StatusType.taskTypeExchange(task.type), task.keytext });
				}
			}

			GlobalInfo.localDB.resultSetClose();

			// 显示
			new MutiSelectForm().open(Language.apply("以下任务未能执行,请通知电脑部"), title, width, contents);

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();
		}
	}

	// 关机载入收银信息
	public boolean quitLoadInfo(Label lbl_message)
	{
		//
		setLabelHint(lbl_message, Language.apply("正在安全退出系统......"));

		//
		String msg = "";
		while (true)
		{
			// 尝试重新连接网络
			if (!GlobalInfo.isOnline)
			{
				setLabelHint(lbl_message, Language.apply("正在尝试连接网络......"));
				DataService.getDefault().getServerTime(false);

				// 刷新状态栏网络状态
				GlobalInfo.statusBar.setNetStatus();
			}

			// 执行历史任务
			try
			{
				setLabelHint(lbl_message, Language.apply("正在执行未完成任务......"));
				DataService.getDefault().execHistoryTask();
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			// 检查网上数据
			setLabelHint(lbl_message, Language.apply("正在检查当天送网数据......"));
			InvoiceInfoDef inv = new InvoiceInfoDef();
			inv.bs = 0;
			inv.je = 0;

			// 如果数据只发往远端数据库，那么不进行关机数据校验
			/*
			 * if (ConfigClass.DataBaseEnable.equals("Y")) { inv.bs = 0; inv.je
			 * = GlobalInfo.syjStatus.je; } else { if(GlobalInfo.isOnline &&
			 * NetService.getDefault().getInvoiceInfo(inv)) inv.bs =
			 * GlobalInfo.syjStatus.bs - inv.bs; }
			 */
			if (GlobalInfo.isOnline && NetService.getDefault().getInvoiceInfo(inv))
				inv.bs = GlobalInfo.syjStatus.bs - inv.bs;

			// 设置退出提示
			boolean isCall = true;
			boolean msgclear = false;

			if (!GlobalInfo.isOnline)
			{
				// 记录关机工作日志
				AccessDayDB.getDefault().writeWorkLog(Language.apply("关机时连接网络失败,可能会有数据未送网"), StatusType.WORK_SENDERROR);

				msg = Language.apply("连接网络失败,可能会有数据未送网\n\n请通知电脑部进行检查!");
			}
			else
			{
				if (inv.bs > 0)
				{
					// 记录日志
//					AccessDayDB.getDefault().writeWorkLog("关机时发现有 " + String.valueOf(inv.bs) + " 笔交易未发送", StatusType.WORK_SENDERROR);
					AccessDayDB.getDefault().writeWorkLog(Language.apply("关机时发现有 {0} 笔交易未发送" ,new Object[]{String.valueOf(inv.bs)}), StatusType.WORK_SENDERROR);

					//
					msg = String.valueOf(inv.bs) + Language.apply(" 笔交易未发送,请通知电脑部!");
				}
				else if (ManipulatePrecision.doubleCompare(GlobalInfo.syjStatus.je, inv.je, 2) != 0)
				{
					// 记录日志
					AccessDayDB.getDefault().writeWorkLog(Language.apply("关机时交易金额不平,本地:") + String.valueOf(GlobalInfo.syjStatus.je) + Language.apply(",网上:") + String.valueOf(inv.je), StatusType.WORK_SENDERROR);

					//
					msg = Language.apply("交易金额不平,请通知电脑部!\n\n本地金额:{0}", new Object[]{ManipulatePrecision.doubleToString(GlobalInfo.syjStatus.je, 2, 1, false, 12)}) + Language.apply("\n网上金额:") + ManipulatePrecision.doubleToString(inv.je, 2, 1, false, 12);
				}
				else
				{
					Object obj = GlobalInfo.localDB.selectOneData("select count(*) from TASKS");

					if ((obj != null) && (Integer.parseInt(String.valueOf(obj)) > 0))
					{
						// 记录日志
						AccessDayDB.getDefault().writeWorkLog(Language.apply("关机时有任务未执行,请进行检查"), StatusType.WORK_SENDERROR);

						msg = Language.apply("有任务未执行,请通知电脑部!");

						// 显示任务清单
						msgclear = ShowTaskList();
					}
					else
					{
						msg = "";
						isCall = false;
					}
				}
			}

			// 关机有错误提示或者断网，则进行重试
			if (isCall || !GlobalInfo.isOnline)
			{
				String tips;
				if (!GlobalInfo.isOnline)
				{
					tips = Language.apply("连接网络失败,可能会有数据未送网\n\n是否重新连接网络？\n\n任意键-重试 / 2-放弃 ");
				}
				else
				{
					tips = msg + Language.apply("\n\n可能有数据未送网,是否重新连接网络？\n\n任意键-重试 / 2-放弃 ");
				}

				int ret = new MessageBox(tips, null, false).verify();
				if (ret != GlobalVar.Key2)
				{
					continue;
				}
				else
				{
					AccessDayDB.getDefault().writeWorkLog(Language.apply("关机时可能有数据未送网,收银员放弃重试"), StatusType.WORK_SENDERROR);
				}
			}

			// 关机有错误提示，发送呼叫到后台
			if (isCall && GlobalInfo.isOnline)
			{
				setLabelHint(lbl_message, Language.apply("正在发送关机异常呼叫信息......"));

				CallInfoDef info = new CallInfoDef();
				info.code = "00";
				info.text = msg;
				NetService.getDefault().sendCallInfo(info);
			}

			// 已显示了未完成任务清单，不再进行提示
			if (msgclear)
				msg = null;

			// 跳出循环
			break;
		}

		// 备份有效的DAY库
		if (GlobalInfo.dayDB != null)
		{
			setLabelHint(lbl_message, Language.apply("正在备份DAY每日数据库......"));

			String date = GlobalInfo.balanceDate.replaceAll("/", "");
			PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + getDayDBName());
			PathFile.copyPath(ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName(), ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + getDayDBName());
		}

		// 清除进度提示
		setLabelHint(lbl_message, "");

		// 退出系统
		ExitSystem(msg);

		return true;
	}

	public void checkExceptionShutdown(boolean check)
	{
		String chkfile = "bootposstatus";

		if (!check)
		{
			// 删除开机标记文件,表示是正常关机
			if (PathFile.fileExist(chkfile))
				PathFile.deletePath(chkfile);
		}
		else
		{
			// 开机标记文件未删除，可能是非正常关机
			if (PathFile.fileExist(chkfile))
			{
				new MessageBox(Language.apply("上次系统被非正常退出，可能是直接关闭了款机电源\n\n请通过系统菜单功能安全退出系统\n\n非正常退出系统易引起文件系统损坏"));
				AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机曾经被非正常关闭"), StatusType.WORK_SENDERROR);
			}

			// 创建开机标记文件
			try
			{
				new File(chkfile).createNewFile();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void ExitSystem()
	{
		ExitSystem(null);
		// LoadPlugin.getDefault().loadModule(false);
	}

	public void ExitSystem(String msg)
	{
		try
		{
			// 刷新界面交互
			while (Display.getCurrent().readAndDispatch());

			// 检查正在更新本地数据库,如果在更新则等待更新完毕才退出系统
			UpdateBaseInfo.waitUpdateBase();

			// 停止后台工作
			if (GlobalInfo.backgroundTimer != null)
			{
				GlobalInfo.backgroundTimer.cancel();
			}

			// 发送关机状态
			if (GlobalInfo.syjStatus != null)
			{
				GlobalInfo.syjStatus.status = StatusType.STATUS_SHUTDOWN;
				AccessLocalDB.getDefault().writeSyjStatus();
				DataService.getDefault().sendSyjStatus();
			}

			// 记录关机工作日志
			if (GlobalInfo.isOnline)
			{
				AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机安全关机"), StatusType.WORK_SHUTDOWN);
				TaskExecute.getDefault().sendAllWorkLog(TaskExecute.getKeyTextByBalanceDate());
			}
			else
			{
				AccessDayDB.getDefault().writeWorkLog(Language.apply("收银机脱网关机,请检查"), StatusType.WORK_SHUTDOWN);
			}

			// 释放设备资源
			releaseDevice();

			// 关闭HTTP连接
			if (GlobalInfo.localHttp != null)
			{
				GlobalInfo.localHttp.disconncet();
			}

			if (GlobalInfo.timeHttp != null)
			{
				GlobalInfo.timeHttp.disconncet();
			}

			if (GlobalInfo.cardHttp != null)
			{
				GlobalInfo.cardHttp.disconncet();
			}

			if (GlobalInfo.memcardHttp != null)
			{
				GlobalInfo.memcardHttp.disconncet();
			}

			// 关闭本地数据库
			if (GlobalInfo.baseDB != null)
			{
				GlobalInfo.baseDB.Close();
			}

			if (GlobalInfo.localDB != null)
			{
				GlobalInfo.localDB.Close();
			}

			if (GlobalInfo.dayDB != null)
			{
				GlobalInfo.dayDB.Close();
			}

			// 删除开机标记文件,表示是正常关机
			checkExceptionShutdown(false);
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("最外层异常:") + ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			// 有错误提示先提示
			if ((msg != null) && (msg.trim().length() > 0))
			{
				new MessageBox(msg, null, false);
			}

			// 未登录或非维护人员不能返回命令行状态
			if (!rerunflag && (GlobalInfo.posLogin == null || (GlobalInfo.posLogin != null && GlobalInfo.posLogin.type != '3')))
			{
				boolean quit = ConfigClass.DebugMode;

				// 专卖下不闭款机，若要关闭，可将DebugModeString=S
				if (ConfigClass.DebugModeString.equals("Z"))
					quit = true;

				// 未读取到款机定义,则可用缺省维护密码退到命令行
				if (GlobalInfo.syjDef == null)
				{
					GlobalInfo.sysPara = new GlobalParaDef();
					GlobalInfo.sysPara.quitpwd = "9999";
					GlobalInfo.sysPara.ischoiceExit = 'Y';
				}

				// 检查退出密码
				if (!quit)
				{
					if ((GlobalInfo.sysPara != null) && (GlobalInfo.sysPara.quitpwd.trim().length() > 0))
					{
						StringBuffer buffer = new StringBuffer();

						if (new TextBox().open(Language.apply("请输入维护员退出密码"), "PASSWORD", Language.apply("输入正确的维护密码可退出到操作系统"), buffer, 0, 0, false, TextBox.AllInput) && buffer.toString().trim().equals(GlobalInfo.sysPara.quitpwd.trim()))
						{
							quit = true;
						}
					}
				}

				// 非维护关闭电源
				if (!quit)
				{
					int i = 0;
					ProgressBox pb = new ProgressBox();

					if ((GlobalInfo.sysPara != null) && GlobalInfo.sysPara.ischoiceExit != 'Y')
					{
						pb.setText(Language.apply("系统将自动关机,请稍后关闭电源..."));
						while (true)
						{
							// 提示关闭电源
							// new MessageBox("请按'回车'键,系统将自动关机,稍后关闭电源!", null,
							// false);

							// 调用关闭电源指令
							if (i <= 0)
							{
								haltSystem();
							}

							// 关闭电源指令只调用一次
							i++;
						}
					}
					else
					{
						int choice = 0;
						StringBuffer info = new StringBuffer();
						info.append(Convert.appendStringSize("", Language.apply("请选择操作"), 1, 30, 30, 2) + "\n\n");
						info.append(Convert.appendStringSize("", Language.apply("1、关闭计算机"), 1, 30, 30, 2) + "\n");
						info.append(Convert.appendStringSize("", Language.apply("2、重启计算机"), 1, 30, 30, 2) + "\n");

						while (choice != GlobalVar.Key1 && choice != GlobalVar.Key2)
						{
							choice = new MessageBox(info.toString(), null, false).verify();
						}

						if (choice == GlobalVar.Key1)
						{
							pb.setText(Language.apply("系统将关闭,请稍后..."));
							while (true)
							{
								// 调用关闭电源指令
								if (i <= 0)
								{
									haltSystem();
								}

								// 关闭电源指令只调用一次
								i++;
							}
						}

						if (choice == GlobalVar.Key2)
						{
							pb.setText(Language.apply("系统将重启,请稍后..."));
							while (true)
							{
								// 调用重启指令
								if (i <= 0)
								{
									restartSystem();
								}

								// 关闭电源指令只调用一次
								i++;
							}
						}
					}
				}
			}

			//
			if (GlobalInfo.ModuleType.indexOf("ZM")!=0) new MessageBox(Language.apply("系统已退出,将返回命令行状态!"), null, false);

			// 最后释放键盘设备
			if (KeyBoard.getDefault() != null)
			{
				KeyBoard.getDefault().close();
			}

			// 关闭双屏
			if (SecMonitor.secMonitor != null)
				SecMonitor.secMonitor.monitorClose();

			// 开启WINDOWS任务栏
			GlobalVar.EnableWindowsTrayWnd(true);

			// 关闭屏幕键盘
			if (Math.abs(ConfigClass.ScreenKeyboard) > 0)
				GlobalVar.EnableScreenKeyboard(false);

			// 释放SWT资源&退出系统
			if (rerunflag)
			{
				// 重启软件
				reRunning();
			}
			else
			{
				SWTResourceManager.dispose();
				System.exit(0);
			}
		}
	}

	// 重启系统
	public void restartSystem()
	{
		try
		{
			if (ConfigClass.RebootCmd != null && ConfigClass.RebootCmd.trim().length() > 0)
			{
				CommonMethod.waitForExec(ConfigClass.RebootCmd, false);
			}
			else if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
			{
				CommonMethod.waitForExec("reboot -p", false);
			}
			else
			{
				String os = System.getProperties().getProperty("os.name");

				// WIN2000 关机命令
				// %Windir%\RunDLL32.exe
				// %Windir%\System\Shell32.dll,SHExitWindowsEx 8
				if ((os.indexOf("XP") >= 0) || (os.indexOf("2000") >= 0) || (os.indexOf("NT") >= 0))
				{
					CommonMethod.waitForExec("shutdown -r -f -t 3", false);
				}
				else
				{
					// WIN98/WINME
					// rundll32.exe shell32.dll,SHExitWindowsEx 1
					CommonMethod.waitForExec("rundll32.exe shell32.dll,SHExitWindowsEx 2", false);
				}
			}

			// 退出系统
			SWTResourceManager.dispose();
			System.exit(0);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void haltSystem()
	{
		try
		{
			if (ConfigClass.HaltCmd != null && ConfigClass.HaltCmd.trim().length() > 0)
			{
				CommonMethod.waitForExec(ConfigClass.HaltCmd, false);
			}
			else if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
			{
				CommonMethod.waitForExec("halt -p", false);
			}
			else
			{
				String os = System.getProperties().getProperty("os.name");

				// WIN2000 关机命令
				// %Windir%\RunDLL32.exe
				// %Windir%\System\Shell32.dll,SHExitWindowsEx 8
				if ((os.indexOf("XP") >= 0) || (os.indexOf("2000") >= 0) || (os.indexOf("NT") >= 0)|| (os.indexOf("Vista")>=0))
				{
					CommonMethod.waitForExec("shutdown -s -f -t 3", false);
				}
				else 
				{
					// WIN98/WINME
					// rundll32.exe shell32.dll,SHExitWindowsEx 1
					CommonMethod.waitForExec("rundll32.exe user.exe,exitwindows", false);
					
				}
			}

			// 退出系统
			SWTResourceManager.dispose();
			System.exit(0);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void reRunning()
	{
		try
		{
			if (ConfigClass.FastrunCmd != null && ConfigClass.FastrunCmd.trim().length() > 0)
			{
				CommonMethod.waitForExec(ConfigClass.FastrunCmd, false);
			}
			else if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
			{
				CommonMethod.waitForExec("./startpos.sh", false);
			}
			else
			{
				CommonMethod.waitForExec("./startpos.exe", false);
			}

			// 退出系统
			SWTResourceManager.dispose();
			System.exit(0);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void setFastRunning(boolean flag)
	{
		rerunflag = flag;
	}

	public void releaseDevice()
	{
		if (CashBox.getDefault() != null)
		{
			CashBox.getDefault().close();
		}

		if (LineDisplay.getDefault() != null)
		{
			LineDisplay.getDefault().close();
		}

		if (MSR.getDefault() != null)
		{
			MSR.getDefault().close();
		}

		if (Printer.getDefault() != null)
		{
			Printer.getDefault().close();
		}

		if (Scanner.getDefault() != null)
		{
			Scanner.getDefault().close();
		}

		if (ElectronicScale.getDefault() != null)
		{
			ElectronicScale.getDefault().close();
		}
	}

	public boolean initRdPlugins()
	{
		try
		{
			if (ConfigClass.Plugins1 != null && ConfigClass.Plugins1.length() > 0)
				RdPlugins.getDefault().loadPlugins1(ConfigClass.Plugins1);
			
			if (ConfigClass.Plugins2 != null && ConfigClass.Plugins2.length() > 0)
				RdPlugins.getDefault().loadPlugins2(ConfigClass.Plugins2);
			
			if (ConfigClass.Plugins3 != null && ConfigClass.Plugins3.length() > 0)
				RdPlugins.getDefault().loadPlugins3(ConfigClass.Plugins3);
			
			if (ConfigClass.Plugins4 != null && ConfigClass.Plugins4.length() > 0)
				RdPlugins.getDefault().loadPlugins4(ConfigClass.Plugins4);
			
			if (ConfigClass.Plugins5 != null && ConfigClass.Plugins5.length() > 0)
				RdPlugins.getDefault().loadPlugins5(ConfigClass.Plugins5);

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean initDevice(Label lbl_message)
	{
		try
		{
			// 读取设配逻辑名
			new DeviceName();

			//
			setLabelHint(lbl_message, Language.apply("正在初始化键盘设备......"));

			if (KeyBoard.getDefault().isValid())
			{
				if (!KeyBoard.getDefault().open())
				{
					new MessageBox(Language.apply("专业键盘设备初始化失败!\n") + ConfigClass.KeyBoard1 + " " + DeviceName.deviceKeyBoard);
				}
				else
				{
					// 开机即启用键盘设备
					KeyBoard.getDefault().setEnable(true);
				}
			}

			//
			setLabelHint(lbl_message, Language.apply("正在初始化打印设备......"));

			if (Printer.getDefault().isValid())
			{
				while (true)
				{
					if (!Printer.getDefault().open())
					{
						if (new MessageBox(Language.apply("打印机设备初始化失败! \n 是否尝试重新初始化设备?"), null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						// 开机即启用打印设备
						Printer.getDefault().setEnable(true);

						break;
					}
				}
			}

			//
			setLabelHint(lbl_message, Language.apply("正在初始化刷卡设备......"));

			if (MSR.getDefault().isValid())
			{
				while (true)
				{
					if (!MSR.getDefault().open())
					{
						if (new MessageBox(Language.apply("刷卡设备初始化失败! \n 是否尝试重新初始化设备?"), null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						// 开机即启用刷卡设备
						MSR.getDefault().setEnable(true);

						break;
					}
				}
			}

			//
			setLabelHint(lbl_message, Language.apply("正在初始化客显设备......"));

			if (LineDisplay.getDefault().isValid())
			{
				while (true)
				{
					if (!LineDisplay.getDefault().open())
					{
						if (new MessageBox(Language.apply("客显设备初始化失败! \n 是否尝试重新初始化设备?"), null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						// 开机即启用客显设备
						LineDisplay.getDefault().setEnable(true);

						break;
					}
				}
			}

			//
			setLabelHint(lbl_message, Language.apply("正在初始化钱箱设备......"));

			if (CashBox.getDefault().isValid())
			{
				while (true)
				{
					if (!CashBox.getDefault().open())
					{
						if (new MessageBox(Language.apply("钱箱设备初始化失败! \n 是否尝试重新初始化设备?"), null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						// 开机即启用钱箱设备
						CashBox.getDefault().setEnable(true);

						break;
					}
				}
			}

			//
			setLabelHint(lbl_message, Language.apply("正在初始化扫描设备......"));

			if (Scanner.getDefault().isValid())
			{
				while (true)
				{
					if (!Scanner.getDefault().open())
					{
						if (new MessageBox(Language.apply("扫描设备初始化失败!\n 是否尝试重新初始化设备?"), null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						// 开机即启用串口设备
						Scanner.getDefault().setEnable(true);

						break;
					}
				}
			}

			setLabelHint(lbl_message, Language.apply("正在初始化电子秤设备......"));
			if (ElectronicScale.getDefault().isValid())
			{
				while (true)
				{
					if (!ElectronicScale.getDefault().open())
					{
						if (new MessageBox(Language.apply("电子秤设备初始化失败!\n 是否尝试重新初始化设备?"), null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						ElectronicScale.getDefault().setEnable(true);

						break;
					}
				}
			}

			setLabelHint(lbl_message, Language.apply("正在初始化第三方刷卡设备......"));
			if (BankTracker.getDefault().isValid())
			{
				while (true)
				{
					if (!BankTracker.getDefault().open())
					{
						if (new MessageBox(Language.apply("第三方刷卡设备始化失败!\n 是否尝试重新初始化设备?"), null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						BankTracker.getDefault().setEnable(true);

						break;
					}
				}
			}

			setLabelHint(lbl_message, Language.apply("正在读取复式IC设备配置......"));
			if ("device.ICCard.Muti_ICCard".equals(ConfigClass.ICCard1))
			{
				if (!Muti_ICCard.getMutiICInfo())
				{
					new MessageBox(Language.apply("复式IC卡设备配置错误，将无法使用"));
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			new MessageBox(er.getMessage());

		}

		return true;
	}

	public void checkfreeSpace(Label lbl_message)
	{
		try
		{
			if (GlobalInfo.syjDef.datatime <= 0) { return; }

			File file = new File(ConfigClass.LocalDBPath);

			if ((DiskSpace.os_freesize(file.getAbsoluteFile().getAbsolutePath())) <= GlobalInfo.syjDef.dataspace)
			{
				ManipulateDateTime mdt = new ManipulateDateTime();
				String date = mdt.skipDate(mdt.getDateBySlash(), GlobalInfo.syjDef.datatime * (-1));
				date = date.replaceAll("/", "");

				//
//				setLabelHint(lbl_message, "正在删除 " + GlobalInfo.syjDef.datatime + " 天之前的本地数据");
				setLabelHint(lbl_message, Language.apply("正在删除 {0} 天之前的本地数据" ,new Object[]{GlobalInfo.syjDef.datatime+""}));
//				AccessDayDB.getDefault().writeWorkLog("执行删除 " + GlobalInfo.syjDef.datatime + " 天之前的本地数据", StatusType.WORK_FULLYSPACE);
				AccessDayDB.getDefault().writeWorkLog(Language.apply("执行删除 {0} 天之前的本地数据",new Object[]{GlobalInfo.syjDef.datatime+""}), StatusType.WORK_FULLYSPACE);

				//
				RemoveDayBS.remvoeDataBasePath(date);
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
	}
}
