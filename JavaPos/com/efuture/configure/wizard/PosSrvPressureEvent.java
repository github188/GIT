package com.efuture.configure.wizard;

import java.io.*;
import java.util.Vector;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.*;
//import org.eclipse.swt.widgets.Listener;
//import org.eclipse.swt.widgets.Event;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;

public class PosSrvPressureEvent
{
	private Table table;
	private Text txtDelay;
	private Button btnStopTest;
	private Button btnStartTest;
	private Text txtThdCount;
	private Combo cmbTestCmd;
	private Text txtSrvUrl;
	private Button btnClrTxt;
	private Button btnClose;
	protected Shell shell;
	private Label runCount;
	private Label stopCount;
	private Label cmdCount;
	public static PosSrvPressureEvent srvPressEvt;

	private String ip = "127.0.0.1";
	private String port = "8080";
	private String path = "";

	// private static int PageSize = 15;
	public static int connectTimeout = 1000;
	public static int receiveTimeout = 2000;
	public static boolean isStartBtnPressed = false; // 开始按钮是否按下
	public static boolean isStopBtnPressed = false; // 停止按钮是否按下
	public static Vector cmdList; // 命令列表
	public static Thread[] threadAry; // 线程数组
	public static String[] result;
	public int cmdSendTimes;
	public int cmdSendFaileTimes;

