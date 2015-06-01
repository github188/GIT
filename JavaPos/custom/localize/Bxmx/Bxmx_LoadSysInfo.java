package custom.localize.Bxmx;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.InvoiceInfoDef;

public class Bxmx_LoadSysInfo extends LoadSysInfo
{
	public boolean quitLoadInfo(Label lbl_message)
	{
		setLabelHint(lbl_message, "正在安全退出系统......");

		//
		String msg = "";
		while (true)
		{
			// 尝试重新连接网络
			if (!GlobalInfo.isOnline)
			{
				setLabelHint(lbl_message, "正在尝试连接网络......");
				DataService.getDefault().getServerTime(false);

				// 刷新状态栏网络状态
				GlobalInfo.statusBar.setNetStatus();
			}

			// 执行历史任务
			try
			{
				setLabelHint(lbl_message, "正在执行未完成任务......");
				DataService.getDefault().execHistoryTask();
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			// 检查网上数据
			setLabelHint(lbl_message, "正在检查当天送网数据......");
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
				AccessDayDB.getDefault().writeWorkLog("关机时连接网络失败,可能会有数据未送网", StatusType.WORK_SENDERROR);

				msg = "连接网络失败,可能会有数据未送网\n\n请通知电脑部进行检查!";
			}
			else
			{
				if (inv.bs > 0)
				{
					// 记录日志
					AccessDayDB.getDefault().writeWorkLog("关机时发现有 " + String.valueOf(inv.bs) + " 笔交易未发送", StatusType.WORK_SENDERROR);

					//
					// msg = String.valueOf(inv.bs) + " 笔交易未发送,请通知电脑部!";
				}
				else if (ManipulatePrecision.doubleCompare(GlobalInfo.syjStatus.je, inv.je, 2) != 0)
				{
					// 记录日志
					AccessDayDB.getDefault().writeWorkLog("关机时交易金额不平,本地:" + String.valueOf(GlobalInfo.syjStatus.je) + ",网上:" + String.valueOf(inv.je), StatusType.WORK_SENDERROR);

					// msg = "交易金额不平,请通知电脑部!\n\n本地金额: " +
					// ManipulatePrecision.doubleToString(GlobalInfo.syjStatus.je,
					// 2, 1, false, 12) + "\n网上金额: " +
					// ManipulatePrecision.doubleToString(inv.je, 2, 1, false,
					// 12);
				}
				else
				{
					Object obj = GlobalInfo.localDB.selectOneData("select count(*) from TASKS");

					if ((obj != null) && (Integer.parseInt(String.valueOf(obj)) > 0))
					{
						// 记录日志
						AccessDayDB.getDefault().writeWorkLog("关机时有任务未执行,请进行检查", StatusType.WORK_SENDERROR);

						// msg = "有任务未执行,请通知电脑部!";

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
				AccessDayDB.getDefault().writeWorkLog("关机时可能有数据未送网,收银员放弃重试", StatusType.WORK_SENDERROR);

/*				String tips;
				if (!GlobalInfo.isOnline)
				{
					tips = "连接网络失败,可能会有数据未送网\n\n是否重新连接网络？\n\n任意键-重试 / 2-放弃 ";
				}
				else
				{
					tips = msg + "可能有数据未送网,是否重新连接网络？\n\n任意键-重试 / 2-放弃 ";
				}

				int ret = new MessageBox(tips, null, false).verify();
				if (ret != GlobalVar.Key2)
				{
					continue;
				}
				else
				{
					
				}*/
			}

/*			// 关机有错误提示，发送呼叫到后台
			if (isCall && GlobalInfo.isOnline)
			{
				setLabelHint(lbl_message, "正在发送关机异常呼叫信息......");

				CallInfoDef info = new CallInfoDef();
				info.code = "00";
				info.text = msg;
				NetService.getDefault().sendCallInfo(info);
			}
*/
			// 已显示了未完成任务清单，不再进行提示
			if (msgclear)
				msg = null;

			// 跳出循环
			break;
		}

		// 备份有效的DAY库
		if (GlobalInfo.dayDB != null)
		{
			setLabelHint(lbl_message, "正在备份DAY每日数据库......");

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
}
