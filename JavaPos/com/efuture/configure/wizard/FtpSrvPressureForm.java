package com.efuture.configure.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class FtpSrvPressureForm
{
	public Text txtSrvUrl;
	public Text txtPort;
	public Text txtUsr;
	public Text txtSrvFile;
	public Text txtLocalDir;
	public Button btnDir;
	public Text txtPass;
	public  Text txtThdCount;
	public Text txtDelay;
	
	public Label lbcmdCount;
	public Label lbStopCount;
	public Label lbRunCount;
	
	public Table table;
	public Button btnClrTxt;
	public Button btnStopTest;
	public Button btnStartTest;
	public Button btnClose;
	public Shell shell;
	

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	
	public static void main(String[] args)
	{
		try
		{
			PosSrvPressureFrom window = new PosSrvPressureFrom();
			window.open();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open()
	{
		final Display display = Display.getDefault();
		createContents();

		Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
		shell.setLocation((rec.width - shell.getSize().x) / 2, (rec.height - shell.getSize().y) / 2);

		new FtpSrvPressureEvent(this).setSelfRef();
		
		shell.open();

		shell.layout();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell(SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE);
		shell.setLayout(new FormLayout());
		shell.setSize(750, 560);
		//shell.setText("FtpServer压力测试");
		shell.setText(Language.apply("FtpServer压力测试"));

		btnStartTest = new Button(shell, SWT.NONE);
		shell.setDefaultButton(btnStartTest);
		final FormData fd_btn_startTest = new FormData();
		fd_btn_startTest.left = new FormAttachment(0, 9);
		fd_btn_startTest.right = new FormAttachment(0, 109);
		fd_btn_startTest.bottom = new FormAttachment(0, 525);
		fd_btn_startTest.top = new FormAttachment(0, 498);
		btnStartTest.setLayoutData(fd_btn_startTest);
		//btnStartTest.setText("开始测试");
		btnStartTest.setText(Language.apply("开始测试"));

		btnStopTest = new Button(shell, SWT.NONE);
		final FormData fd_btn_stopTest = new FormData();
		fd_btn_stopTest.left = new FormAttachment(0, 117);
		fd_btn_stopTest.right = new FormAttachment(0, 217);
		fd_btn_stopTest.bottom = new FormAttachment(0, 525);
		fd_btn_stopTest.top = new FormAttachment(0, 498);
		btnStopTest.setLayoutData(fd_btn_stopTest);
		//btnStopTest.setText("停止测试");
		btnStopTest.setText(Language.apply("停止测试"));

		btnClrTxt = new Button(shell, SWT.NONE);
		final FormData fd_btn_clrTxt = new FormData();
		fd_btn_clrTxt.top = new FormAttachment(0, 498);
		fd_btn_clrTxt.bottom = new FormAttachment(0, 525);
		fd_btn_clrTxt.right = new FormAttachment(100, -419);
		fd_btn_clrTxt.left = new FormAttachment(100, -519);
		btnClrTxt.setLayoutData(fd_btn_clrTxt);
		//btnClrTxt.setText("清除信息");
		btnClrTxt.setText(Language.apply("清除信息"));
		FormData formData_2;
		formData_2 = new FormData();
		formData_2.right = new FormAttachment(0, 225);

		FormData formData_1;
		formData_1 = new FormData();
		formData_1.left = new FormAttachment(0, 103);

		final FormData formData = new FormData();
		formData.left = new FormAttachment(0, 160);

		final FormData fd_txt_srvUrl = new FormData();
		fd_txt_srvUrl.right = new FormAttachment(0, 283);
		fd_txt_srvUrl.bottom = new FormAttachment(0, 32);
		fd_txt_srvUrl.top = new FormAttachment(0, 9);
		fd_txt_srvUrl.left = new FormAttachment(0, 105);
		//

		final Group group = new Group(shell, SWT.NONE);
//		group.setText("参数设置");
		group.setText(Language.apply("参数设置"));
		final FormData fd_group = new FormData();
		fd_group.bottom = new FormAttachment(0, 118);
		fd_group.right = new FormAttachment(0, 738);
		fd_group.left = new FormAttachment(0, 6);
		fd_group.top = new FormAttachment(0, 5);
		group.setLayoutData(fd_group);
		group.setLayout(new FormLayout());

		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_label = new FormData();
		fd_label.bottom = new FormAttachment(0, 25);
		fd_label.top = new FormAttachment(0, 5);
		fd_label.right = new FormAttachment(0, 100);
		fd_label.left = new FormAttachment(0, 10);
		label.setLayoutData(fd_label);
		//label.setText("服务器地址:");
		label.setText(Language.apply("服务器地址:"));

		txtSrvUrl = new Text(group, SWT.BORDER);
		txtSrvUrl.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final FormData fd_txtSrvUrl = new FormData();
		fd_txtSrvUrl.left = new FormAttachment(0, 102);
		fd_txtSrvUrl.right = new FormAttachment(0, 262);
		fd_txtSrvUrl.bottom = new FormAttachment(0, 28);
		fd_txtSrvUrl.top = new FormAttachment(0, 3);
		txtSrvUrl.setLayoutData(fd_txtSrvUrl);

		final Label label_1 = new Label(group, SWT.NONE);
		final FormData fd_label_1 = new FormData();
		fd_label_1.bottom = new FormAttachment(0, 55);
		fd_label_1.top = new FormAttachment(0, 35);
		fd_label_1.right = new FormAttachment(0, 100);
		fd_label_1.left = new FormAttachment(0, 57);
		label_1.setLayoutData(fd_label_1);
		label_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		//label_1.setText("端口:");
		label_1.setText(Language.apply("端口:"));

		txtPort = new Text(group, SWT.BORDER);
		final FormData fd_txtPort = new FormData();
		fd_txtPort.right = new FormAttachment(0, 262);
		fd_txtPort.bottom = new FormAttachment(0, 59);
		fd_txtPort.top = new FormAttachment(0, 34);
		fd_txtPort.left = new FormAttachment(0, 103);
		txtPort.setLayoutData(fd_txtPort);
		txtPort.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		final Label label_1_1 = new Label(group, SWT.NONE);
		final FormData fd_label_1_1 = new FormData();
		fd_label_1_1.bottom = new FormAttachment(0, 88);
		fd_label_1_1.top = new FormAttachment(0, 68);
		fd_label_1_1.right = new FormAttachment(0, 95);
		fd_label_1_1.left = new FormAttachment(0, 25);
		label_1_1.setLayoutData(fd_label_1_1);
		label_1_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		//label_1_1.setText("目标文件:");
		label_1_1.setText(Language.apply("目标文件:"));

		txtSrvFile = new Text(group, SWT.BORDER);
		final FormData fd_cmbCmd = new FormData();
		fd_cmbCmd.bottom = new FormAttachment(0, 92);
		fd_cmbCmd.top = new FormAttachment(0, 68);
		fd_cmbCmd.right = new FormAttachment(0, 263);
		fd_cmbCmd.left = new FormAttachment(0, 103);
		txtSrvFile.setLayoutData(fd_cmbCmd);
		txtSrvFile.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		Label label_2;
		label_2 = new Label(group, SWT.NONE);
		final FormData fd_label_2 = new FormData();
		fd_label_2.bottom = new FormAttachment(0, 89);
		fd_label_2.top = new FormAttachment(0, 69);
		fd_label_2.right = new FormAttachment(0, 388);
		fd_label_2.left = new FormAttachment(0, 313);
		label_2.setLayoutData(fd_label_2);
		label_2.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		//label_2.setText("存放路径:");
		label_2.setText(Language.apply("存放路径:"));

		btnDir = new Button(group, SWT.NONE);
		final FormData fd_btnDir = new FormData();
		fd_btnDir.bottom = new FormAttachment(0, 93);
		fd_btnDir.top = new FormAttachment(0, 66);
		fd_btnDir.right = new FormAttachment(0, 715);
		fd_btnDir.left = new FormAttachment(0, 616);
		btnDir.setLayoutData(fd_btnDir);
//		btnDir.setText("选择目录");
		btnDir.setText(Language.apply("选择目录"));

		Label label_3;
		label_3 = new Label(group, SWT.NONE);
		final FormData fd_label_3 = new FormData();
		fd_label_3.bottom = new FormAttachment(0, 56);
		fd_label_3.top = new FormAttachment(0, 36);
		fd_label_3.right = new FormAttachment(0, 384);
		fd_label_3.left = new FormAttachment(0, 329);
		label_3.setLayoutData(fd_label_3);
		label_3.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
//		label_3.setText("用户名:");
		label_3.setText(Language.apply("用户名:"));


		txtUsr = new Text(group, SWT.BORDER);
		final FormData fd_txtUsr = new FormData();
		fd_txtUsr.bottom = new FormAttachment(0, 60);
		fd_txtUsr.top = new FormAttachment(0, 35);
		fd_txtUsr.right = new FormAttachment(0, 492);
		fd_txtUsr.left = new FormAttachment(0, 389);
		txtUsr.setLayoutData(fd_txtUsr);
		txtUsr.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		txtPass = new Text(group, SWT.BORDER);
		final FormData fd_txtPass = new FormData();
		fd_txtPass.right = new FormAttachment(0, 716);
		fd_txtPass.bottom = new FormAttachment(0, 61);
		fd_txtPass.top = new FormAttachment(0, 35);
		fd_txtPass.left = new FormAttachment(0, 614);
		txtPass.setLayoutData(fd_txtPass);
		txtPass.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		txtThdCount = new Text(group, SWT.BORDER);
		final FormData fd_txtThdCount = new FormData();
		fd_txtThdCount.bottom = new FormAttachment(0, 28);
		fd_txtThdCount.top = new FormAttachment(0, 3);
		fd_txtThdCount.right = new FormAttachment(0, 491);
		fd_txtThdCount.left = new FormAttachment(0, 389);
		txtThdCount.setLayoutData(fd_txtThdCount);
		txtThdCount.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		txtDelay = new Text(group, SWT.BORDER);
		final FormData fd_txtDelay = new FormData();
		fd_txtDelay.bottom = new FormAttachment(0, 28);
		fd_txtDelay.top = new FormAttachment(0, 3);
		fd_txtDelay.right = new FormAttachment(0, 716);
		fd_txtDelay.left = new FormAttachment(0, 614);
		txtDelay.setLayoutData(fd_txtDelay);
		txtDelay.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		Label label_3_1;
		label_3_1 = new Label(group, SWT.NONE);
		final FormData fd_label_3_1 = new FormData();
		fd_label_3_1.bottom = new FormAttachment(0, 56);
		fd_label_3_1.top = new FormAttachment(0, 36);
		fd_label_3_1.right = new FormAttachment(0, 614);
		fd_label_3_1.left = new FormAttachment(0, 568);
		label_3_1.setLayoutData(fd_label_3_1);
		label_3_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
//		label_3_1.setText("密码:");
		label_3_1.setText(Language.apply("密码:"));

		Label label_3_1_1;
		label_3_1_1 = new Label(group, SWT.NONE);
		final FormData fd_label_3_1_1 = new FormData();
		fd_label_3_1_1.bottom = new FormAttachment(0, 25);
		fd_label_3_1_1.top = new FormAttachment(0, 5);
		fd_label_3_1_1.right = new FormAttachment(0, 385);
		fd_label_3_1_1.left = new FormAttachment(0, 313);
		label_3_1_1.setLayoutData(fd_label_3_1_1);
		label_3_1_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		label_3_1_1.setText(Language.apply("线程数量:"));
//		label_3_1_1.setText("线程数量:");

		Label label_3_1_1_1;
		label_3_1_1_1 = new Label(group, SWT.NONE);
		final FormData fd_label_3_1_1_1 = new FormData();
		fd_label_3_1_1_1.bottom = new FormAttachment(0, 24);
		fd_label_3_1_1_1.top = new FormAttachment(0, 4);
		fd_label_3_1_1_1.right = new FormAttachment(0, 607);
		fd_label_3_1_1_1.left = new FormAttachment(0, 520);
		label_3_1_1_1.setLayoutData(fd_label_3_1_1_1);
		label_3_1_1_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		label_3_1_1_1.setText(Language.apply("延时(毫秒):"));
//		label_3_1_1_1.setText("延时(毫秒):");

		txtLocalDir = new Text(group, SWT.BORDER);
		final FormData fd_txtLocalDir = new FormData();
		fd_txtLocalDir.right = new FormAttachment(0, 615);
		fd_txtLocalDir.left = new FormAttachment(0, 390);
		fd_txtLocalDir.bottom = new FormAttachment(0, 93);
		fd_txtLocalDir.top = new FormAttachment(0, 68);
		txtLocalDir.setLayoutData(fd_txtLocalDir);
		txtLocalDir.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		final Group group_1 = new Group(shell, SWT.NONE);
		final FormData fd_group_1 = new FormData();
		fd_group_1.bottom = new FormAttachment(0, 443);
		fd_group_1.top = new FormAttachment(0, 120);
		fd_group_1.right = new FormAttachment(0, 739);
		fd_group_1.left = new FormAttachment(0, 7);
		group_1.setLayoutData(fd_group_1);
		group_1.setLayout(new FormLayout());
		group_1.setText(Language.apply("测试信息"));
//		group_1.setText("测试信息");

		table= new Table(group_1, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER);
		final FormData fd_table = new FormData();
		fd_table.bottom = new FormAttachment(100, -5);
		fd_table.top = new FormAttachment(0, 5);
		fd_table.right = new FormAttachment(100, -5);
		fd_table.left = new FormAttachment(0, 5);
		table.setLayoutData(fd_table);
		table.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(70);
		newColumnTableColumn.setText(Language.apply("线程ID"));
//		newColumnTableColumn.setText("线程ID");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(120);
		newColumnTableColumn_1.setText(Language.apply("启动时间"));
//		newColumnTableColumn_1.setText("启动时间");

		final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setAlignment(SWT.CENTER);
		newColumnTableColumn_2.setWidth(120);
		newColumnTableColumn_2.setText(Language.apply("命令发送次数"));
//		newColumnTableColumn_2.setText("命令发送次数");

		final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_3.setWidth(120);
		newColumnTableColumn_3.setText(Language.apply("停止时间"));
//		newColumnTableColumn_3.setText("停止时间");

		final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_4.setWidth(280);
		newColumnTableColumn_4.setText(Language.apply("异常信息"));
//		newColumnTableColumn_4.setText("异常信息");

		Group group_2;
		group_2 = new Group(shell, SWT.NONE);
		final FormData fd_group_2 = new FormData();
		fd_group_2.bottom = new FormAttachment(0, 493);
		fd_group_2.top = new FormAttachment(0, 447);
		fd_group_2.right = new FormAttachment(0, 739);
		fd_group_2.left = new FormAttachment(0, 7);
		group_2.setLayoutData(fd_group_2);
		group_2.setLayout(new FormLayout());
		group_2.setText(Language.apply("状态信息"));
//		group_2.setText("状态信息");

		final Label label_4 = new Label(group_2, SWT.NONE);
		final FormData fd_label_4 = new FormData();
		fd_label_4.top = new FormAttachment(0, 6);
		fd_label_4.right = new FormAttachment(0, 117);
		fd_label_4.left = new FormAttachment(0, 7);
		label_4.setLayoutData(fd_label_4);
		label_4.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		label_4.setText(Language.apply("活动线程数量:"));
//		label_4.setText("活动线程数量:");

		final Label label_4_1 = new Label(group_2, SWT.NONE);
		final FormData fd_label_4_1 = new FormData();
		fd_label_4_1.bottom = new FormAttachment(0, 25);
		fd_label_4_1.top = new FormAttachment(0, 8);
		fd_label_4_1.right = new FormAttachment(0, 385);
		fd_label_4_1.left = new FormAttachment(0, 280);
		label_4_1.setLayoutData(fd_label_4_1);
		label_4_1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		label_4_1.setText(Language.apply("停止线程数量:"));
//		label_4_1.setText("停止线程数量:");

		final Label label_4_2 = new Label(group_2, SWT.NONE);
		final FormData fd_label_4_2 = new FormData();
		fd_label_4_2.bottom = new FormAttachment(0, 26);
		fd_label_4_2.top = new FormAttachment(0, 9);
		fd_label_4_2.right = new FormAttachment(0, 625);
		fd_label_4_2.left = new FormAttachment(0, 520);
		label_4_2.setLayoutData(fd_label_4_2);
		label_4_2.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		label_4_2.setText(Language.apply("命令发送次数:"));
//		label_4_2.setText("命令发送次数:");

		lbRunCount = new Label(group_2, SWT.NONE);
		final FormData fd_lbRunCount = new FormData();
		fd_lbRunCount.bottom = new FormAttachment(0, 25);
		fd_lbRunCount.top = new FormAttachment(0, 8);
		fd_lbRunCount.right = new FormAttachment(0, 192);
		fd_lbRunCount.left = new FormAttachment(0, 117);
		lbRunCount.setLayoutData(fd_lbRunCount);
		lbRunCount.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		lbStopCount = new Label(group_2, SWT.NONE);
		final FormData fd_lbStopCount = new FormData();
		fd_lbStopCount.bottom = new FormAttachment(0, 26);
		fd_lbStopCount.top = new FormAttachment(0, 9);
		fd_lbStopCount.right = new FormAttachment(0, 468);
		fd_lbStopCount.left = new FormAttachment(0, 388);
		lbStopCount.setLayoutData(fd_lbStopCount);
		lbStopCount.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		lbcmdCount = new Label(group_2, SWT.NONE);
		final FormData fd_lbcmdcountLabel = new FormData();
		fd_lbcmdcountLabel.bottom = new FormAttachment(0, 26);
		fd_lbcmdcountLabel.top = new FormAttachment(0, 9);
		fd_lbcmdcountLabel.right = new FormAttachment(0, 713);
		fd_lbcmdcountLabel.left = new FormAttachment(0, 628);
		lbcmdCount.setLayoutData(fd_lbcmdcountLabel);
		lbcmdCount.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		btnClose = new Button(shell, SWT.NONE);
		final FormData fd_btnClose = new FormData();
		fd_btnClose.bottom = new FormAttachment(0, 525);
		fd_btnClose.top = new FormAttachment(0, 498);
		fd_btnClose.right = new FormAttachment(0, 740);
		fd_btnClose.left = new FormAttachment(0, 640);
		btnClose.setLayoutData(fd_btnClose);
		btnClose.setText(Language.apply("关闭窗口"));
//		btnClose.setText("关闭窗口");
	}
}
