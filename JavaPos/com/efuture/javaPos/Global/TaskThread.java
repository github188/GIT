package com.efuture.javaPos.Global;

import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.NetService;

public class TaskThread extends TimerTask
{
	private boolean oldnet, curnet;

	public TaskThread()
	{
	}

	private void createHttpConnection()
	{
		if (GlobalInfo.timeHttp == null)
		{
			GlobalInfo.timeHttp = new Http(ConfigClass.ServerIP, ConfigClass.ServerPort, ConfigClass.ServerPath);
			GlobalInfo.timeHttp.init();
			GlobalInfo.timeHttp.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
			GlobalInfo.timeHttp.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
		}
	}

	public void run()
	{
		// PublicMethod.DEBUG_MSG("start time   " + new
		// ManipulateDateTime().getTime());

		try
		{
			// 关机状态定时器不工作
			if (GlobalInfo.syjStatus.status == StatusType.STATUS_SHUTDOWN)
				return;

			// SWT界面线程
			Display display = Display.getDefault();

			// 创建HTTP对象
			createHttpConnection();

			// 保存原网络状态
			oldnet = GlobalInfo.isOnline;
			curnet = GlobalInfo.isOnline;

			// 获取网络时间的同时检查网络是否连通,主动脱网不自动重联,只在收到命令联网时重新连接网络
			if (GlobalInfo.syjStatus.netstatus != 'Z' && GlobalInfo.sysPara.isconnect == 'Y')
			{
				TimeDate time = new TimeDate();
				if (NetService.getDefault().getServerTime(GlobalInfo.timeHttp, time))
				{
					ManipulateDateTime mdt = new ManipulateDateTime();

					// 设置本机时间
					mdt.setDateTime(time);

					//
					GlobalInfo.isOnline = true;
					curnet = true;
				}
				else
				{
					// 定时线程不自动设置脱网状态,总是保持在假联网状态,只有通过主线程访问网络后才切换脱网
					// 避免在网络不稳定的情况下,进行操作时已切换到脱网(而在此时实际上网络已连上,但由于定时器未到达工作时间没能联网)
					// 但允许在脱网状态时自动检测网络连通后自动切换到联网状态
					// GlobalInfo.isOnline = false;
					curnet = false;

					// 重新生成Http对象，创建新的session
					GlobalInfo.timeHttp = null;
					createHttpConnection();
				}
			}

			// 发送收银机状态
			if (GlobalInfo.isOnline && curnet)
			{
				NetService.getDefault().sendSyjStatus(GlobalInfo.timeHttp, GlobalInfo.syjStatus);
			}
			
			// 采用异步方式执行
			display.asyncExec(new Runnable()
			{
				public void run()
				{
					// 网络状态发生变化
					if (GlobalInfo.isOnline != oldnet)
					{
						// 记录日志
						AccessDayDB.getDefault().writeWorkLog(GlobalInfo.isOnline ? Language.apply("收银机自动联网运行") : Language.apply("收银机自动脱网运行"));

						// 刷新状态栏网络状态显示
						GlobalInfo.statusBar.setNetStatus();
					}

					// 执行定时任务,正在销售状态不检查任务
					if (GlobalInfo.isOnline && curnet && GlobalInfo.syjStatus.status != StatusType.STATUS_SALEING)
					{
						TaskExecute.getDefault().executeTimeTask(true);
					};
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
