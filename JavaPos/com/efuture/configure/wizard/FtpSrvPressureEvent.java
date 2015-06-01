package com.efuture.configure.wizard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;

public class FtpSrvPressureEvent
{
	public Text txtSrvUrl;
	public Text txtPort;
	public Text txtSrvFile;
	public Text txtLocalDir;
	public Text txtUsr;
	public Text txtPass;
	public Button btnDir;
	public Text txtThdCount;
	public Text txtDelay;
	public Table table;
	private Label runCount;
	private Label stopCount;
	private Label cmdCount;
	public Button btnStartTest;
	public Button btnStopTest;
	public Button btnClrTxt;
	private Button btnClose;
	public Shell shell;

	public static String ftpDir = "";

	public static int connectTimeout = 5000;
	public static int receiveTimeout = 10000;

	public static FtpSrvPressureEvent ftpSrvPressure = null;

	public static boolean isStartBtnPressed = false; // 开始按钮是否按下
	public static boolean isStopBtnPressed = false; // 停止按钮是否按下
	public static Vector cmdList; // 命令列表
	public static Thread[] threadAry; // 线程数组
	public static String[] result;
	public int cmdSendTimes;

	public synchronized static boolean getStopBtnStatus()
	{
		return isStopBtnPressed;
	}

	public FtpSrvPressureEvent(FtpSrvPressureForm form)
	{
		txtSrvUrl = form.txtSrvUrl;
		txtPort = form.txtPort;
		txtSrvFile = form.txtSrvFile;
		txtLocalDir = form.txtLocalDir;
		txtUsr = form.txtUsr;
		txtPass = form.txtPass;
		btnDir = form.btnDir;
		txtThdCount = form.txtThdCount;
		txtDelay = form.txtDelay;
		table = form.table;
		runCount = form.lbRunCount;
		stopCount = form.lbStopCount;
		cmdCount = form.lbcmdCount;
		btnStartTest = form.btnStartTest;
		btnStopTest = form.btnStopTest;
		btnClrTxt = form.btnClrTxt;
		btnClose = form.btnClose;
		shell = form.shell;

		loadConfig();
		ConfigClass.MouseMode = true;

		txtThdCount.setText("10");
		txtDelay.setText("1000");

		ConfigClass.MouseMode = true;

		btnDir.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				// 创建一个打开对话框,样式设置为SWT.OPEN
				DirectoryDialog dialog = new DirectoryDialog(shell);

				//dialog.setMessage("请选择所要保存的文件夹");
				dialog.setMessage(Language.apply("请选择所要保存的文件夹"));
				//dialog.setText("选择文件目录");
				dialog.setText(Language.apply("选择文件目录"));
				dialog.setFilterPath("C:\\");
				String saveFile = dialog.open();
				
				if (saveFile == null)
					return;
				else
					txtLocalDir.setText(saveFile);
			}
		});

		btnStartTest.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				if (isStartBtnPressed)
					return;

				if (txtSrvFile.getText().trim().equals("") || txtLocalDir.getText().trim().equals("") || txtThdCount.getText().trim().equals(""))
					return;

				// 处理延时时间
				if (txtDelay.getText().indexOf(".") > -1 || txtDelay.getText().trim().startsWith(",") || txtDelay.getText().trim().equals(""))
				{
					txtDelay.selectAll();
					txtDelay.setFocus();
					return;
				}

				int cmddelay = 0;
				int thddelay = 0;

				String[] tmpAry = txtDelay.getText().trim().split(",");
				if (tmpAry.length == 2)
				{
					cmddelay = Integer.parseInt(tmpAry[0]);
					thddelay = Integer.parseInt(tmpAry[1]);
				}
				else
				{
					cmddelay = 1000;
					thddelay = 100;
				}

				if (table.getItemCount() != 0)
					table.removeAll();

				clrTestFile();

				cmdSendTimes = 0;

				runCount.setText("0");
				stopCount.setText("0");
				cmdCount.setText("0");

				isStartBtnPressed = true;
				isStopBtnPressed = false;
				result = null;

				String srvUrl = txtSrvUrl.getText().trim();
				String srvPort = txtPort.getText().trim();

				String usrname = txtUsr.getText();
				String pass = txtPass.getText();

				String srvFile = txtSrvFile.getText();
				String localPath = txtLocalDir.getText().trim();

				String thdCount = txtThdCount.getText().trim();

				Thread thread = new Thread(new FtpSrvPressureBS(srvUrl, srvPort, srvFile, localPath, usrname, pass, thdCount, cmddelay, thddelay));
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
				catch (Exception ex)
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

				clrTestFile();

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
					//new MessageBox("命令无返回信息", null, false);
				    new MessageBox(Language.apply("命令无返回信息"), null, false);
				else if (result[index].equals(""))
					return;
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
	}

	public void setSelfRef()
	{
		ftpSrvPressure = this;
	}

	public static FtpSrvPressureEvent getDefault()
	{
		return ftpSrvPressure;
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
					item.setText(2, retInfo[1]); // 运行次数
					item.setText(3, retInfo[2]); // 停止时间
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
					}
				}
				cmdCount.setText(String.valueOf(cmdSendTimes));
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

				stopCount.setText(String.valueOf(retInfo[1]));
			}
		});
	}

	private void clrTestFile()
	{
		try
		{
			File dir = new File(txtLocalDir.getText().trim());
			if (dir.exists())
			{
				File[] fileList = dir.listFiles();
				if (fileList.length > 1)
				{
					for (int i = 0; i < fileList.length; i++)
					{
						fileList[i].delete();
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void loadConfig()
	{
		String line = GlobalVar.ConfigPath + "/Update.ini";
		Vector v = CommonMethod.readFileByVector(line);

		if (v == null)
		{
			return;
		}

		for (int i = 0; i < v.size(); i++)
		{
			String[] row = (String[]) v.elementAt(i);
			if ("FtpDefaultTimeout".equalsIgnoreCase(row[0]))
			{
				connectTimeout = Integer.parseInt(row[1]);
			}
			else if ("FtpDataTimeout".equalsIgnoreCase(row[0]))
			{
				receiveTimeout = Integer.parseInt(row[1]);
			}
			else if ("FtpUpdateIP".equalsIgnoreCase(row[0]))
			{
				txtSrvUrl.setText(row[1]);
			}
			else if ("FtpUpdatePort".equalsIgnoreCase(row[0]))
			{
				txtPort.setText(row[1]);
			}
			else if ("FtpUpdatePath".equalsIgnoreCase(row[0]))
			{
				ftpDir = row[1];
			}
			else if ("FtpUpdateUser".equalsIgnoreCase(row[0]))
			{
				txtUsr.setText(row[1]);
			}
			else if ("FtpUpdatePwd".equalsIgnoreCase(row[0]))
			{
				txtPass.setText(row[1]);
			}
			else
			{
				continue;
			}
		}
	}
}
