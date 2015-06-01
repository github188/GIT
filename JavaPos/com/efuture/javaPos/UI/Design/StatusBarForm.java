package com.efuture.javaPos.UI.Design;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.efuture.DeBugTools.DebugReader;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.PosClock;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.AssemblyInfo;
import com.efuture.javaPos.Communication.UpdateBaseInfo;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.swtdesigner.SWTResourceManager;

public class StatusBarForm extends Composite
{
	private Label lbl_PosTime;
	private PosClock label;
	private Label lbl_Message;
	private Label lbl_NetStatus;
	private Label label_5;
	private static Label label_GDCount;
	boolean twinkling = true;
	TimerTask time = null;
	Timer timer = null;
	Composite parent = null;
	private Vector msghistory = null;

	/**
	 * Create the composite
	 * 
	 * @param parent
	 * @param style
	 */
	public StatusBarForm(Composite parent, int style)
	{
		super(parent, style);

		this.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

		this.parent = parent;
		timer = new Timer(true);

		if (GlobalInfo.ModuleType.indexOf("ZM")==0)
		{
			//中免
			create_ZM();
		}
		else
		{
			create();
		}
		
	}

	private void create_ZM()
	{
		this.setLayout(new FormLayout());

		final FormData formData = new FormData();
		formData.bottom = new FormAttachment(this, -6, SWT.DEFAULT);

		final FormData formData_1 = new FormData();
		formData_1.right = new FormAttachment(100, -7);
		formData_1.left = new FormAttachment(0, 10);
		formData_1.top = new FormAttachment(100, -30);
		formData_1.bottom = new FormAttachment(100, 0);
		this.setLayoutData(formData_1);

		label_GDCount = new Label(this, SWT.NONE|SWT.CENTER);
		final FormData fd_label1 = new FormData();
		fd_label1.right = new FormAttachment(100, -101);
		label_GDCount.setLayoutData(fd_label1);
		label_GDCount.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		label_GDCount.setText(Language.apply("挂单数:"));
		
		/*label_1.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
//				new MenuFuncForm(GlobalInfo.saleform.getShell(), GlobalInfo.posLogin.funcmenu);
				CustomLocalize.getDefault().createMenuFuncBS().openLwCz(null, null);
			}
		});
*/
		lbl_Message = new Label(this, SWT.NONE);
		final FormData formData_3 = new FormData();
		formData_3.left = new FormAttachment(0, 192);
		formData_3.top = new FormAttachment(0, 6);

		lbl_Message.setLayoutData(formData_3);
		lbl_Message.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		lbl_Message.setText(Language.apply("提示框"));
		lbl_Message.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				String[] title1 = { Language.apply("描述") };
				int[] width1 = {430 };
				Vector cho = new Vector();
				cho.add(new String[]{Language.apply("进入日志工具")});
				cho.add(new String[]{Language.apply("查看状态提示历史记录")});
				int choice = new MutiSelectForm().open(Language.apply("POS 日志工具"), title1, width1, cho, false);
				if (choice == 0)
				{
					DebugReader window = new DebugReader();
					window.open();
				}

				if (choice == 1 && msghistory != null)
				{
					String[] title = { Language.apply("时间"), Language.apply("描述") };
					int[] width = { 100, 430 };
					new MutiSelectForm().open(Language.apply("查看状态提示历史记录"), title, width, msghistory, false);
				}
			}
		});

		Color cl = CustomLocalize.getDefault().getStatusBarColor();
		if (cl != null)
			lbl_Message.setForeground(cl);

		label = new PosClock(this, SWT.NONE);//SWT.BORDER
		fd_label1.left = new FormAttachment(label, 0, SWT.RIGHT);
		fd_label1.bottom = new FormAttachment(label, 0, SWT.BOTTOM);
		fd_label1.top = new FormAttachment(label, 0, SWT.TOP);
		label.setAlignment(SWT.CENTER);
		final FormData formData_4 = new FormData();
		formData_4.left = new FormAttachment(100, -380);
		formData_4.right = new FormAttachment(100, -195);
		label.setLayoutData(formData_4);
		label.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		label.setText(Language.apply("2010-09-15 09:34:07 三"));
		label.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				new MenuFuncForm(GlobalInfo.saleform.getShell(), GlobalInfo.posLogin.funcmenu);
			}
		});

		lbl_NetStatus = new Label(this, SWT.NONE);
		formData_4.bottom = new FormAttachment(lbl_NetStatus, 30, SWT.TOP);
		formData_4.top = new FormAttachment(lbl_NetStatus, 0, SWT.TOP);
		lbl_NetStatus.setAlignment(SWT.CENTER);
		final FormData formData_6 = new FormData();
		formData_6.left = new FormAttachment(100, -50);
		formData_6.right = new FormAttachment(100, -10);
		formData_6.top = new FormAttachment(0, 6);
		lbl_NetStatus.setLayoutData(formData_6);
		lbl_NetStatus.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		lbl_NetStatus.setText(Language.apply("联网"));
		lbl_NetStatus.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				CustomLocalize.getDefault().createMenuFuncBS().openLwCz(null, null);
			}
		});

		/*label_5 = new Label(this, SWT.BORDER);
		final FormData formData_7 = new FormData();
		formData_7.right = new FormAttachment(0, 85);
		formData_7.bottom = new FormAttachment(0, 30);
		formData_7.top = new FormAttachment(0, 0);
		formData_7.left = new FormAttachment(0, 0);
		label_5.setLayoutData(formData_7);
		label_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		if(GlobalInfo.ModuleType.equals("WDGC")){
			label_5.setText("");
		}else{
			label_5.setText(AssemblyInfo.AssemblyStatusText);
		}
		
		label_5.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				CustomLocalize.getDefault().createMenuFuncBS().openGy(null, null);
			}
		});*/

		lbl_PosTime = new Label(this, SWT.NONE);//SWT.CENTER | SWT.BORDER
		formData_3.right = new FormAttachment(label, 1, SWT.LEFT);
		formData_6.bottom = new FormAttachment(lbl_PosTime, 0, SWT.BOTTOM);
		final FormData formData_8 = new FormData();
		formData_8.left = new FormAttachment(label_GDCount, 0);
		formData_8.right = new FormAttachment(lbl_NetStatus, 5, SWT.LEFT);
		formData_8.bottom = new FormAttachment(label, 30, SWT.TOP);
		formData_8.top = new FormAttachment(label, 0, SWT.TOP);
		lbl_PosTime.setLayoutData(formData_8);
		lbl_PosTime.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		lbl_PosTime.setAlignment(SWT.CENTER);
		lbl_PosTime.setText(Language.apply("班次"));
		lbl_PosTime.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				CustomLocalize.getDefault().createMenuFuncBS().openSyyDl(new MenuFuncDef(), null);
			}
		});

		Label button;
		button = new Label(this, SWT.NONE);//SWT.FLAT | SWT.BORDER

		final FormData formData5 = new FormData();
		formData5.right = new FormAttachment(0, 195);
		formData5.left = new FormAttachment(0, 0);
		formData5.bottom = new FormAttachment(0, 30);
		formData5.top = new FormAttachment(0, 0);
		button.setLayoutData(formData5);

		// GlobalVar.HomeBase + "//" +
		if(!(GlobalInfo.ModuleType.equals("WDGC"))){
			button.setImage(SWTResourceManager.getImage(ConfigClass.BackImagePath + "logo.png"));
		}
		button.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				//debug模式或专卖模式下不需要强推POS界面
				if (ConfigClass.DebugMode || ConfigClass.DebugModeString.equals("Z"))
				{
					try
					{
						Runtime.getRuntime().exec("c:\\javapos\\javaposbank.exe DESKTOP");

					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
				else
				{
					ConfigClass.KeepActive = !ConfigClass.KeepActive;
				}
			}});
	}

	private void create()
	{
		this.setLayout(new FormLayout());

		final FormData formData = new FormData();
		formData.bottom = new FormAttachment(this, -6, SWT.DEFAULT);

		final FormData formData_1 = new FormData();
		formData_1.right = new FormAttachment(100, -7);
		formData_1.left = new FormAttachment(0, 10);
		formData_1.top = new FormAttachment(100, -30);
		formData_1.bottom = new FormAttachment(100, 0);
		this.setLayoutData(formData_1);

		lbl_Message = new Label(this, SWT.BORDER);

		final FormData formData_3 = new FormData();
		formData_3.right = new FormAttachment(100, -387);
		lbl_Message.setLayoutData(formData_3);
		lbl_Message.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lbl_Message.setText(Language.apply("提示框"));
		lbl_Message.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				String[] title1 = { Language.apply("描述") };
				int[] width1 = {430 };
				Vector cho = new Vector();
				cho.add(new String[]{Language.apply("进入日志工具")});
				cho.add(new String[]{Language.apply("查看状态提示历史记录")});
				int choice = new MutiSelectForm().open(Language.apply("POS 日志工具"), title1, width1, cho, false);
				if (choice == 0)
				{
					DebugReader window = new DebugReader();
					window.open();
				}

				if (choice == 1 && msghistory != null)
				{
					String[] title = { Language.apply("时间"), Language.apply("描述") };
					int[] width = { 100, 430 };
					new MutiSelectForm().open(Language.apply("查看状态提示历史记录"), title, width, msghistory, false);
				}
			}
		});

		Color cl = CustomLocalize.getDefault().getStatusBarColor();
		if (cl != null)
			lbl_Message.setForeground(cl);

		label = new PosClock(this, SWT.BORDER);
		label.setAlignment(SWT.CENTER);
		formData_3.top = new FormAttachment(label, -30, SWT.BOTTOM);
		formData_3.bottom = new FormAttachment(label, 0, SWT.BOTTOM);

		final FormData formData_4 = new FormData();
		formData_4.left = new FormAttachment(100, -382);
		formData_4.right = new FormAttachment(100, -125);
		label.setLayoutData(formData_4);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		label.setText(Language.apply("2010-09-15 09:34:07 三"));
		label.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				new MenuFuncForm(GlobalInfo.saleform.getShell(), GlobalInfo.posLogin.funcmenu);
			}
		});

		lbl_NetStatus = new Label(this, SWT.CENTER | SWT.BORDER);
		formData_4.bottom = new FormAttachment(lbl_NetStatus, 30, SWT.TOP);
		formData_4.top = new FormAttachment(lbl_NetStatus, 0, SWT.TOP);
		lbl_NetStatus.setAlignment(SWT.CENTER);
		final FormData formData_6 = new FormData();
		formData_6.left = new FormAttachment(100, -50);
		formData_6.right = new FormAttachment(100, 0);
		formData_6.bottom = new FormAttachment(0, 30);
		formData_6.top = new FormAttachment(0, 0);
		lbl_NetStatus.setLayoutData(formData_6);
		lbl_NetStatus.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lbl_NetStatus.setText(Language.apply("联网"));
		lbl_NetStatus.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				CustomLocalize.getDefault().createMenuFuncBS().openLwCz(null, null);
			}
		});

		label_5 = new Label(this, SWT.BORDER);
		final FormData formData_7 = new FormData();
		formData_7.right = new FormAttachment(0, 85);
		formData_7.bottom = new FormAttachment(0, 30);
		formData_7.top = new FormAttachment(0, 0);
		formData_7.left = new FormAttachment(0, 0);
		label_5.setLayoutData(formData_7);
		label_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		if(GlobalInfo.ModuleType.equals("WDGC")){
			label_5.setText("");
		}else{
			label_5.setText(AssemblyInfo.AssemblyStatusText);
		}
		
		label_5.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				CustomLocalize.getDefault().createMenuFuncBS().openGy(null, null);
			}
		});

		lbl_PosTime = new Label(this, SWT.CENTER | SWT.BORDER);
		final FormData formData_8 = new FormData();
		formData_8.right = new FormAttachment(lbl_NetStatus, -5, SWT.LEFT);
		formData_8.bottom = new FormAttachment(label, 30, SWT.TOP);
		formData_8.top = new FormAttachment(label, 0, SWT.TOP);
		formData_8.left = new FormAttachment(label, 5, SWT.RIGHT);
		lbl_PosTime.setLayoutData(formData_8);
		lbl_PosTime.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		lbl_PosTime.setAlignment(SWT.CENTER);
		lbl_PosTime.setText(Language.apply("班次"));
		lbl_PosTime.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				CustomLocalize.getDefault().createMenuFuncBS().openSyyDl(new MenuFuncDef(), null);
			}
		});

		final Button button;
		button = new Button(this, SWT.FLAT | SWT.BORDER);
		formData_3.left = new FormAttachment(button, 5, SWT.RIGHT);

		final FormData formData5 = new FormData();
		formData5.left = new FormAttachment(label_5, 5, SWT.RIGHT);
		formData5.right = new FormAttachment(0, 130);
		formData5.bottom = new FormAttachment(0, 30);
		formData5.top = new FormAttachment(0, 0);
		button.setLayoutData(formData5);

		// GlobalVar.HomeBase + "//" +
		if(!(GlobalInfo.ModuleType.equals("WDGC"))){
			button.setImage(SWTResourceManager.getImage(ConfigClass.BackImagePath + "logo.png"));
		}
		button.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent mouseevent)
			{
				//debug模式或专卖模式下不需要强推POS界面
				if (ConfigClass.DebugMode || ConfigClass.DebugModeString.equals("Z"))
				{
					try
					{
						Runtime.getRuntime().exec("c:\\javapos\\javaposbank.exe DESKTOP");

					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
				else
				{
					ConfigClass.KeepActive = !ConfigClass.KeepActive;
				}
			}});
	}
	
	public void dispose()
	{
		super.dispose();
	}

	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	public String getHelpMessage()
	{
		if (!lbl_Message.isDisposed())
			return lbl_Message.getText();
		else
			return "";
	}

	private void addHelpMessageHistor(String text)
	{
		if (msghistory == null)
			msghistory = new Vector();
		if (msghistory.size() >= 100)
			msghistory.remove(99);

		msghistory.insertElementAt(new String[] { ManipulateDateTime.getCurrentTime(), text }, 0);
	}

	public void asyncSetHelpMessage(final String text, boolean isthread)
	{
		if (isthread)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					addHelpMessageHistor(text);
					if (!lbl_Message.isDisposed())
						lbl_Message.setText(text);
				}
			});
		}
		else
		{
			addHelpMessageHistor(text);
			if (!lbl_Message.isDisposed())
			{
				lbl_Message.setText(text);
				while (Display.getCurrent().readAndDispatch())
					;
			}
		}
	}

	public void setHelpMessage(String text)
	{
		addHelpMessageHistor(text);
		if (!lbl_Message.isDisposed())
			lbl_Message.setText(text);
	}

	public void setPosTime(String text)
	{
		lbl_PosTime.setText(text);
	}

	public void setBackProcInfo(String text)
	{
		if (label_5 == null) return;
		
		if (text == null)
		{
			label_5.setText(AssemblyInfo.AssemblyStatusText);
		}
		else
		{
			label_5.setText(text);
		}
	}

	public void setNetStatus()
	{
		if (GlobalInfo.isOnline)
		{
			lbl_NetStatus.setText(Language.apply("联网"));
			if (GlobalInfo.sysPara != null && GlobalInfo.sysPara.localfind != 'N')
				lbl_NetStatus.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));//SWT.COLOR_DARK_GRAY
			else
				lbl_NetStatus.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
			lbl_NetStatus.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		}
		else
		{
			lbl_NetStatus.setText(Language.apply("脱网"));
			lbl_NetStatus.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			lbl_NetStatus.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

			// 检查正在更新本地数据库,如果在更新则等待更新完毕才能使用脱网使用本地数据库
			UpdateBaseInfo.waitUpdateBase();
		}
	}

	/**
	 * 设置挂单数量
	 * @param hangCount 挂单数量
	 */
	public void setHangCount(int hangCount)
	{
		try
		{
			if (label_GDCount!=null)
			{
				if (hangCount<0) hangCount = 0;
				label_GDCount.setText(Language.apply("挂单数:") + hangCount);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void setHangCount()
	{
		setHangCount(getHangFileCount());
	}

	private int getHangFileCount()
	{
		int n = 0;

		TimeDate date = new TimeDate(GlobalInfo.balanceDate, "00:00:00");
		String path = ConfigClass.LocalDBPath + "Invoice/" + date.cc + date.yy + date.mm + date.dd;

		File file = new File(path);
		if (file.isDirectory())
		{
			for (int i = 0; i < file.list().length; i++)
			{
				if (file.list()[i].indexOf("GD_") == 0)
				{
					n++;
				}
			}
		}

		return n;
	}
}
