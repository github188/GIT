package com.efuture.javaPos.Global;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.javaPos;
import com.efuture.javaPos.Device.SecMonitor;

public class IdleThread extends TimerTask
{
	String lastcmd = null;
	Vector v = new Vector();
	public IdleThread()
	{

	}

	public void run()
	{
		try
		{
			changeDayDB();
			setSecMonitor();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setSecMonitor()
	{
		// 读取配置文件
		if (!PathFile.fileExist(GlobalVar.ConfigPath+"\\secMonitor.ini"))
		{
			return ;
		}
		
		v = CommonMethod.readFileByVector(GlobalVar.ConfigPath+"\\secMonitor.ini");
		
		if (v == null) return ;
		
		String curDateTime = ManipulateDateTime.getCurrentTime();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		for (int i = 0 ; i < v.size(); i++)
		{
			String[] cmd = (String[]) v.elementAt(i);
			if (cmd[0] == null || cmd[0].length() < 0) continue;
			
			if (cmd[1] == null) cmd[1] = "";
			String[] times = cmd[0].trim().split("-");
			try
			{
				if ((df.parse(curDateTime).compareTo(df.parse(times[0])) >= 0 && df.parse(curDateTime).compareTo(df.parse(times[1])) <= 0))
				{
					if (lastcmd == null || !lastcmd.equals(cmd[1].trim()))
					{
						lastcmd = cmd[1].trim();
						if (SecMonitor.secMonitor != null) SecMonitor.secMonitor.sendCmd("VIDEOPLAY|"+cmd[1]);
						break;
					}
					
				}
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void changeDayDB()
	{
		try
		{
			if (GlobalInfo.balanceDate != null && GlobalInfo.dayDB != null)
			{
				String curdt = ManipulateDateTime.staticGetDateBySlash();
				boolean resetDay = false;
				
				// 2010/01/01|00:00:00,2010/01/02|00:00:00 （日期时间区间）
				if (GlobalInfo.sysPara.overNightBegin.length() == 19 && GlobalInfo.sysPara.overNightEnd.length() == 19)
				{
					String[] begin = GlobalInfo.sysPara.overNightBegin.split("\\|");
					String[] end   = GlobalInfo.sysPara.overNightEnd.split("\\|");
		    		String dayBegin  = begin[0].trim();		// 开始日期
		    		String timeBegin = begin[1].trim();		// 开始时间
		    		String dayEnd 	 = end[0].trim();		// 结束日期
		    		String timeEnd 	 = end[1].trim();		// 结束时间

					// 如果当前时间处于区间外则切换
		    		String curDateTime = ManipulateDateTime.getCurrentDateTime();
		    		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					if (!(df.parse(curDateTime).compareTo(df.parse(dayBegin + " " + timeBegin)) >= 0 && df.parse(curDateTime).compareTo(df.parse(dayEnd + " " + timeEnd)) <= 0))
					{
						resetDay = true;
					}
				}
				else if (GlobalInfo.sysPara.overNightTime.length() == 8) // 02:30:00 （时间点）
				{
					// 记账日期不等于当前日期时且设置的时间点小于当前时间是切换
					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
					if (!curdt.equals(GlobalInfo.balanceDate) && df.parse(ManipulateDateTime.getCurrentTime()).compareTo(df.parse(GlobalInfo.sysPara.overNightTime)) > 0)
					{
						resetDay = true;
					}
				}
				// 没有设置通宵营业时
				else
				{
					if (!curdt.equals(GlobalInfo.balanceDate))
					{
						resetDay = true;
					}
				}

				// 检查是否需要切换当天日期数据库
				if (resetDay)
				{
					// 设置记账日期
					GlobalInfo.balanceDate = curdt;
					
					// 收银机状态归零
					GlobalInfo.syjStatus.bs = 0;
					GlobalInfo.syjStatus.je = 0;

					// 更换每日数据源
					Display.getDefault().syncExec(new Runnable()
					{
						public void run()
						{
							Sqldb sqldb = null;

							ProgressBox pb = new ProgressBox();
							pb.setText(Language.apply("正在切换每日数据源,请等待....."));

							// 先创建每日数据库
							LoadSysInfo.getDefault().createDayDB();

							// 连接每日数据库
							if ((sqldb = LoadSysInfo.getDefault().loadDayDB(GlobalInfo.balanceDate)) != null)
							{
								GlobalInfo.dayDB.Close();
								GlobalInfo.dayDB = null;
								GlobalInfo.dayDB = sqldb;
							}
							else
							{
								new MessageBox(Language.apply("连接当天本地数据库失败!\n\n请检查后重新启动"), null, false);

								// 退出系统
								PublicMethod.forceQuit();
							}
							//
							pb.close();
							pb = null;
						}
					});
				}
				else
				{
					// 未切换每日库且当前日期与记帐日期不一致
					if (!GlobalInfo.balanceDate.equals(curdt))
					{
						Display.getDefault().syncExec(new Runnable()
						{
							public void run()
							{
					            // 设置窗口标题
					            javaPos.setMainShellTitle("[" + GlobalInfo.sysPara.mktcode + "]" + GlobalInfo.sysPara.mktname + "    [IP: " + GlobalInfo.ipAddr + "]" + "  ("+GlobalInfo.balanceDate+")");
							}
						});
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
