package com.efuture.javaPos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.KeyPadSet;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.UI.Design.BackgroundForm;
import com.efuture.javaPos.UI.Design.PosFormFuncTab;
import com.efuture.javaPos.UI.Design.SaleForm;
import com.efuture.javaPos.UI.Design.SaleFormTouch;
import com.efuture.javaPos.UI.Design.StatusBarForm;
import com.swtdesigner.SWTResourceManager;

public class javaPos
{
	public javaPos()
	{
		boolean errflag = false;

		try
		{
			// 标记当前运行为JavaPOS,自定义字体启用
			ConfigClass.curRunning = "JavaPOS";

			// 载入配置文件
			if (!ConfigClass.LoadConfigSet())
			{
				PublicMethod.forceQuit();
			}

			// 创建背景窗体
			Shell shell = new Shell(GlobalVar.style);
			shell.forceActive();
			shell.setFocus();
			GlobalInfo.mainshell = shell;

			Rectangle rec = Display.getDefault().getPrimaryMonitor().getClientArea();
			if (!ConfigClass.TouchSaleForm.equals("Default"))
			{
				GlobalVar.rec = new Point(rec.width - ConfigClass.SplitScrSize, rec.height);
			}
			else
			{
				GlobalVar.rec = new Point(rec.width, rec.height);
			}

			// 创建全局异常日志文件
			if (PublicMethod.createExceptionStream() == null)
			{
				PublicMethod.forceQuit();
			}

			// 根据操作系统设置窗口风格
			if (ConfigClass.TitleStyle.equals("Linux"))
			{
				GlobalVar.heightPL = 15;
				GlobalVar.rec.y += 25;
				GlobalVar.style = GlobalVar.style_linux;
			}
			else
			{
				GlobalVar.heightPL = 0;
				GlobalVar.style = GlobalVar.style_windows;
			}
			if (ConfigClass.MouseMode)
				GlobalVar.style |= SWT.CLOSE;

			// 设置系统模块
			GlobalInfo.ModuleType = ManipulatePrecision.getRegisterCodeKey(ConfigClass.CDKey);
			String s = ManipulatePrecision.readApplicationModuleType();
			if (s != null && !s.trim().equals(""))
				GlobalInfo.ModuleType = s.trim();

			// 载入按键配置
			KeyPadSet key = new KeyPadSet(GlobalVar.KeyFile);

			if (!key.loadFile())
			{
				PublicMethod.forceQuit();
			}

			key.setKeyMem();

			// 创建标题栏
			if (GlobalVar.heightPL != 0)
			{
				GlobalInfo.labelTitle = new Label(shell, SWT.NONE);
				GlobalInfo.labelTitle.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
				GlobalInfo.labelTitle.setBounds(0, 0, Display.getDefault().getClientArea().width, 25);
				GlobalInfo.labelTitle.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				GlobalInfo.labelTitle.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
				GlobalInfo.labelTitle.setAlignment(SWT.CENTER);
			}

			// 初始显示连接服务器信息
			if (ConfigClass.DebugMode)
				setMainShellTitle("http://" + ConfigClass.ServerIP + ":" + ConfigClass.ServerPort + ConfigClass.ServerPath);

			// 关闭WINDOWS任务栏
			GlobalVar.EnableWindowsTrayWnd(false);

			GlobalInfo.initHandle = shell.handle;

			// 初始化开机界面
			GlobalInfo.background = new BackgroundForm(shell);

			// 初始化状态栏
			GlobalInfo.statusBar = new StatusBarForm(shell, SWT.NONE);

			// 将状态栏放入开机界面
			GlobalInfo.background.setStatusBarForm(GlobalInfo.statusBar);

			// 显示开机界面
			GlobalInfo.background.open();

			// 把主窗口激活
			GlobalInfo.mainshell.forceActive();

			PosLog.getLog(getClass()).info("startLoadinfo");
			// 开始下载开机信息
			// 登陆界面在开机时根据断点情况需要显示
			if (!GlobalInfo.background.startLoadInfo())
			{
				PublicMethod.forceQuit();
			}

			PosLog.getLog(getClass()).info("endLoadInfo");
			// 设置窗口标题
			setMainShellTitle("[" + GlobalInfo.sysPara.mktcode + "]" + GlobalInfo.sysPara.mktname + "    [IP: " + GlobalInfo.ipAddr + "]");
			// 销售界面显示
			GlobalInfo.background.setVersionEanble(false);

			//当设置为Touch时加载触屏Form
			if (ConfigClass.TouchSaleForm.equalsIgnoreCase("Touch"))
			{
				GlobalInfo.saleform = new SaleFormTouch(shell, SWT.NONE);
			}//当设置为客户化代码时加载触屏客户化Form
			else if (ConfigClass.TouchSaleForm.equalsIgnoreCase("Nxmx"))
			{
				PosFormFuncTab tabForm = new PosFormFuncTab(shell, SWT.NONE);
				GlobalInfo.saleform = tabForm.getSaleForm();
			}//默认处理
			else
			{
				GlobalInfo.saleform = new SaleForm(shell, SWT.NONE);
			}

			// 等待收银员操作
			GlobalInfo.background.waitClose();
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).fatal(ex);
			ex.printStackTrace();
			new MessageBox(Language.apply("系统发生未知异常，即将关闭系统\n") + ex.getMessage(), null, false);
			errflag = true;
		}
		finally
		{
			if (!errflag)
			{
				new MessageBox(Language.apply("无法捕获系统异常,请联系管理员\n"), null, false);
			}

			LoadSysInfo.getDefault().ExitSystem();
		}
	}

	public static void setMainShellTitle(String title)
	{
		if (GlobalVar.heightPL != 0)
		{
			GlobalInfo.labelTitle.setText(title);
		}
		else
		{
			GlobalInfo.mainshell.setText(title);
		}
	}

	public static void main(String[] args)
	{
		// 设定时区
		System.setProperty("user.timezone", "Asia/Shanghai");

		if (args != null)
		{
			if (args.length > 0)
			{
				GlobalVar.RefushConfPath(args[0].trim());
			}
		}

		new javaPos();
	}
}
