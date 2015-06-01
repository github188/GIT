package posserver.tools.PressTest;

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
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;

public class PosSrvPressureEvent {
	private Table table;
	private Text txtDelay;
	private Button btnStopTest;
	private Button btnStartTest;
	private Text txtThdCount;
	private Combo cmbTestCmd;
	private Text txtSrvUrl;
	private Button btnClrTxt;
	private Button btnClose;
	private Button btnClearHisData;
	protected Shell shell;

	// 运行数量
	private Label runCount;
	// 错误数量
	private Label errorCount;
	// 发送数量
	private Label cmdCount;

	public static PosSrvPressureEvent srvPressEvt;

	public PosSrvPressureEvent(PosSrvPressureFrom srvPress) {
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
		errorCount = srvPress.lbErrorCount;
		cmdCount = srvPress.lbCmdCount;
		btnClearHisData = srvPress.btnClearHisData;

		shell = srvPress.shell;

		GlobalConfig.loadConfig();
		
		shell.setText("[" + GlobalConfig.identify + "]" + shell.getText());
		txtSrvUrl.setText("http://" + GlobalConfig.ip + ":"
				+ Integer.parseInt(GlobalConfig.port) + GlobalConfig.path);

		btnStartTest.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (!GlobalStatus.isIsstopcompleted()
						&& GlobalStatus.getActiveThreadNum() > 0)
					return;

				// 不允许某项为空
				if (txtSrvUrl.getText().trim() == ""
						|| cmbTestCmd.getText().trim() == ""
						|| txtThdCount.getText().trim() == "")
					return;

				// 处理延时时间
				if (txtDelay.getText().indexOf(".") > -1
						|| txtDelay.getText().trim().startsWith(",")
						|| txtDelay.getText().trim().equals("")) {
					txtDelay.selectAll();
					txtDelay.setFocus();
					return;
				}

				int cmddelay = 1000;
				int thddelay = 100;
				int exceptiondelay = cmddelay;
				if (txtDelay.getText().trim().length() > 0) {
					String[] tmpAry = txtDelay.getText().trim().split(",");
					if (tmpAry.length > 0)
						cmddelay = Integer.parseInt(tmpAry[0]);
					if (tmpAry.length > 1)
						thddelay = Integer.parseInt(tmpAry[1]);
					if (tmpAry.length > 2)
						exceptiondelay = Integer.parseInt(tmpAry[2]);
				}

				if (table.getItemCount() != 0)
					table.removeAll();

				runCount.setText("0");
				errorCount.setText("0");
				cmdCount.setText("0");

				if (txtSrvUrl.getText().indexOf("http://") == -1) {
					txtSrvUrl.setText("http://" + txtSrvUrl.getText().trim());
				}

				GlobalStatus.init();

				GlobalStatus.setIsstopcompleted(false);

				String Status = "连接超时:" + GlobalConfig.connectTimeout + "接收超时:"
						+ GlobalConfig.receiveTimeout + "命令延时:" + cmddelay
						+ "线程延时:" + thddelay + "异常延时:" + exceptiondelay + ((GlobalConfig.testTimer>0)?"测试时长:" + GlobalConfig.testTimer:""); 
				shell.setText("[" + GlobalConfig.identify + "]" + "PosServer压力测试:" + Status);

				GlobalConfig.srvUrl = txtSrvUrl.getText().trim();
				GlobalConfig.threadCount = Integer.parseInt(txtThdCount
						.getText().trim());
				GlobalConfig.cmdDelaytime = cmddelay;
				GlobalConfig.threadDelaytime = thddelay;
				GlobalConfig.exceptiondelaytime = exceptiondelay;

				if (cmbTestCmd.getSelectionIndex() == 0) {
					GlobalStatus.cmdlisting.addAll(GlobalConfig.cmdlist);
				} else {
					GlobalStatus.cmdlisting.add((String[]) GlobalConfig.cmdlist
							.elementAt(cmbTestCmd.getSelectionIndex() - 1));
				}

				Thread thread = new Thread(new PosSrvPressureBS());
				thread.start();
			}
		});

		btnStopTest.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				try {
					if (GlobalStatus.isIsstopcompleted())
						return;

					GlobalStatus.stopTimer1();
					
					GlobalStatus.isstoping = true;

					if (GlobalStatus.arrThreadStatus != null
							&& GlobalStatus.arrThreadStatus.length > 1)
						Thread.sleep(GlobalStatus.arrThreadStatus.length * 15);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		btnClrTxt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (!GlobalStatus.isIsstopcompleted())
					return;

				if (table.getItemCount() != 0)
					table.removeAll();

				runCount.setText("0");
				errorCount.setText("0");
				cmdCount.setText("0");
			}
		});

		btnClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (!GlobalStatus.isIsstopcompleted())
					return;

				if (!shell.isDisposed())
					shell.close();
			}
		});

		btnClearHisData.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				try {
					if (!GlobalStatus.isIsstopcompleted())
					{
						new MessageBox("请先完成测试!");
						return;
					}
					
					org.eclipse.swt.widgets.MessageBox messageBox = 
						  new org.eclipse.swt.widgets.MessageBox(shell, SWT.OK|SWT.CANCEL); 
					messageBox.setMessage("确定要清除历史的测试数据吗!");
						if (messageBox.open() == SWT.OK) 
						{ 
						  GlobalStatus.clearHisData();
						} 
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});

		table.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(final MouseEvent event) {
				int index = table.getSelectionIndex();
				if (GlobalStatus.arrThreadStatus[index] == null || index == -1)
					return;

				if (GlobalStatus.arrThreadStatus[index].getResult() == null
						|| GlobalStatus.arrThreadStatus[index].getResult()
								.equals(""))
					new MessageBox("命令无返回信息", null, false);
				else {
					try {
						FileOutputStream ofile = new FileOutputStream(new File(
								"c:\\srvtest.txt"));
						OutputStreamWriter owriter = new OutputStreamWriter(
								ofile);
						BufferedWriter bwriter = new BufferedWriter(owriter);
						bwriter.write(GlobalStatus.arrThreadStatus[index]
								.getResult());

						bwriter.close();
						owriter.close();
						ofile.close();

						Runtime.getRuntime()
								.exec("notepad.exe c:\\srvtest.txt");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		// 初始化命令列表
		for (int i = 0; i < GlobalConfig.cmdlist.size(); i++) {
			String[] tmpAry = (String[]) GlobalConfig.cmdlist.elementAt(i);
			if (tmpAry != null && tmpAry.length > 1)
				cmbTestCmd.add(tmpAry[0]);
		}

		cmbTestCmd.add("全部命令", 0);
		cmbTestCmd.select(0);

		System.setProperty("user.timezone", "Asia/Shanghai");
	}

	public void setSelfRef() {
		srvPressEvt = this;
	}

	public synchronized static PosSrvPressureEvent getDefault() {
		return srvPressEvt;
	}

	public void updateTabLine(final ThreadStatus threadstatus) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				TableItem item = null;

				if (threadstatus.getIndex() >= table.getItems().length) {
					item = new TableItem(table, SWT.NONE);
					item.setText(0, String.valueOf(threadstatus.getIndex()));
					item.setText(1, threadstatus.getStarttime());
				} else {
					item = table.getItem(threadstatus.getIndex());
				}

				item.setText(
						2,
						threadstatus.getRunCount() + " "
								+ threadstatus.getSucCount() + " "
								+ threadstatus.getErrorCount());
				item.setText(3, "");
				if (!threadstatus.isIssuc()) {
					item.setText(4, "发生异常,请双击查看...");
				} else {
					item.setText(4, "");
				}

				if (!threadstatus.isIssuc()) {
					item.setText(4, "发生异常,请双击查看...");
				}

				cmdCount.setText(String.valueOf(GlobalStatus.getCmdSendTimes()));
				errorCount.setText(String.valueOf(GlobalStatus
						.getCmdSendFaileTimes()));
			}
		});
	}

	public void updateStatus() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				runCount.setText(String.valueOf(GlobalStatus
						.getActiveThreadNum()));

				errorCount.setText(String.valueOf(GlobalStatus
						.getCmdSendFaileTimes()));

				cmdCount.setText(String.valueOf(GlobalStatus.getCmdSendTimes()));
			}
		});
	}
}
