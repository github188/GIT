package custom.localize.Cbbh;


import org.eclipse.swt.widgets.Label;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.RemoveDayBS;
import com.efuture.javaPos.Logic.ShortcutKeyBS;
import com.efuture.javaPos.UI.Design.ImportSmallTicketBackupForm;

import custom.localize.Bcrm.Bcrm_LoadSysInfo;

public class Cbbh_LoadSysInfo extends Bcrm_LoadSysInfo {

	
//	 开机载入收银信息
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
		Cbbh_DownloadData.downloadBaseDB(lbl_message);

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
	
}