	public PosSrvPressureEvent(PosSrvPressureFrom srvPress)
	{
		txtDelay = srvPress.txtDelay;
		btnStopTest = srvPress.btnStopTest;
		btnStartTest = srvPress.btnStartTest;
		table = srvPress.table;
		txtThdCount = srvPress.txtThdCount;
		cmbTestCmd = srvPress.cmbTestCmd;
		txtSrvUrl = srvPress.txtSrvUrl;
		btnClrTxt = srvPress.btnClrTxt;
		btnClose = srvPress.btnClose;
		runCount = srvPress.lbRunCount;
		stopCount = srvPress.lbStopCount;
		cmdCount = srvPress.lbcmdCount;

		shell = srvPress.shell;
		loadConfig();
		ConfigClass.MouseMode = true;

		cmdList = new Vector();
		readCmdFile();

		txtSrvUrl.setText("http://" + ip + ":" + Integer.parseInt(port) + path);
		/*
		 * table.addListener(SWT.SetData, new Listener() { public void
		 * handleEvent(Event event) { TableItem item = (TableItem) event.item;
		 * int index = event.index; int page = index / PageSize; //当前页 int start =
		 * page * PageSize; int end = start + PageSize; end = Math.min(end,
		 * table.getColumnCount()); for (int i=start; i<end; i++) { item =
		 * table.getItem(index); item.setText((String[])result.get(i)); } } });
		 */
		// table.setItemCount(result.size());
		btnStartTest.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				if (isStartBtnPressed && PosSrvPressureBS.allCount > 0)
					return;

				// 不允许某项为空
				if (txtSrvUrl.getText().trim() == "" || cmbTestCmd.getText().trim() == "" || txtThdCount.getText().trim() == "")
					return;

				// 处理延时时间
				if (txtDelay.getText().indexOf(".") > -1 || txtDelay.getText().trim().startsWith(",") || txtDelay.getText().trim().equals(""))
				{
					txtDelay.selectAll();
					txtDelay.setFocus();
					return;
				}

				int cmddelay = 1000;
				int thddelay = 100;
				int exceptiondelay = cmddelay;
				if (txtDelay.getText().trim().length() > 0)
				{
					String[] tmpAry = txtDelay.getText().trim().split(",");
					if (tmpAry.length > 0) cmddelay = Integer.parseInt(tmpAry[0]);
					if (tmpAry.length > 1) thddelay = Integer.parseInt(tmpAry[1]);
					if (tmpAry.length > 2) exceptiondelay = Integer.parseInt(tmpAry[2]);
				}
				
				
				if (table.getItemCount() != 0)
					table.removeAll();

				cmdSendTimes = 0;
				cmdSendFaileTimes = 0;

				runCount.setText("0");
				stopCount.setText("0");
				cmdCount.setText("0");

				if (txtSrvUrl.getText().indexOf("http://") == -1)
				{
					txtSrvUrl.setText("http://" + txtSrvUrl.getText().trim());
				}

				isStartBtnPressed = true;
				isStopBtnPressed = false;

//				String Status = "连接超时:" + connectTimeout + "接收超时:" + receiveTimeout + "命令延时:" + cmddelay + "线程延时:" + thddelay + "异常延时:" + exceptiondelay;
				String Status = Language.apply("连接超时:{0}接收超时:{1}命令延时:{2}线程延时:{3}异常延时:" ,new Object[]{connectTimeout + "",receiveTimeout + "",cmddelay + "",thddelay + "",exceptiondelay + ""});
				shell.setText(Language.apply("PosServer压力测试:") + Status);
//				shell.setText("PosServer压力测试:" + Status);
				Thread thread = new Thread(new PosSrvPressureBS(txtSrvUrl.getText().trim(), cmbTestCmd.getSelectionIndex(), txtThdCount.getText().trim(), cmddelay, thddelay, exceptiondelay));
				thread.start();
			}
		});

		btnStopTest.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				try
				{
					isStopBtnPressed = true; // 设置程序停止标志
					isStartBtnPressed = false;

					if (threadAry != null && threadAry.length > 1)
						Thread.sleep(threadAry.length * 15);
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();
				}
			}
		});

		btnClrTxt.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				if (isStartBtnPressed)
					return;

				if (table.getItemCount() != 0)
					table.removeAll();

				threadAry = null;
				result = null;

				runCount.setText("0");
				stopCount.setText("0");
				cmdCount.setText("0");
			}
		});

		btnClose.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				if (isStartBtnPressed)
					return;

				if (!shell.isDisposed())
					shell.close();
			}
		});

		table.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(final MouseEvent event)
			{
				int index = table.getSelectionIndex();
				if (result == null || index == -1)
					return;

				if (result[index] == null || result[index].equals(""))
					new MessageBox(Language.apply("命令无返回信息"), null, false);
//				new MessageBox("命令无返回信息", null, false);
				else
				{
					try
					{
						FileOutputStream ofile = new FileOutputStream(new File("c:\\srvtest.txt"));
						OutputStreamWriter owriter = new OutputStreamWriter(ofile);
						BufferedWriter bwriter = new BufferedWriter(owriter);
						bwriter.write(result[index]);

						bwriter.close();
						owriter.close();
						ofile.close();

						Runtime.getRuntime().exec("notepad.exe c:\\srvtest.txt");
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});

		// 初始化命令列表
		for (int i = 0; i < cmdList.size(); i++)
		{
			String[] tmpAry = (String[]) cmdList.elementAt(i);
			if (tmpAry != null && tmpAry.length > 1)
				cmbTestCmd.add(tmpAry[0]);
		}
		cmbTestCmd.add(Language.apply("全部命令"), 0);
//		cmbTestCmd.add("全部命令", 0);
		cmbTestCmd.select(0);

		System.setProperty("user.timezone", "Asia/Shanghai");
	}

	public void setSelfRef()
	{
		srvPressEvt = this;
	}

	public synchronized static boolean getStopBtnStatus()
	{
		return isStopBtnPressed;
	}

	public synchronized static PosSrvPressureEvent getDefault()
	{
		return srvPressEvt;
	}

	public void showThreadInfo(final String[] retInfo)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(retInfo);
			}
		});
	}

	public void updateTabLine(final String[] retInfo)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				if (retInfo.length == 2)
				{
					TableItem item = table.getItem(Integer.parseInt(retInfo[0]));
					item.setText(2, retInfo[1]);
					synchronized (this)
					{
						cmdSendTimes++;
					}
				}
				if (retInfo.length == 3)
				{
					TableItem item = table.getItem(Integer.parseInt(retInfo[0]));
					item.setText(2, retInfo[1]);
					item.setText(3, retInfo[2]);
				}
				if (retInfo.length == 4)
				{
					TableItem item = table.getItem(Integer.parseInt(retInfo[0]));
					item.setText(2, retInfo[1]); // 运行次数
					item.setText(3, retInfo[2]); // 停止时间
					item.setText(4, retInfo[3]); // 异常信息

					synchronized (this)
					{
						cmdSendTimes++;
						cmdSendFaileTimes++;
					}
				}
				cmdCount.setText(String.valueOf(cmdSendTimes));
				stopCount.setText(String.valueOf(cmdSendFaileTimes));
			}
		});
	}

	public void updateStatus(final int[] retInfo)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				if (retInfo[1] == 0)
					runCount.setText(String.valueOf(retInfo[0]));
				else
					runCount.setText(String.valueOf(retInfo[0] - retInfo[1]));

				stopCount.setText(String.valueOf(cmdSendFaileTimes));
				//stopCount.setText(String.valueOf(retInfo[1]));
			}
		});
	}

	// 读取命令配置文件
	public static void readCmdFile()
	{
		if (!PathFile.fileExist(GlobalVar.ConfigPath + "/TestCmd.ini"))
				return;
		
		BufferedReader br = CommonMethod.readFileGB2312(GlobalVar.ConfigPath + "/TestCmd.ini");
		String line = "";
		String[] itemCmd = new String[2];
		boolean isLine = false;
		try
		{
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith(";") && line.length() > 1)
				{
					itemCmd[0] = line.substring(1, line.length());
					continue;
				}
				if (line.length() > 18 && line.indexOf("#@#") > 1)
				{
					itemCmd[1] = line.trim();
					isLine = true;
				}
				if (line.toLowerCase().startsWith("bankfunc:"))
				{
					itemCmd[1] = line.trim();
					isLine = true;
				}

				if (isLine)
				{
					cmdList.add(itemCmd);
					itemCmd = new String[2];
					isLine = false;
				}
			}
			if (br != null)
				br.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public void loadConfig()
	{
		String line = GlobalVar.ConfigPath + "/Config.ini";
		System.out.println(line);
		if (!PathFile.fileExist(line))
			return;
		
		Vector v = CommonMethod.readFileByVector(line);

		if (v == null)
		{
			return;
		}

		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);

			if ("ServerIP".equalsIgnoreCase(row[0]))
			{
				ip = row[1];
			}
			else if ("ServerPath".equalsIgnoreCase(row[0]))
			{
				path = row[1];
			}
			else if ("ServerPort".equalsIgnoreCase(row[0]))
			{
				port = row[1];
			}
			else if ("ConnectTimeout".equalsIgnoreCase(row[0]))
			{
				connectTimeout = Integer.parseInt(row[1]);
			}
			else if ("ReceiveTimeout".equalsIgnoreCase(row[0]))
			{
				receiveTimeout = Integer.parseInt(row[1]);
			}
			else
			{
				continue;
			}
		}
	}
}
